package app.document.cursor

import app.document.context.{Context, Node}

import scala.annotation.tailrec

/**
  * Created by Nick on 5/3/2017.
  */
class Cursor(keys : List[Key], id : Key) {
  def getId() = id


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
      var newCtx = ctx.descend(this, ctx)
      //Swap back the propagated nodes
      newCtx.child = newCtx.doc
      newCtx.doc = ctx.doc

      //Step to the child key, otherwise you will get siblings
      var node:Node = null
      for(n:Node <- newCtx.child.getChildren()){
        if(n.getName() == newCtx.op.getCursor().getId().getKey()){
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
