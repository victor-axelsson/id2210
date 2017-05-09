package app.document.context

import app.document.cursor.Key.{RootMapT, listT, mapT}
import app.document.cursor.{Cursor, Key}
import app.document.evaluator.Mutation
import org.json.JSONObject

/**
  * Created by victoraxelsson on 2017-05-06.
  */
class Context(initObj : String = "{}") {

  private var body = new JSONObject(initObj);
  var id :Int = -1
  var deps:List[Int] = null
  var cursor: Cursor = null
  var mutation:Mutation = null



  private def copyCtor(_id :Int, _deps:List[Int], _cursor: Cursor, _mutation:Mutation): Context = {
    var instance:Context = new Context(body.toString())
    instance.id = _id
    instance.deps = _deps
    instance.cursor = _cursor
    instance.mutation = _mutation
    instance
  }

  private def copyCtor(_id :Int, _deps:List[Int], _cursor: Cursor, _mutation:Mutation, _body: String): Context = {
    var instance:Context = new Context(_body)
    instance.id = _id
    instance.deps = _deps
    instance.cursor = _cursor
    instance.mutation = _mutation
    instance
  }

  def apply(id :Int, deps:List[Int], cursor: Cursor, mutation:Mutation) = {
    var instance:Context = copyCtor(id, deps, cursor, mutation)

    var newContext = descend(instance)
    println(newContext.body.toString())
    newContext
  }


  def descend(instance:Context): Context = {
    var keys:List[Key] = instance.cursor.getKeys()
    var tail = instance.cursor.getTail()

    if(keys.size > 0){
      var newCursor = new Cursor(keys.tail, tail);
      var newBody:String = "{}"

      keys.head match {
          case RootMapT(_) => {
            newBody = instance.body.toString
          }
          case mapT(_) => {
            var s:String = keys.head.asInstanceOf[mapT].key
            newBody = instance.body.getJSONObject(s).toString
          }
          case listT(_) => {
            var s:String = keys.head.asInstanceOf[listT].key
            newBody = instance.body.getJSONArray(s).toString
          }
          case _ => {
            throw new Exception("Not matched any class")
          }
      }

      var instance2:Context = copyCtor(instance.id, instance.deps, newCursor, instance.mutation, newBody)
      return descend(instance2)
    }

    instance
  }


  def getJson() :String = body.toString()
}
