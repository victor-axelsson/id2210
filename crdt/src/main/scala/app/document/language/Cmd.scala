package app.document.language


sealed trait Cmd extends Product with Serializable

object Cmd{
  final case class Let(x:Var, expr:Expr)
  final case class Assign(expr:Expr, v:Val)
  final case class InsertAfter(expr:Expr, v:Val)
  final case class Delete(expr:Expr)
  final case class Yield()

}