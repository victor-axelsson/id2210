package app.document.evaluator


import app.document.context._
import app.document.cursor.Cursor
import app.document.cursor.Key._
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.language.Expr._
import app.document.language.{Cmd, Expr, Val}

import scala.annotation.tailrec

/**
  * Created by Nick on 5/3/2017.
  */
case class Evaluator(replicaId : Int) {

  private var counter = 0
  private var executedOperations = List.empty[Int]
  private var localStateAp:Context = new Context(new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]()))
  private var queue = List.empty[Operation]
  private var variables = new scala.collection.mutable.HashMap[String, Evaluator]()

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
        //TODO: stuff
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
  }

  def makeInsert(cursor : Cursor, value : Val) = makeOperation(cursor, Insert(value))

  def makeAssign(cursor : Cursor, value : Val) = makeOperation(cursor, Assign(value))

  def makeDelete(cursor : Cursor) = makeOperation(cursor, Delete())

  def toJsonString():String = localStateAp.doc.toString()
}
