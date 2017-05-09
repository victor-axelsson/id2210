package app.document.context

import app.document.language.Val

import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeReg(name:String, values : List[Val], pres:Set[String]) extends Node(name, pres){

  override def getChildren(): List[Node] = {
    null
  }

  override def toString: String = {
    val builder : mutable.StringBuilder = new mutable.StringBuilder()
    builder.append('"').append(name).append('"').append(" : ").append('"')
    for (value <- values) {
      builder.append(value)
      if (!value.eq(values.last)) {
        builder.append(",")
      }
    }
    builder.append('"').toString()
  }
}
