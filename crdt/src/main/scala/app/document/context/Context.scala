package app.document.context

import app.document.cursor.Key._
import app.document.cursor.{Cursor, Key}
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.evaluator.Operation
import app.document.language.Val
import app.document.language.Val.{EmptyList, EmptyMap}

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * Created by victoraxelsson on 2017-05-06.
  */
class Context(var doc: Node) {

  var op: Operation = null
  var child: Node = doc
  var prev: Node = null

  def getDoc(): Node = doc

  def apply(_op: Operation): Context = {
    op = _op

    var newContext = this

    if (op.getCursor().getKeys().size > 0) {
      prev = null
      newContext = descend(copyCtor(op, doc))
      //the doc is used for descending, now swap it back
      child = newContext.doc
      newContext.doc = doc;
    }

    //Do shiet
    op.getMutation() match {
      case Assign(_) => {

        var ass: Assign = newContext.op.getMutation().asInstanceOf[Assign]

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


  //CLEAR-REG
  private def clearReg(deps: List[Timestamp], regT: regT) = {
    val register = childGetFromListWithType(regT, child.getChildren(), classOf[NodeReg])
    if (register != null && register.isInstanceOf[NodeReg]) {
      var nodeReg = register.asInstanceOf[NodeReg]
      clearNodeReg(deps, nodeReg)
      clearElem(deps, child)
    }
  }

  private def clearNodeReg(deps: List[Timestamp], nodeReg: NodeReg) = {
    for (dep <- deps) {
      if (child.getPres().get(dep).isDefined) {
        var op: Operation = child.getPres()(dep)
        if (op.getMutation().isInstanceOf[Assign]) {
          var ass = op.getMutation().asInstanceOf[Assign]
          nodeReg.values = nodeReg.values.filter(v => !v.eq(ass.value))
        }
      }
    }
    if (nodeReg.values.isEmpty) {
      nodeReg.setTombstone(true)
    }
  }

  //CLEAR-ELEM
  def clearElem(deps: List[Timestamp], node: Node) = {
    for (dep <- deps) {
      node.removeKeyPresence(dep)
    }
  }

  //CLEAR-MAP
  def clearMap(deps: List[Timestamp], t: mapT) = {
    val map = childGetFromListWithType(t, child.getChildren(), classOf[NodeMap])
    if (map != null && map.isInstanceOf[NodeMap]) {
      var nodeMap = map.asInstanceOf[NodeMap]
      clearNodeMap(deps, nodeMap)
    }
    clearElem(deps, child)
  }

  private def clearNodeMap(deps: List[Timestamp], nodeMap: NodeMap) : Unit = {
    for (dep <- deps) {
      if (child.getPres().get(dep).isDefined) {
        var op: Operation = child.getPres()(dep)
        if (op.getMutation().isInstanceOf[Assign] && nodeMap.children.contains(op.getCursor().getId().getKey())) { //CLEAR-NONE
          var node : Node =  nodeMap.children(op.getCursor().getId().getKey())
          //Recursive clear
          node match {
            case nodeReg: NodeReg =>
              clearNodeReg(deps, nodeReg)
            case nodeList: NodeList =>
              clearNodeList(deps, nodeList)
            case nodeMap: NodeMap =>
              clearNodeMap(deps, nodeMap)
          }
        }
      }
    }
  }

  //CLEAR-LIST
  def clearList(ints: List[Timestamp], t: listT) = {
    //TODO: implement
  }

  private def clearNodeList(deps: List[Timestamp], nodeList: NodeList) = {
    //TODO implement
  }

  //CLEAR-ANY
  def clearAny(deps: List[Timestamp], key: Key) : Unit = {
    key match {
      case RootMapT(_) => {
        throw new Exception("You cannot clear a doc")
      }
      case mapT(_) => {
        clearMap(deps, key.asInstanceOf[mapT])
      }
      case listT(_) => {
        clearList(deps, key.asInstanceOf[listT])
      }
      case regT(_) => {
        clearReg(deps, key.asInstanceOf[regT])
      }
      case identifierT(_) => {
        //CLEAR-NONE
      }
    }
  }

  private def emptyMap(context: Context) = {

    var mapT: mapT = null

    if (context.op.getCursor().getTail().isInstanceOf[mapT]) {
      mapT = context.op.getCursor().getTail().asInstanceOf[mapT]
    } else {
      mapT = new mapT(context.op.getCursor().getId().getKey())
    }

    clearAny(context.op.getDeps(), mapT)

    var nMap: NodeMap = childGetWithType(mapT, context, classOf[NodeMap]).asInstanceOf[NodeMap]

    if (nMap == null) {
      nMap = new NodeMap(mapT.key, new scala.collection.mutable.HashMap[Timestamp, Operation]())
    }

    context.child.addChild(nMap)
    addId(mapT.key, context.op, context.child)
  }

  private def emptyList(context: Context) = {
    var listT: listT = null

    if (context.op.getCursor().getTail().isInstanceOf[listT]) {
      listT = context.op.getCursor().getTail().asInstanceOf[listT]
    } else {
      listT = new listT(context.op.getCursor().getId().getKey())
    }


    var node = childGet(listT, context)

    if (node == null) {
      node = new NodeList(listT.key, new scala.collection.mutable.HashMap[Timestamp, Operation]())
      clearAny(context.op.getDeps(), listT)
      context.child.addChild(node)
    } else {

      node match {
        case _:NodeList => {
          throw new Exception("I dunno")
        }
        case _ : NodeMap => {
          val node2 = new NodeList(listT.key, new scala.collection.mutable.HashMap[Timestamp, Operation]())
          context.child.addChild(node2)

        }
      }

      addId(listT.key, context.op, context.child)
      //throw new Exception("I'm not sure what to do here")
    }


  }


  private def delete(context: Context) = {

    var key:Key = context.op.getCursor().getId()
    var node:Node = childGetFromListWithType(key, context.child.getChildren(), classOf[Node])
    node.setTombstone(true)
    clearAny(context.op.getDeps(), key)
    addId(key.getKey(), context.op, node)

    if(context.child.isInstanceOf[NodeList]){
      context.child.asInstanceOf[NodeList].reIndexChildren()
    }
  }

  private def insert(context: Context) = {

    val insert: Insert = context.op.getMutation().asInstanceOf[Insert]
    val index: Int = context.op.getCursor().getId().getKey().toInt

    var key: Key = null;
    val name: String = "[" + index + "]"

    val node: Node = insert.value match {
      case s@EmptyMap => {
        key = mapT(name)
        new NodeMap(name, new scala.collection.mutable.HashMap[Timestamp, Operation]())
      }
      case s@EmptyList => {
        key = listT(name)
        new NodeList(name, new scala.collection.mutable.HashMap[Timestamp, Operation]())
      }
      case s@_ => {
        key = regT(name)
        var values = List[Val]()
        values = values :+ s
        new NodeReg(name, values, new scala.collection.mutable.HashMap[Timestamp, Operation]())
      }
    }

    context.child.asInstanceOf[NodeList].insertAt(index, node)
    addId(key.getKey(), context.op, context.child)
  }

  private def assign(context: Context) = {
    var regT: regT = null
    if (context.op.getCursor().getTail().isInstanceOf[regT]) {
      regT = context.op.getCursor().getTail().asInstanceOf[regT]
    } else {
      regT = new regT(context.op.getCursor().getId().getKey())
    }

    clearAny(context.op.getDeps(), regT)
    val assign: Assign = context.op.getMutation().asInstanceOf[Assign]

    var nReg: NodeReg = childGetWithType(regT, context, classOf[NodeReg]).asInstanceOf[NodeReg]

    if (nReg == null) {
      var values = List[Val]()
      values = values :+ assign.value
      nReg = new NodeReg(regT.key, values, new scala.collection.mutable.HashMap[Timestamp, Operation]());
    } else {
      nReg.addValues(assign.value)
      nReg.setTombstone(false)
    }

    context.child.addChild(nReg)
    addId(regT.key, context.op, context.child)

  }

  private def copyCtor(_op: Operation, _doc: Node): Context = {
    var instance: Context = new Context(_doc)
    instance.op = _op
    instance
  }


  def childGetWithType(key: Key, context: Context, searchType : Class[_]): Node = {
    return childGetFromListWithType(key, context.getDoc().getChildren(), searchType)
  }


  def childGet(key: Key, context: Context): Node = {
    return childGetFromList(key, context.getDoc().getChildren())
  }

  def childGetFromListWithType(key: Key, children : List[Node], searchType : Class[_]): Node = {

    @tailrec
    def find(childs: List[Node]): Node = {

      if (childs.size <= 0) {
        return null;
      }

      val s: String = "[" + key.getKey() + "]"

      if ((childs.head.getName() == key.getKey() || childs.head.getName() == s) && (searchType.isAssignableFrom(childs.head.getClass))) {
        return childs.head
      }

      find(childs.tail)
    }

    return find(children)
  }


  def childGetFromList(key: Key, children : List[Node]): Node = {

    @tailrec
    def find(childs: List[Node]): Node = {

      if (childs.size <= 0) {
        return null;
      }

      val s: String = "[" + key.getKey() + "]"

      if ((childs.head.getName() == key.getKey() || childs.head.getName() == s)) {
        return childs.head
      }

      find(childs.tail)
    }

    return find(children)
  }


  def childMap(key: String): Node = {
    new NodeMap(key, mutable.Map.empty)
  }

  def childList(key: String): Node = {
    new NodeList(key, mutable.Map.empty)
  }

  def addId(key: String, operation: Operation, node: Node) = {
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
  def presence(context: Context, id: Timestamp): Context = {
    val node = context.child
    if (node.getPres().contains(id)) {
      context
    } else {
      null
    }
  }

  //DESCEND
  def descend(context: Context): Context = {
    var keys: List[Key] = context.op.getCursor().getKeys()
    var tail = context.op.getCursor().getTail()

    var node: Node = null
    if (keys.size > 0) {

      val T = keys.head match {
        case mapT(_) => {classOf[NodeMap]}
        case listT(_) => {classOf[NodeList]}
        case regT(_) => {classOf[NodeReg]}
        case _ => { classOf[Node]}
      }

      node = childGetWithType(keys.head, context: Context, T)
    }

    if (keys.size > 0) {
      if (node == null) {
        keys.head match {
          case RootMapT(_) => {
            //Keep the doc as node if we are at first iteration
            node = context.doc
          }
          case mapT(_) => {
            node = childMap(keys.head.getKey())
          }
          case listT(_) => {
            node = childList(keys.head.getKey())
          }
          case regT(_) => {
            throw new Exception("There should not be a reg in he middle of the cursor")
          }
        }
      }

      if (prev != null && !prev.isInstanceOf[NodeList]) {
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
    var keys: List[Key] = c.getKeys()
    var tail = c.getTail()
    var node: Node = null

    if (keys.size > 0) {
      val T = keys.head match {
        case mapT(_) => {classOf[NodeMap]}
        case listT(_) => {classOf[NodeList]}
        case regT(_) => {classOf[NodeReg]}
        case _ => { classOf[Node]}
      }

      node = childGetWithType(keys.head, context: Context, T)
    }

    if (keys.size > 0) {
      if (node == null) {

        keys.head match {
          case RootMapT(_) => {
            //Keep the doc as node if we are at first iteration
            node = context.doc
          }
          case mapT(_) => {
            node = childMap(keys.head.getKey())
          }
          case listT(_) => {
            node = childList(keys.head.getKey())
          }
          case regT(_) => {
            throw new Exception("There should not be a reg in he middle of the cursor")
          }
        }
      }

      if (prev != null) {
        prev.addChild(node)
      }

      val newCursor = new Cursor(keys.tail, tail);

      prev = node
      return descend(newCursor, copyCtor(new Operation(op.getId(), op.getDeps(), newCursor, op.getMutation()), node))
    }

    context
  }

}
