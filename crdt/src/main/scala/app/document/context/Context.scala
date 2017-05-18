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

    var mapT:mapT = null

    if(context.op.getCursor().getTail().isInstanceOf[mapT]){
      mapT = context.op.getCursor().getTail().asInstanceOf[mapT]
    }else{
      mapT = new mapT(context.op.getCursor().getId().getKey())
    }

    clear(context.op.getDeps(), mapT)

    var nMap:NodeMap = childGet(mapT, context).asInstanceOf[NodeMap]

    if(nMap == null){
      nMap = new NodeMap(mapT.key, new scala.collection.immutable.HashMap[Int, Operation]())
    }else{
        throw new Exception("I'm not sure what to do here")
    }

    context.child.addChild(nMap)
    addId(mapT.key, context.op, context.child)
  }

  private def emptyList(context: Context) = {
    var listT:listT = null

    if(context.op.getCursor().getTail().isInstanceOf[listT]){
      listT = context.op.getCursor().getTail().asInstanceOf[listT]
    }else{
      listT = new listT(context.op.getCursor().getId().getKey())
    }

    clear(context.op.getDeps(), listT)

    var nList = childGet(listT, context).asInstanceOf[NodeList]

    if(nList == null){
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
    var listT:listT = null

    if(context.op.getCursor().getTail().isInstanceOf[listT]){
      listT = context.op.getCursor().getTail().asInstanceOf[listT]
    }else{
      listT = new listT(context.op.getCursor().getId().getKey())
    }

    clear(context.op.getDeps(), listT)

    val insert:Insert = context.op.getMutation().asInstanceOf[Insert]

    throw new NotImplementedError("We need to add the value drom insert into the array")

    var nList = childGet(listT, context).asInstanceOf[NodeList]

    if(nList == null){
      nList = new NodeList(listT.key, new scala.collection.immutable.HashMap[Int, Operation]())
      //nList.insertAfter(insert.value, )
    }else{
      throw new Exception("I'm not sure what to do here")
    }

    context.child.addChild(nList)
    addId(listT.key, context.op, context.child)
  }

  private def assign(context: Context) = {

    var regT:regT = null

    if(context.op.getCursor().getTail().isInstanceOf[regT]){
      regT = context.op.getCursor().getTail().asInstanceOf[regT]
    }else{
      regT = new regT(context.op.getCursor().getId().getKey())
    }

    //var regT = context.op.getCursor().getTail().asInstanceOf[regT]
    clear(context.op.getDeps(), regT)
    val assign:Assign = context.op.getMutation().asInstanceOf[Assign]


    var nReg:NodeReg = childGet(regT, context).asInstanceOf[NodeReg]

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


  def childGet(key:Key, context: Context):Node = {

    @tailrec
    def find(childs:List[Node]):Node = {

      if(childs.size <= 0){
        return null
      }

      if(childs.head.getName() == key.getKey()){
        return childs.head
      }

      find(childs.tail)
    }

    return find(context.getDoc().getChildren())
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

    var node:Node = null
    if(keys.size > 0){
      node = childGet(keys.head, context: Context)
    }

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

  //DESCEND with a given cursor
  def descend(c: Cursor, context: Context): Context = {
    var keys:List[Key] = c.getKeys()
    var tail = c.getTail()
    var node:Node = null

    if(keys.size > 0){
      node = childGet(keys.head, context)
    }

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

      prev = node
      return descend(newCursor, copyCtor(new Operation(op.getId(), op.getDeps(), newCursor, op.getMutation()), node))
    }

    context
  }

}
