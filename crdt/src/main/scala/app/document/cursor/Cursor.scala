package app.document.cursor

/**
  * Created by Nick on 5/3/2017.
  */
class Cursor(keys : List[Key], id : Key) {
  def append(key : Key) : Cursor = {
    new Cursor(keys :+ id, key)
  }

  def getKeys() : List[Key] = keys

  def getTail() : Key = id


}
