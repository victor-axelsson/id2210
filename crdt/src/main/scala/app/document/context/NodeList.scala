package app.document.context

import app.document.evaluator.Operation

import scala.annotation.tailrec

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeList(name:String, pres:Map[Int, Operation]) extends Node(name, pres){

  var children : List[Node] = List.empty

  def insertAt(index: Int, node: Node) = {


    @tailrec
    def addAtIndex(i:Int, counter:Int, index:Int, n:Node, acc:List[Node], childs:List[Node]):List[Node] = {

      //Base case
      if(i >= childs.size){

        if(i == 0){
          return acc :+ n
        }

        return acc
      }

      //Get the current node
      val curr:Node = childs(i)
      var nextI:Int = i
      var nextCounter:Int = counter
      var nextAcc:List[Node] = acc

      //if it's a tombstone, just continue
      if(curr.isTombstone()){
         nextAcc = acc :+ curr
         nextI = i+1
      }else if(counter == index){
          //we found the index
         nextAcc = acc :+ n
         nextAcc = acc :+ curr
         nextI = i+1
         nextCounter = counter+1
      }else{
        nextAcc = acc :+ curr
        nextI = i+1
        nextCounter = counter+1
      }

      //Just continue
      return addAtIndex(nextI, nextCounter, index, n, acc, childs)
    }

    children = addAtIndex(0, 0, index, node, List.empty, children)
  }

  override def getChildren(): List[Node] = {
    children
  }

  override def toString() = {
    val builder : StringBuilder = new StringBuilder()
    builder.append('"').append(name).append('"').append(":[")
    var counter = 0
    for (child <- getChildren()) {
      counter+=1
      builder.append(child.toString())
      if (counter < getChildren().size) {
        builder.append(",")
      }
    }
    builder.append("]")
    builder.toString()
  }

  override def addChild(node: Node) = {
    throw new Exception("You should use the insertAt(index, Node) function to add children");
  }
}
