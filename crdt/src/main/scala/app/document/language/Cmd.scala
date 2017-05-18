package app.document.language

import app.document.language.Var.VarString


sealed trait Cmd extends Product with Serializable

object Cmd{
  final case class Let(x:VarString) extends Cmd
  final case class Assign(v:Val) extends Cmd
  final case class InsertAfter(v:Val) extends Cmd
  final case class Delete() extends Cmd
  final case class Yield() extends Cmd

}