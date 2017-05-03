package app.document.cursor

import app.document.language.Expr

sealed trait Key extends Product with Serializable

object Key{
  final case class RootMapT(doc: Expr.Doc) extends Key
  final case class mapT(key: String) extends Key
  final case class listT(key: String) extends Key
}