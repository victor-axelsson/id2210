package app.document.evaluator

import app.document.context.{Context, NodeDoc}
import app.document.cursor.Cursor
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.language.Expr.{Get, Idx, Keys, Values}
import app.document.language.{Expr, Val}

/**
  * Created by Nick on 5/3/2017.
  */
class Evaluator(replicaId : Int) {

  private var counter = 0
  private var executedOperations = List.empty[Int]
  private var localStateAp:Context = new Context(new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]()))
  private var queue = List.empty[Operation]

  private def getId() : Int = counter * replicaId

  def evalExpr(expr: Expr):Cursor = {

    expr match {
      case Get(nextExpr, key) => {
        //TODO: stuff
        null
      }
      case Idx(nextExpr, index) => {
        //TODO: stuff
        null
      }
      case Keys(nextExpr) => {
        //TODO: stuff
        null
      }
      case Values(nextExpr) => {
        //TODO: stuff
        null
      }
      case _ => {
        //TODO: stuff
        null
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
