package app.document.context

import app.document.cursor.Cursor
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

  def apply(id :Int, deps:List[Int], cursor: Cursor, mutation:Mutation) = {
    var instance:Context = copyCtor(id, deps, cursor, mutation)



    instance
  }


  def getJson() :String = body.toString()
}
