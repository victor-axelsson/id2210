package app.document.evaluator

import app.document.cursor.Cursor
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.language.Val

/**
  * Created by Nick on 5/3/2017.
  */
class Evaluator(replicaId : Int) {

  private var counter = 1
  private val executedOperations = List.empty[Int]

  private def getId() : Int = counter * replicaId

  private def makeOperation(cursor : Cursor, mutation: Mutation) = {
    (getId(), executedOperations, cursor, mutation)
  }

  def makeInsert(cursor : Cursor, value : Val) = makeOperation(cursor, Insert(value))

  def makeAssign(cursor : Cursor, value : Val) = makeOperation(cursor, Assign(value))

  def makeDelete(cursor : Cursor) = makeOperation(cursor, Delete())

}
