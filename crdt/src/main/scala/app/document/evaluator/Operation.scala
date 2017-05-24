package app.document.evaluator

import app.document.context.Timestamp
import app.document.cursor.Cursor

/**
  * Created by Nick on 5/9/2017.
  */
class Operation(id: Timestamp, deps : List[Timestamp], cursor: Cursor, mutation: Mutation) {
  def getId() = id
  def getDeps() = deps
  def getCursor() = cursor
  def getMutation() = mutation
}
