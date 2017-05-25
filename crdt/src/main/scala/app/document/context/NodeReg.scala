package app.document.context

import app.document.evaluator.Operation
import app.document.language.Val
import app.document.language.Val.{False, Null, Number, Str, True}

import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-09.
  */
class NodeReg(theName:String, var values : List[Val], pres:mutable.Map[Timestamp, Operation]) extends Node(theName, pres){


  def getValues() = values
  def addValues(newVal:Val) = {
   values = values :+ newVal
  }

  override def getChildren(): List[Node] = {
    null
  }

  override def toString: String = {
    val builder : mutable.StringBuilder = new mutable.StringBuilder()
    builder.append('"').append(name).append('"').append(":")
    for (value <- values) {

      value match {
        case Str(_) => {
          builder.append('"')
          builder.append(value.asInstanceOf[Str].getVal())
          builder.append('"')
        }
        case Number(_) => {
          builder.append(value.asInstanceOf[Number].getVal())
        }
        case True => {
          builder.append("true")
        }
        case False => {
          builder.append("false")
        }
        case Null => {
          builder.append("null")
        }
        case _ => {
         throw new Exception("Node reg should noe be able to be anything else")
        }
      }


      if (!value.eq(values.last)) {
        builder.append(",")
      }
    }
    builder.toString()
  }

  override def addChild(node: Node) = {
    throw new Exception("You cannot add children to a register")
  }
}
