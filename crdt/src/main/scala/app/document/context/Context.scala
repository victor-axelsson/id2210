package app.document.context

import app.document.cursor.Key.{RootMapT, listT, mapT, regT}
import app.document.cursor.{Cursor, Key}
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.evaluator.Operation
import app.document.language.Val
import app.document.language.Val.{EmptyList, EmptyMap}

import scala.annotation.tailrec

/**
  * Created by victoraxelsson on 2017-05-06.
  */
class Context(var doc:Node) {

  var op : Operation = null
  var child:Node = doc
  var prev:Node = null

  def getDoc() :Node = doc

  def apply(_op : Operation) : Context = {
    op = _op

    var newContext = this

    if(op.getCursor().getKeys().size > 0){
      prev = null
      newContext = descend(copyCtor(op, doc))
      //the doc is used for descending, now swap it back
      child = newContext.doc
      newContext.doc = doc;
    }

    //Do shiet
    op.getMutation() match {
      case Assign(_) => {

        var ass:Assign = newContext.op.getMutation().asInstanceOf[Assign]

        ass.value match {
          case EmptyMap => {
            emptyMap(newContext)
          }
          case EmptyList => {
            emptyList(newContext)
          }
          case _ => {
            assign(newContext)
          }
        }

      }
      case Insert(_) => {
        insert(newContext)
      }
      case Delete() => {
        delete(newContext)
      }
    }

    newContext
  }

  def clearElem(ints: List[Int], key: Key) = {
    //TODO: implement
  }

  def clearMap(ints: List[Int], t: mapT) = {
    //TODO: implement
  }

  def clearList(ints: List[Int], t: listT) = {
    //TODO: implement
  }

  def clear(deps: List[Int], key: Key) = {
    key match {
      case RootMapT(_) => { throw new Exception("You cannot clear a doc")}
      case mapT(_) => {
        clearMap(deps, key.asInstanceOf[mapT])
      }
      case listT(_) => {
        clearList(deps, key.asInstanceOf[listT])
      }
      case regT(_) => {
        clearReg(deps, key.asInstanceOf[regT])
      }
    }
  }

  private def emptyMap(context: Context) = {

    if(!context.op.getCursor().getTail().isInstanceOf[mapT]){
      throw new Exception("Assign EmptyMap is only for mapT")
    }

    val mapT = context.op.getCursor().getTail().asInstanceOf[mapT]
    clear(context.op.getDeps(), mapT)

    var nMap:NodeMap = childGet(mapT).asInstanceOf[NodeMap]

    if(nMap == null){
      nMap = new NodeMap(mapT.key, new scala.collection.immutable.HashMap[Int, Operation]())
    }else{
        throw new Exception("I'm not sure what to do here")
    }

    context.child.addChild(nMap)
    addId(mapT.key, context.op, context.child)
  }

  private def emptyList(context: Context) = {
    if(!context.op.getCursor().getTail().isInstanceOf[listT]){
      throw new Exception("Assign EmptyList is only for listT")
    }

    val listT = context.op.getCursor().getTail().asInstanceOf[listT]
    clear(context.op.getDeps(), listT)

    var nList = childGet(listT).asInstanceOf[NodeList]


    if(nList == null){
      //name:String, pres:Map[Int, Operation]
      nList = new NodeList(listT.key, new scala.collection.immutable.HashMap[Int, Operation]())
    }else{
      throw new Exception("I'm not sure what to do here")
    }

    context.child.addChild(nList)
    addId(listT.key, context.op, context.child)
  }

  private def clearReg(deps:List[Int], regT:regT) = {
    //TODO: shiet
  }

  private def delete(context: Context) = {
    //TODO: shiet
  }

  private def insert(context: Context) = {
    //TODO: shiet
  }

  private def assign(context: Context) = {

    if(!context.op.getCursor().getTail().isInstanceOf[regT]){
      throw new Exception("Assign is only for regT")
    }

    var regT = context.op.getCursor().getTail().asInstanceOf[regT]
    clear(context.op.getDeps(), regT)
    var assign:Assign = context.op.getMutation().asInstanceOf[Assign]


    var nReg:NodeReg = childGet(regT).asInstanceOf[NodeReg]

    if(nReg == null){
      var values = List[Val]()
      values = values :+ assign.value
      nReg = new NodeReg(regT.key, values, new scala.collection.immutable.HashMap[Int, Operation]());
    }else{
      nReg.addValues(assign.value)
    }

    context.child.addChild(nReg)
    addId(regT.key, context.op, context.child)

  }

  private def copyCtor(_op : Operation, _doc:Node): Context = {
    var instance:Context = new Context(_doc)
    instance.op = _op
    instance
  }


  def childGet(tail:Key):Node = {
    //Find new doc
    var newDoc:Node = null;
    for(node <- getDoc().getChildren()){
      if(node.getName().equals(tail.getKey())){
        newDoc = node
      }
    }
    newDoc
  }


  def childMap(key:String) :Node= {
    new NodeMap(key, Map.empty)
  }

  def childList(key:String) :Node= {
    new NodeList(key, Map.empty)
  }

  def addId(key:String, operation : Operation, node: Node) = {
    operation.getMutation() match {
      case Delete() => {
        node.removeKeyPresence(operation.getId())
      }
      case _ => {
        node.addKeyPresence(operation)
      }
    }
  }

  //PRESENCE
  def presence(context: Context, id : Int) : Context = {
    val node = context.child
    if (node.getPres().contains(id)) {
      context
    } else {
      null
    }
  }

  //DESCEND
  def descend(context: Context): Context = {
    var keys:List[Key] = context.op.getCursor().getKeys()
    var tail = context.op.getCursor().getTail()
    var node:Node = childGet(tail)


    if(keys.size > 0){
      if(node == null){

        keys.head match {
          case RootMapT(_) => {
            //Keep the doc as node if we are at first iteration
            node = context.doc
          }
          case mapT => {
            node = childMap(keys.head.getKey())
          }
          case listT => {
            node = childList(keys.head.getKey())
          }
          case regT => {
            throw new Exception("There should not be a reg in he middle of the cursor")
          }
        }
      }

      if(prev != null){
        prev.addChild(node)
      }

      val newCursor = new Cursor(keys.tail, tail);

      addId(keys.head.getKey(), context.op, node)
      prev = node
      return descend(copyCtor(new Operation(op.getId(), op.getDeps(), newCursor, op.getMutation()), node))
    }

    context
  }

}
