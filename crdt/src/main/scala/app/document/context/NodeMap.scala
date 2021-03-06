package app.document.context

import app.document.evaluator.Operation

import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeMap(theName:String, pres:mutable.Map[Timestamp, Operation]) extends Node(theName, pres){

  var children:mutable.HashMap[String, Node] = new mutable.HashMap[String, Node]()

  override def getChildren(): List[Node] = {
    children.values.toSeq.toList.filter((n:Node) => {!n.isTombstone()})
  }

  override def toString: String = {
    val builder : mutable.StringBuilder = new mutable.StringBuilder()
    builder.append('"').append(name).append('"').append(":").append("{")
    var counter = 0
    for (child <- children.values) {
      counter += 1
      if (!child.isTombstone()) {

        builder.append(child.toString())

        if (counter < getChildren().size) builder.append(",")
      }
    }
    builder.append("}")
    builder.toString()
  }

  override def addChild(node: Node) = {
    children += node.getName() -> node
  }
}
