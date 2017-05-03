package app.document.language


sealed trait Var extends Product with Serializable

object Var{
  final class Var(s:String)
}