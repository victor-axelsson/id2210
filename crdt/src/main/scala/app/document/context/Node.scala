package app.document.context

/**
  * Created by victoraxelsson on 2017-05-09.
  */
abstract class Node(name:String, pres:Set[String]) {

  def getName() = name
  def getPres() = pres

  def getChildren() :List[Node]
}
