package app.document.context

/**
  * Created by victoraxelsson on 2017-05-09.
  */
abstract class Node(name:String, var pres:Set[String]) {

  def getName() = name
  def getPres() = pres
  def removeKeyPresence(key: String) = {
    pres = pres - key
  }
  def addKeyPresence(key:String) = {
    pres = pres + key
  }

  def getChildren() :List[Node]

  override def toString() : String
}
