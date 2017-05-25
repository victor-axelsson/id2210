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
  private var executedOperations = List.empty[Timestamp]
  private var localStateAp:Context = new Context(new NodeDoc(new scala.collection.mutable.HashMap[Timestamp, Operation]()))
  private var queue = List.empty[Operation]
  private var variables = new scala.collection.mutable.HashMap[String, Evaluator]()
  private var root:Evaluator = this

  private var receiveBuffer = List.empty[Operation]
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

  private def getId() : Timestamp = {
    new Timestamp(counter, replicaId)
  }

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
        makeDelete(cursor)
      }
      case Cmd.Yield() => {
        throw new NotImplementedError("Not implemented")
      }
    }
    transferStateToRoot()
    this


  }

  def receive(operation: Operation): Unit = {
    receiveBuffer = receiveBuffer :+ operation
    applyRemote()
  }

  def send(): java.util.List[Operation] = {
    var ops: java.util.List[Operation] = new util.ArrayList[Operation]()
    for (op <- sendBuffer)
      ops.add(op)
    return ops
  }

  private def revertAndApplyOperations(op:Operation) = {


    //Start by resetting the local state
    counter = 0
    executedOperations = List.empty[Timestamp]
    localStateAp = new Context(new NodeDoc(new scala.collection.mutable.HashMap[Timestamp, Operation]()))
    sendBuffer = List.empty[Operation]
    cursor = getNewCursor()
    node = null
    var tmpQueue:List[Operation] = queue
    queue = List.empty[Operation]

//    var haveExecuted:Boolean = false
//
//    tmpQueue.foreach((operation:Operation) => {
//      if(op.getId().isGreaterThan(operation.getId()) && !haveExecuted){
//        haveExecuted = true
//        applyLocal(op)
//      }
//
//      applyLocal(operation)
//    })

    tmpQueue = tmpQueue :+ op

    //Offsets
    tmpQueue = resolveConcurrentConflicts(tmpQueue)

    tmpQueue = tmpQueue.sortWith((l:Operation, r:Operation) => {l.getId().isGreaterThan(r.getId())})
    tmpQueue.foreach((operation:Operation) => {applyLocal(operation)})
    println("OK, so I need to revert");
  }

  private def resolveConcurrentConflicts(localQueue : List[Operation]) : List[Operation] = {
    var concurrentModifications : mutable.Map[listT, mutable.Map[Int, mutable.Set[Operation]]] = mutable.Map.empty
    for (op:Operation <- localQueue) {
      if (op.getMutation().isInstanceOf[Insert] && op.getCursor().getKeys().last.isInstanceOf[listT]) {
        var modifiedList = op.getCursor().getKeys().last.asInstanceOf[listT]
        for (execOp <- localQueue) {
          if (execOp.getId() != op.getId() && execOp.getMutation().isInstanceOf[Insert] && execOp.getCursor().getKeys().last.getKey().eq(modifiedList.getKey())) {
            // No causal relation <-> concurrent operations
            if (!execOp.getDeps().contains(op.getId()) && !op.getDeps().contains(execOp.getId())) {
              if (!concurrentModifications.contains(modifiedList)) {
                concurrentModifications += modifiedList -> new mutable.HashMap[Int, mutable.Set[Operation]]()
              }

              //Add operation at current replica
              if (!concurrentModifications(modifiedList).contains(execOp.getId().getP())) {
                concurrentModifications(modifiedList) += execOp.getId().getP() -> new mutable.HashSet[Operation]()
              }
              concurrentModifications(modifiedList)(execOp.getId().getP()) += execOp

              //Add operation at remote replica
              if (!concurrentModifications(modifiedList).contains(op.getId().getP())) {
                concurrentModifications(modifiedList) += op.getId().getP() -> new mutable.HashSet[Operation]()
              }
              concurrentModifications(modifiedList)(op.getId().getP()) += op
            }
          }
        }
      }
    }

    var offsets : mutable.Map[Timestamp, Int] = mutable.Map.empty
    if (concurrentModifications.nonEmpty) {
      for (modificationMap <- concurrentModifications.values) {
        for (replicaId <- modificationMap.keys) {
          for (otherReplicaId <- modificationMap.keys) {
            if (replicaId > otherReplicaId) {
              for (operation <- modificationMap(replicaId)) {

                if (offsets.contains(operation.getId())) {
                  offsets(operation.getId()) = offsets(operation.getId()) + modificationMap(otherReplicaId).size
                } else {
                  offsets += operation.getId() -> modificationMap(otherReplicaId).size
                }

              }
            }
          }
        }
      }
    }

    var result : List[Operation] = List.empty
    for (operation <- localQueue) {
      if (!offsets.contains(operation.getId())) {
        result = result :+ operation
      } else {
        var nTimestamp = new Timestamp(operation.getId().getC() + offsets(operation.getId()), operation.getId().getP())
        nTimestamp.preserveHashCode(operation.getId())
        var nOp = new Operation(nTimestamp, operation.getDeps(), operation.getCursor(), operation.getMutation())
        result = result :+ nOp
      }
    }
    return result
  }

  private def isConcurrent(op1:Operation, op2:Operation): Boolean = {
    op1.getId().isConcurrent(op2.getId())
  }

  private def isConcurrentWithAny(op:Operation):Boolean = {

    var isC:Boolean = false

    queue.foreach((operation:Operation)  => {
      if(isConcurrent(op, operation)){
        isC = true
      }
    })

    isC
  }

  private def applyRemote(): Unit = {
    for (op <- receiveBuffer) {
      if (!(executedOperations.contains(op.getId()) || executedOperations.contains(op.getId().saved))) {
        //1. determine if any concurrect insertAfter for a list
        //2. determine priority for these inserts - either change received indexes or not

        if(queue.size > 0){
          val lastOp:Operation = queue(queue.size -1)

          if(op.getId().isLessThan(lastOp.getId())){
            revertAndApplyOperations(op)
          }else{
            applyLocal(op)
          }
        }else{
          applyLocal(op)
        }


        println(getId() + " " +this.toJsonString())
      }
    }
    receiveBuffer = List.empty
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
    //counter += 1
    val op = new Operation(getId(), executedOperations, cursor, mutation)
    applyLocal(op)
    op
  }

  private def applyLocal(op:Operation) = {
    //Increase Lamport TS
    counter = Math.max(op.getId().getC(), counter) + 1

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
