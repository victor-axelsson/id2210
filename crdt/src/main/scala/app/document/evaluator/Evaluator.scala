package app.document.evaluator

import app.document.cursor.Cursor
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.language.Val

/**
  * Created by Nick on 5/3/2017.
  */
class Evaluator(replicaId : Int) {

  private var counter = 0
  private val executedOperations = List.empty[Int]

  private def getId() : Int = counter * replicaId

  private def makeOperation(cursor : Cursor, mutation: Mutation) : Operation = {
    counter += 1;
    var op = new Operation(getId(), executedOperations, cursor, mutation)
    applyLocal(op.getId(), op.getDeps(), op.getCursor(), op.getMutation())
    op
  }

  private def applyLocal(id :Int, deps:List[Int], cursor: Cursor, mutation:Mutation) = {
    //1, modify local state


    //2, add to operation queue
    

    //3, add id to set of processed operations
    executedOperations :+ id
  }

  def makeInsert(cursor : Cursor, value : Val) = makeOperation(cursor, Insert(value))

  def makeAssign(cursor : Cursor, value : Val) = makeOperation(cursor, Assign(value))

  def makeDelete(cursor : Cursor) = makeOperation(cursor, Delete())

}
