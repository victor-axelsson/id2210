package app.document.language


sealed trait Val extends Product with Serializable

object Val{
  final case class Number(n:Int)
  final case class String(str:String)
  case object True
  case object False
  case object Null
  case object EmptyMap
  case object EmptyList
}