package app.document.context

import app.document.evaluator.Operation

import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeMap(name:String, pres:Map[Int, Operation]) extends Node(name, pres){

  var children:mutable.HashMap[String, Node] = new mutable.HashMap[String, Node]()

  override def getChildren(): List[Node] = {
    children.values.toSeq.toList
  }

  override def toString: String = {
    val builder : mutable.StringBuilder = new mutable.StringBuilder()
    builder.append('"').append(name).append('"').append("{\n")
    var counter = 0
    for (child <- children.values) {
      counter += 1

      builder.append(child.toString())

      if (counter < children.size) builder.append(", ")
    }
    builder.append("\n}")
    builder.toString()
  }
}
