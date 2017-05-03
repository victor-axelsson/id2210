package app.document.language

import app.document.language.Var.VarString


sealed trait Cmd extends Product with Serializable

object Cmd{
  final case class Let(x:VarString, expr:Expr)
  final case class Assign(expr:Expr, v:Val)
  final case class InsertAfter(expr:Expr, v:Val)
  final case class Delete(expr:Expr)
  final case class Yield()

}