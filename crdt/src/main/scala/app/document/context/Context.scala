package app.document.context

import app.document.cursor.{Cursor, Key}
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.evaluator.Operation
import app.document.language.Val.{EmptyList, EmptyMap}

/**
  * Created by victoraxelsson on 2017-05-06.
  */
class Context(var doc:Node) {

  var op : Operation = null
  var child:Node = doc

  def getDoc() :Node = doc

  def apply(_op : Operation) : Context = {
    op = _op

    var newContext = this

    if(op.getCursor().getKeys().size > 0){
      newContext = descend(copyCtor(op, doc))
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

  private def emptyMap(context: Context) = {
    //TODO: shiet
  }

  private def emptyList(context: Context) = {
    //TODO: shiet
  }

  private def clearReg(deps:List[Int], regT:NodeReg) = {
    //TODO: shiet
  }

  private def delete(context: Context) = {
    //TODO: shiet
  }

  private def insert(context: Context) = {
    //TODO: shiet
  }

  private def assign(context: Context) = {
    if(!child.isInstanceOf[NodeReg]){
      throw new Exception("Assign is only for NodeReg")
    }

    var regT = child.asInstanceOf[NodeReg]

    clearReg(context.op.getDeps(), regT)

    var assign:Assign = context.op.getMutation().asInstanceOf[Assign]
    regT.addValues(assign.value)
  }

  private def copyCtor(_op : Operation, _doc:Node): Context = {
    var instance:Context = new Context(_doc)
    instance.op = _op
    instance
  }


  def childGet(tail:Key) = {
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



    if(keys.size > 0){
      var newCursor = new Cursor(keys.tail, tail);

      var node:Node = childGet(tail)

      if(node == null){

        keys.head match {
          case mapT => {
            println(keys.head.getKey())
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
      addId(keys.head.getKey(), context.op, node)
      return descend(copyCtor(new Operation(op.getId(), op.getDeps(), newCursor, op.getMutation()), node))
    }


    context
  }

}
