package app.document.context

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeList(name:String, pres:Set[String]) extends Node(name, pres){


  override def getChildren(): List[Node] = {
    null
  }

  override def toString() = {
    val builder : StringBuilder = new StringBuilder()
    builder.append('"').append(name).append('"').append(" : [")
    var counter = 0
    for (child <- getChildren()) {
      counter+=1
      builder.append(child.toString())
      if (counter < getChildren().size) {
        builder.append(", ")
      }
    }
    builder.append("]")
    builder.toString()
  }
}
