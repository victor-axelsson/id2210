package app.document.language


sealed trait Expr extends Product with Serializable

object Expr{
  final case class Doc() extends Expr
  final case class Var (name: Var) extends Expr

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