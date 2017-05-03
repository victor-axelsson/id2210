package app.document.evaluator

import app.document.language.Val

/**
  * Created by Nick on 5/3/2017.
  */
sealed trait Mutation

object Mutation {
  final case class Assign(value : Val) extends Mutation
  final case class Insert(value : Val) extends Mutation
  final case class Delete() extends Mutation
}
