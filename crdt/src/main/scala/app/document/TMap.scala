package app.document

import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-03.
  */
class TMap extends DocItem{
  var content : mutable.Map[String, DocItem] = new mutable.HashMap[String, DocItem]()

  override def get(key: String): DocItem = {
    if (content.contains(key)) {
      return content(key)
    }
    else {
      val newElement = new TReg()
      content.put(key, newElement)
      return newElement
    }
  }

  override  def getType(): String = "tmap"
}
