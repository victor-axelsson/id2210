package app.document.evaluator


import app.document.context._
import app.document.cursor.Cursor
import app.document.cursor.Key.{RootMapT, listT, mapT, regT}
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.language.Expr._
import app.document.language.{Expr, Val}

import scala.annotation.tailrec

/**
  * Created by Nick on 5/3/2017.
  */
case class Evaluator(replicaId : Int) {

  private var counter = 0
  private var executedOperations = List.empty[Int]
  private var localStateAp:Context = new Context(new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]()))
  private var queue = List.empty[Operation]

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
    eval
  }

  def toCursor() = {
    cursor
  }

  private def getId() : Int = counter * replicaId

  def evalExpr(expr: Expr):Evaluator = {

    var eval:Evaluator = getClone()


    expr match {
      case Get(nextExpr, key) => {

        if(nextExpr.isInstanceOf[Doc]){
         // eval.cursor = eval.cursor.append(new RootMapT(nextExpr.asInstanceOf[Doc]))
          eval.node = eval.localStateAp.doc
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

        eval.node = find(eval.node.getChildren())

        if(eval.node == null){
          throw new Exception("Key now found")
        }

        if(eval.node.isInstanceOf[NodeMap]){
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
      case Idx(nextExpr, index) => {
        //TODO: stuff
        eval
      }
      case Keys(nextExpr) => {
        //TODO: stuff
        eval
      }
      case Values(nextExpr) => {
        //TODO: stuff
        null
      }
      case _ => {
        //TODO: stuff
        eval
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

}
