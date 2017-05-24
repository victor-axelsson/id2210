package app.document.context

import app.document.evaluator.Operation

import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeDoc(pres:mutable.Map[Timestamp, Operation]) extends Node("doc", pres){

  var children:mutable.HashMap[String, Node] = new mutable.HashMap[String, Node]()

  override def getChildren(): List[Node] = {
    children.values.toSeq.toList
  }

  override def toString: String = {
    val builder : mutable.StringBuilder = new mutable.StringBuilder()
    builder.append("{").append("\"").append("doc").append('"').append(":").append("{")
    var counter = 0
    for (child <- children.values) {
      counter += 1

      builder.append(child.toString())

      if (counter < children.size) builder.append(",")
    }
    builder.append("}}")
    builder.toString()
  }

  override def addChild(node: Node) = {
    children += node.getName() -> node
  }
}
