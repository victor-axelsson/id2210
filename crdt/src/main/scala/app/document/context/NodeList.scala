package app.document.context

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeList(name:String, pres:Set[String]) extends Node(name, pres){


  override def getChildren(): List[Node] = {
    null
  }
}
