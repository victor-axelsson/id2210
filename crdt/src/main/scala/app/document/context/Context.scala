package app.document.context

import app.document.cursor.{Cursor, Key}
import app.document.evaluator.Mutation
import app.document.evaluator.Mutation.Delete

/**
  * Created by victoraxelsson on 2017-05-06.
  */
class Context(doc:Node) {

  var id :Int = -1
  var deps:List[Int] = null
  var cursor: Cursor = null
  var mutation:Mutation = null

  def getDoc() :Node = doc


  def apply(_id :Int, _deps:List[Int], _cursor: Cursor, _mutation:Mutation) : Context = {
    id = _id
    deps = _deps
    cursor = _cursor
    mutation = _mutation

    var newContext = this

    if(cursor.getKeys().size > 0){
      newContext = descend(copyCtor(id, deps, cursor, mutation, doc))
    }

    newContext
  }

  private def copyCtor(_id :Int, _deps:List[Int], _cursor: Cursor, _mutation:Mutation, _doc:Node): Context = {
    var instance:Context = new Context(_doc)
    instance.id = _id
    instance.deps = _deps
    instance.cursor = _cursor
    instance.mutation = _mutation
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
    new NodeMap(key, Set.empty)
  }

  def childList(key:String) :Node= {
    new NodeList(key, Set.empty)
  }

  def addId(key:String, mutation: Mutation, node: Node) = {
    mutation match {
      case Delete() => {
        node.removeKeyPresence(key)
      }
      case _ => {
        node.addKeyPresence(key)
      }
    }
  }



  def descend(context: Context): Context = {
    var keys:List[Key] = context.cursor.getKeys()
    var tail = context.cursor.getTail()



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
      addId(keys.head.getKey(), context.mutation, node)
      return descend(copyCtor(id, deps, newCursor, mutation, node))
    }


    context
  }

}
