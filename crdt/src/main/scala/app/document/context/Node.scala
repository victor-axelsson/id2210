package app.document.context

import app.document.evaluator.Operation

import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-09.
  */
abstract class Node(var name:String, var pres:mutable.Map[Timestamp, Operation]) {
  var tombstone = false

  def isTombstone() = tombstone

  //TODO call when is tombstone
  def setTombstone(t: Boolean) = tombstone = t

  def getName() = name
  def setName(n:String) = {
    this.name = n
  }

  def getPres() = pres
  def removeKeyPresence(id : Timestamp) = {
    pres -= id
  }
  def addKeyPresence(op : Operation) = {
    pres += op.getId() -> op
  }

  def getChildren() :List[Node]
  def addChild(node: Node)

  override def toString() : String
}
