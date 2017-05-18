package app.document.cursor

import app.document.context.{Context, Node, NodeList, NodeReg}
import app.document.cursor.Key.{identifierT, listT, mapT}
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

  def idx(ctx: Context, i: Int): Node = {

    //If no cursor, return null
    if(ctx.child == null){
      return null
    }

    //IDx1 seems to do some weird stuff with cursor tail, switching it to the head of the list - don't see reason to do it

    //IDx2 descends until we are at the needed Node
    var newCtx = ctx
    if(this.getKeys().size > 0){
      newCtx = descendInContext(ctx)
    }

    //We need to store the key from this because it changes closure later
    val key:String = this.id.getKey()

    var node:NodeList = null
    // Find the list node
    for(n:Node <- newCtx.child.getChildren()){
      if(n.getName() == this.id.getKey()){
        node = n.asInstanceOf[NodeList]
      }
    }

    // If node is null then we didn't find it. Return null
    if (node == null) {
      return null
    }
    else {
      var counter = i
      for (child <- node.getChildren()) {
        // IDx4
        if (!child.isTombstone()) { //figure out how to check for tombstone - node shouldn't be a tombstone
          // IDx3
         if (counter > 0) {
           counter -= 1
         }
         // IDx5
         else {
          return child
         }
        }
      }
    }
    null
  }

  def append(key : Key) : Cursor = {

    //If you try and append when there is a identifier it will be shuffled to a mapT
    var shuffleId = id
    if(id.isInstanceOf[identifierT]){
      shuffleId = new mapT(id.getKey())
    }

    new Cursor(keys :+ shuffleId, key)
  }

  def appendAsList(key : Key) : Cursor = {
    new Cursor(keys :+ new listT(id.getKey()), key)
  }

  def getKeys() : List[Key] = keys

  def getTail() : Key = id

}
