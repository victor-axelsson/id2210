package app.document.cursor

import app.document.context.{Context, Node, NodeReg}
import app.document.language.Val

import scala.annotation.tailrec

/**
  * Created by Nick on 5/3/2017.
  */
class Cursor(keys : List[Key], id : Key) {


  def getId() = id

  def descendInContext(ctx:Context) = {
    var newCtx = ctx.descend(this, ctx)
    //Swap back the propagated nodes
    newCtx.child = newCtx.doc
    newCtx.doc = ctx.doc
    newCtx
  }

  def values(ctx: Context): List[Val] = {

    //If no cursor, return empty set
    if(ctx.child == null){
      return List[Val]()
    }

    var newCtx = ctx;

    if(this.getKeys().size > 0){
      newCtx = descendInContext(ctx)
    }

    for(n:Node <- newCtx.child.getChildren()){
      if(n.getName() == this.id.getKey()){
        return n.asInstanceOf[NodeReg].values
      }
    }

    null
  }

  def keys(ctx: Context): scala.collection.mutable.Set[String] = {
    @tailrec
    def find(keys:scala.collection.mutable.Set[String], childs:List[Node]):scala.collection.mutable.Set[String] = {

      if(childs.size <= 0){
        return keys
      }

      keys += childs.head.getName()

      find(keys, childs.tail)
    }

    //If no cursor, return empty set
    if(ctx.child == null){
      return scala.collection.mutable.Set[String]()
    }

    //We might need to descend first (KEY2 vs KEY3)
    if(this.getKeys().size > 0){

      //KEY3
      val newCtx = descendInContext(ctx)

      //We need to store the key from this because it changes closure later
      val key:String = this.id.getKey()

      //Step to the child key, otherwise you will get siblings
      var node:Node = null
      for(n:Node <- newCtx.child.getChildren()){
        if(n.getName() == key){
          node = n
        }
      }

      find(scala.collection.mutable.Set[String](), node.getChildren())
    }else{

      //KEY2
      find(scala.collection.mutable.Set[String](), ctx.child.getChildren())
    }
  }

  def append(key : Key) : Cursor = {
    new Cursor(keys :+ id, key)
  }

  def getKeys() : List[Key] = keys

  def getTail() : Key = id


}
