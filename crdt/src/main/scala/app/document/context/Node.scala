package app.document.context

import app.document.evaluator.Operation

/**
  * Created by victoraxelsson on 2017-05-09.
  */
abstract class Node(name:String, var pres:Map[Int, Operation]) {

  def getName() = name
  def getPres() = pres
  def removeKeyPresence(id : Int) = {
    pres -= id
  }
  def addKeyPresence(op : Operation) = {
    pres += op.getId() -> op
  }

  def getChildren() :List[Node]

  override def toString() : String
}
