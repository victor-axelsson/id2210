package app.document

import scala.collection.mutable


/**
  * Created by victoraxelsson on 2017-05-03.
  */
class Doc extends TMap {

  override def getType(): String = "doc"

  override def toString: String = {
    val result = new mutable.StringBuilder()
    result.append("{")

    //if (content.nonEmpty)
    //  result.append("\n")
    var counter = 0
    for ((k,v) <- content) {
      result.append("\"").append(k).append("\": ")
      result.append(v.toString())
      counter+=1
      if (counter > 0 && counter < content.size) result.append(", ")
      //result.append("\n")
    }
    result.append("}")
    return result.toString()
  }
}
