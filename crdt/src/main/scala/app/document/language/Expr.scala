package app.document.language

import app.document.language.Var.VarString


sealed trait Expr extends Product with Serializable

object Expr{
  final case class Doc() extends Expr
  final case class Var (name: VarString) extends Expr {
    def getName() = name.getName()
  }
  final case class Get (key:String) extends Expr
  final case class Idx(i:Int) extends Expr
  final case class Keys() extends Expr
  final case class Values() extends Expr
}