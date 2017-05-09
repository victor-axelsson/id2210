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

}
