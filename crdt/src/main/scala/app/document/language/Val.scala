package app.document.language


sealed trait Val extends Product with Serializable

object Val{
  final case class Number(n:Int) extends Val {
     def getVal(): Int = n
  }
  final case class Str(str:String) extends Val {
     def getVal(): String = str
  }
  case object True extends Val
  case object False extends Val
  case object Null extends Val
  case object EmptyMap extends Val
  case object EmptyList extends Val
}