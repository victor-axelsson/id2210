package app.document.language

sealed trait Expr extends Product with Serializable

object Expr{
  final case class Doc() extends Expr
  final case class Var (name: String) extends Expr {
    def getName() = name
  }
  final case class Get (key:String) extends Expr
  final case class Idx(i:Int) extends Expr
  final case class Keys() extends Expr
  final case class Values() extends Expr
}