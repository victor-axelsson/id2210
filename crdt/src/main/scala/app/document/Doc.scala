package app.document

import app.document
import app.document.language.Cmd.Let
import app.document.language.{Cmd, Expr, Var}

/**
  * Created by victoraxelsson on 2017-05-03.
  */
class Doc{
  def get(key:String) : TReg = {
    println("getting key")
    var x = new Var.VarString("stuufff")
    Cmd.Let(x, Expr.Doc)
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
}
