package app.document.language


sealed trait Expr extends Product with Serializable

object Expr{
  case object Doc extends Expr
  final case class Var (name: Var) extends Expr
  final case class Get (expr: Expr, key:String) extends Expr
  final case class Idx(expr: Expr, i:Int) extends Expr
  final case class Keys(expr: Expr) extends Expr
  final case class Values(expr: Expr) extends Expr
}