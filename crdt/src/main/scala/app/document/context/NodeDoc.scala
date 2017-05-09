package app.document.context

import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeDoc(pres:Set[String]) extends Node("doc", pres){

  var children:mutable.HashMap[String, Node] = new mutable.HashMap[String, Node]()

  override def getChildren(): List[Node] = {
    children.values.toSeq.toList
  }

  override def toString: String = {
    var builder : mutable.StringBuilder = new mutable.StringBuilder()
    builder.append("{\n")
    var counter = 0
    for (child <- children.values) {
      counter += 1

      builder.append(child.toString())

      if (counter < children.size) builder.append(",\n")
    }
    builder.append("\n}")
    builder.toString()
  }
}
