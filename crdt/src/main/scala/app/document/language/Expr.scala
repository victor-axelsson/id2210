package app.document.language

import app.document.language.Var.VarString


sealed trait Expr extends Product with Serializable

object Expr{
  final case class Doc() extends Expr
  final case class Var (name: VarString) extends Expr {
    def getName() = name.getName()
  }
  final case class Get (expr: Expr, key:String) extends Expr {
    def getTheExpr() :Expr = expr
  }
  final case class Idx(expr: Expr, i:Int) extends Expr {
    def getTheExpr() :Expr = expr
  }
  final case class Keys(expr: Expr) extends Expr {
    def getTheExpr() :Expr = expr
  }
  final case class Values(expr: Expr) extends Expr {
    def getTheExpr() :Expr = expr
  }
}