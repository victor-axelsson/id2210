package app.document.language


sealed trait Var extends Product with Serializable

object Var{
  final class VarString(s:String)
}