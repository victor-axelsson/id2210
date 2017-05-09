package app.document.cursor

import app.document.language.Expr

sealed trait Key extends Product with Serializable{
  def getKey() :String
}

object Key{
  final case class RootMapT(doc: Expr.Doc) extends Key{
    override def getKey(): String = "doc"
  }
  final case class mapT(key: String) extends Key {
    override def getKey(): String = key
  }
  final case class listT(key: String) extends Key {
    override def getKey(): String = key
  }
  final case class regT(key: String) extends Key {
    override def getKey(): String = key
  }
}