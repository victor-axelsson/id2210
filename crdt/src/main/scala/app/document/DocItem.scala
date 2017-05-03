package app.document

/**
  * Created by victoraxelsson on 2017-05-03.
  */
abstract class DocItem {
  def getType(): String

  def assign(tReg:TReg) = {
    println("Assigning")
  }

  def get(key:String) : DocItem = {
    println("getting key")
    null
  }

  def idx(i:Int) : TReg = {
    println("Idx")
    null
  }

  def insertAfter(key:TReg) = {
    println("insert after")
    null
  }

  def delete(key:String) = {
    println("delete");
  }

  def getKeys(): List[String] = {
    println("get keys")
    null
  }

  def getValues(): List[TReg] = {
    println("Getting values")
    null
  }

  override def toString : String = {""}
}
