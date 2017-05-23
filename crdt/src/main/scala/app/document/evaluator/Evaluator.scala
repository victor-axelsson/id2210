package app.document.evaluator


import java.util

import app.document.context._
import app.document.cursor.Cursor
import app.document.cursor.Key.{mapT, _}
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.language.Expr._
import app.document.language.{Cmd, Expr, Val}

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * Created by Nick on 5/3/2017.
  */
case class Evaluator(replicaId : Int) {

  private var counter = 0
  private var executedOperations = List.empty[Int]
  private var localStateAp:Context = new Context(new NodeDoc(new scala.collection.mutable.HashMap[Int, Operation]()))
  private var queue = List.empty[Operation]
  private var variables = new scala.collection.mutable.HashMap[String, Evaluator]()
  private var root:Evaluator = this

  private var receiveBuffer = mutable.ArrayBuffer.empty[Object]
  private var sendBuffer = List.empty[Operation]

  private var cursor:Cursor = getNewCursor()
  var node:Node = null;


  private def getNewCursor() :Cursor = {
    new Cursor(scala.collection.immutable.List(), RootMapT(Expr.Doc()))
  }

  private def getClone() :Evaluator = {
    val eval:Evaluator = copy(this.replicaId)
    eval.localStateAp = this.localStateAp
    eval.counter = this.counter
    eval.executedOperations = this.executedOperations
    eval.queue = this.queue
    eval.cursor = this.cursor
    eval.node = this.node
    eval.variables = this.variables
    eval.root = this.root
    eval.receiveBuffer = this.receiveBuffer
    eval.sendBuffer = this.sendBuffer
    eval
  }

  def toCursor() = {
    cursor
  }

  def toKeys():scala.collection.mutable.Set[String] = {

    @tailrec
    def find(keys:scala.collection.mutable.Set[String], childs:List[Node]):scala.collection.mutable.Set[String] = {

      if(childs.size <= 0){
        return keys
      }

      keys += childs.head.getName()

      find(keys, childs.tail)
    }

    //If no cursor, return empty set
    if(node == null){
      return scala.collection.mutable.Set[String]()
    }

    find(scala.collection.mutable.Set[String](), node.getChildren())
  }

  def toVals():List[Val] = {

    if(!node.isInstanceOf[NodeReg]){
      throw new Exception("You can only use values() on a regT");
    }

    node.asInstanceOf[NodeReg].getValues()
  }

  private def getId() : Int = counter * replicaId

  def isAllDigits(x: String) = x forall Character.isDigit

  def evalExpr(expr: Expr):Evaluator = {

    var eval:Evaluator = getClone()

    expr match {
      case Get(key) => {

        if(eval.node == null){
          throw new Exception("you need to select the doc first")
        }

        @tailrec
        def find(childs:List[Node]):Node = {

          if(childs.size <= 0){
            return null
          }

          if(childs.head.getName() == key){
            return childs.head
          }else{
            find(childs.tail)
          }
        }

        val prevN = eval.node
        eval.node = find(eval.node.getChildren())

        if(eval.node == null){
          eval.cursor = eval.cursor.append(new identifierT(key))
          eval.node = prevN
        }else if(eval.node.isInstanceOf[NodeMap]){
          eval.cursor = eval.cursor.append(new mapT(key))
        }else if(eval.node.isInstanceOf[NodeList]) {
          eval.cursor = eval.cursor.append(new listT(key))
        }else if(eval.node.isInstanceOf[NodeReg]){
          eval.cursor = eval.cursor.append(new regT(key))
        }else{
          throw new Exception("Key was not of right type")
        }

        eval
      }
      case Idx(index) => {

        if(eval.node == null){
          throw new Exception("you need to select the doc first")
        }

        eval.cursor.getId() match  {
          case mapT(_) => {
            throw new Exception("You cannot assign an index to a map")
          }
          case regT(_) => {
            throw new Exception("You cannot assign an index to a register")
          }
          case identifierT(key) => {
            eval.cursor = eval.cursor.appendAsList(new identifierT(index + ""))
          }
          case listT(key) => {
            eval.cursor = eval.cursor.append(new identifierT(index + ""))
          }
        }

        eval
      }
      case Keys() => {
        //this is handled by the toKeys function
        eval
      }
      case Values() => {
        //this is handled by the toValues function
        eval
      }
      case Var(name) => {
        variables(name)
      }
      case Doc() => {
        val newEval:Evaluator = getClone()
        newEval.node = eval.localStateAp.doc
        newEval
      }
      case _ => {
        //TODO: stuff
        eval
      }
    }
  }

  def evalCmd(cmd: Cmd) = {
    cmd match {
      case Cmd.Let(name) => {

        //Take a snapshot if the state
        val eval = getClone()
        variables += name -> eval
      }
      case Cmd.Assign(value) => {
        makeAssign(cursor, value)
      }
      case Cmd.InsertAfter(value) => {
        makeInsert(cursor, value)
      }
      case Cmd.Delete() => {

      }
      case Cmd.Yield() => {

      }
    }
    transferStateToRoot()
    this


  }

  def receive(id: Int, operation: Operation): Unit = {
    if (!receiveBuffer.contains((id, operation))) {
      receiveBuffer = receiveBuffer :+ (id, operation)
      applyRemote()
    }
  }

  def send(): java.util.List[Operation] = {
    var ops: java.util.List[Operation] = new util.ArrayList[Operation]()
    for (op <- sendBuffer)
      ops.add(op)
    return ops
  }

  private def applyRemote(): Unit = {
    //For every list modification construct a tree map of replicaId to List of concurrent operations
    var concurrentModifications : mutable.Map[listT, mutable.Map[Int, mutable.Set[Operation]]] = mutable.Map.empty
    for ((id:Int, op:Operation) <- receiveBuffer) {
      if (op.getMutation().isInstanceOf[Insert] && op.getCursor().getKeys().last.isInstanceOf[listT]) {
        var modifiedList = op.getCursor().getKeys().last.asInstanceOf[listT]
        for (execOp <- queue) {
          if (execOp.getId() != op.getId() && execOp.getMutation().isInstanceOf[Insert] && execOp.getCursor().getKeys().last.getKey().eq(modifiedList.getKey())) {
            // No causal relation <-> concurrent operations
            if (!execOp.getDeps().contains(op.getId()) && !op.getDeps().contains(execOp.getId())) {
              if (!concurrentModifications.contains(modifiedList)) {
                concurrentModifications += modifiedList -> new mutable.HashMap[Int, mutable.Set[Operation]]()
              }

              //Add operation at current replica
              if (!concurrentModifications(modifiedList).contains(getId())) {
                concurrentModifications(modifiedList) += getId() -> new mutable.HashSet[Operation]()
              }
              concurrentModifications(modifiedList)(getId()) += execOp

              //Add operation at remote replica
              if (!concurrentModifications(modifiedList).contains(id)) {
                concurrentModifications(modifiedList) += id -> new mutable.HashSet[Operation]()
              }
              concurrentModifications(modifiedList)(id) += op
            }
          }
        }
      }
    }

    // If there are concurrent modifications -> repairConflicts by modification of indexes of inserts
    if (concurrentModifications.nonEmpty) {
      for (conflictMap <- concurrentModifications.values) {
        var keys = conflictMap.keySet.toList

        for (first:Int <- 0 to keys.size - 1) {
          var replicaKey = keys(first)
          for (second:Int <- 0 to keys.size - 1) {
            var otherReplicaKey = keys(second)
            if (replicaKey < otherReplicaKey) {
              for (i:Int <- 0 to receiveBuffer.length - 1) {
                var (id:Int, opInReceived:Operation) = receiveBuffer(i)
                if (conflictMap.contains(opInReceived.getId())) {
                  receiveBuffer.remove(i)

                  var cursor = opInReceived.getCursor()
                  var oldId = cursor.id.asInstanceOf[identifierT]
                  var newId = new identifierT(Integer.parseInt(oldId.key) + conflictMap(otherReplicaKey).size + "")

                  cursor.id = newId

                  receiveBuffer.insert(i, (id, new Operation(opInReceived.getId(), opInReceived.getDeps(), cursor
                    , opInReceived.getMutation())))
                }
              }
            }
          }
        }
      }
    }

    for ((id:Int, op:Operation) <- receiveBuffer) {
      if (!executedOperations.contains(op.getId())) {
        applyLocal(op)
        println(getId() + " " +this.toJsonString())
      }
    }
  }

  private def transferStateToRoot(): Unit = {
    root.executedOperations = this.executedOperations
    root.queue = this.queue
    root.localStateAp = this.localStateAp
    root.counter = this.counter
    root.sendBuffer = this.sendBuffer
    root.receiveBuffer = this.receiveBuffer
  }


  private def makeOperation(cursor : Cursor, mutation: Mutation) : Operation = {
    counter += 1
    val op = new Operation(getId(), executedOperations, cursor, mutation)
    applyLocal(op)
    op
  }

  private def applyLocal(op:Operation) = {
    //1, modify local state
    val newLocalStateApPrime:Context = localStateAp.apply(op)
    localStateAp = newLocalStateApPrime

    //2, add to operation queue
    queue = queue :+ op

    //3, add id to set of processed operations
    executedOperations = executedOperations :+ op.getId()

    //4, add it to outgoing sendBuffer
    sendBuffer = sendBuffer :+ op
  }

  def makeInsert(cursor : Cursor, value : Val) = makeOperation(cursor, Insert(value))

  def makeAssign(cursor : Cursor, value : Val) = makeOperation(cursor, Assign(value))

  def makeDelete(cursor : Cursor) = makeOperation(cursor, Delete())

  def toJsonString():String = localStateAp.doc.toString()
}
