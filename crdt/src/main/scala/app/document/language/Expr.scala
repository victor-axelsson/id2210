package app.document.language

import app.document.Doc


sealed trait Expr extends Product with Serializable

object Expr{
  case object Doc extends Doc
  final case class Var (name: Var)
  final case class Get (expr: Expr, key:String)
  final case class Idx(expr: Expr, i:Int)
  final case class Keys(expr: Expr)
  final case class Values(expr: Expr)
}