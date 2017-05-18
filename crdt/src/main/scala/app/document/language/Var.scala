package app.document.language


sealed trait Var extends Product with Serializable

object Var{
  final case class VarString(s:String) extends Var{
    def getName() = s
  }
}