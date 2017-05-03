package app.document

import app.document.TMap
import app.document.language.Cmd.Let
import app.document.language.{Cmd, Expr}
import app.document.language.Expr.{Doc, Var};

object DocItem{
  case object String extends TMap
  case object Vector extends TList
}

/**
  * Created by victoraxelsson on 2017-05-03.
  */
class DocItem  {

  case class TMap(str:String) extends DocItem;
  case class TList(lst:List[String]) extends DocItem;



  def assign(tReg:TReg) = {
    println("Assigning")


    //Cmd.Let("adsa", Expr.Var())

    Cmd.Let(null,null)

    //Cmd.Let(Var.VarString())
  }

  def get(key:String) : TReg = {
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

  override def toString : String = {
    "Hello"
  }
}
