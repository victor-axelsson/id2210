package app.document.context

import app.document.cursor.{Cursor, Key}
import app.document.evaluator.Mutation

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

    if(cursor.getKeys().size > 0){
      var c:Context = descend(this)
      println(c.cursor.getKeys())
      return c
    }

    this
  }

  private def copyCtor(_id :Int, _deps:List[Int], _cursor: Cursor, _mutation:Mutation, _doc:Node): Context = {
    var instance:Context = new Context(_doc)
    instance.id = _id
    instance.deps = _deps
    instance.cursor = _cursor
    instance.mutation = _mutation
    instance
  }

  def descend(context: Context): Context = {
    var keys:List[Key] = context.cursor.getKeys()
    var tail = context.cursor.getTail()

    //TODO: implemented add

    if(keys.size > 0){
      var newCursor = new Cursor(keys.tail, tail);

      println(newCursor.getKeys())


      //Find new doc
      var newDoc:Node = null;
      for(node <- context.getDoc().getChildren()){
        if(node.getName().equals(tail.getKey())){
          newDoc = node
        }
      }

      return descend(copyCtor(id, deps, newCursor, mutation, newDoc))


      /*
      keys.head match {
          case RootMapT(_) => {

          }
          case mapT(_) => {
            var s:String = keys.head.asInstanceOf[mapT].key

          }
          case listT(_) => {
            var s:String = keys.head.asInstanceOf[listT].key

          }
          case _ => {
            throw new Exception("Not matched any class")
          }
      }
      */

    }

    context
  }

}
