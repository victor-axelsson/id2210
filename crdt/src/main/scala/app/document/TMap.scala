package app.document

import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-03.
  */
class TMap extends DocItem{
  var content : mutable.Map[String, DocItem] = new mutable.HashMap[String, DocItem]()

  override def get(key: String): TReg = {

    if (content.contains(key)) {
      var elem = content(key)
      elem match {
        case reg : TReg => { println("reg") }
        case map : TMap => { println("map") }
        case list : TList => { println("list") }
      }
      return elem.asInstanceOf[TReg]
    }
    else {
      val newElement = new TReg()
      content.put(key, newElement.asInstanceOf[DocItem])
      return newElement
    }
  }
}
