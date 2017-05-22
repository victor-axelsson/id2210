package app.document.context

import app.document.evaluator.Operation

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeList(theName:String, pres:mutable.Map[Int, Operation]) extends Node(theName, pres){

  var children : List[Node] = List.empty

  def insertAt(index: Int, node: Node) = {


    @tailrec
    def addAtIndex(i:Int, counter:Int, index:Int, n:Node, acc:List[Node], childs:List[Node]):List[Node] = {

      //If its empty
      if(childs.size == 0){
          return acc :+ n
      }

      //Base case
      if(i > childs.size){
        return acc
      }

      var curr:Node = null

      if(i < childs.size){
        curr = childs(i)
      }

      //Get the current node
      var nextI:Int = i
      var nextCounter:Int = counter
      var nextAcc:List[Node] = acc

      //if it's a tombstone, just continue
      if(curr != null && curr.isTombstone()){
         nextAcc = acc :+ curr
         nextI = i+1
      }else if(counter == index){
          //we found the index
         nextAcc = acc :+ n

         if(curr != null){
            nextAcc = nextAcc :+ curr
         }

         nextI = i+1
         nextCounter = counter+1
      }else{
        if(curr != null){
          nextAcc = acc :+ curr
        }
        nextI = i+1
        nextCounter = counter+1
      }

      //Just continue
      return addAtIndex(nextI, nextCounter, index, n, nextAcc, childs)
    }

    children = addAtIndex(0, 0, index, node, List.empty, children)
    reIndexChildren()
  }

  def reIndexChildren() = {
    var counter:Int = 0
    children.foreach((n:Node) => {
      if(!n.isTombstone()){
          n.name = "["+counter+"]"
          counter += 1
      }
    })

    children
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
