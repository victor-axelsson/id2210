import app.document.context.{Context, NodeDoc}
import app.document.cursor.Cursor
import app.document.cursor.Key.{RootMapT, mapT}
import app.document.evaluator.{Evaluator, Mutation}
import app.document.language.{Expr, Val}
import org.scalatest.FlatSpec





/**
  * Created by victoraxelsson on 2017-05-06.
  */
class ContextTest extends FlatSpec{
  "A context" should "be able to perform DESCEND" in {

    var nodeDoc:NodeDoc = new NodeDoc(null)
    var context = new Context(nodeDoc)
    var eval:Evaluator = new Evaluator(1)

    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val addMap1 = mapT("someVar1")
    val addMap2 = mapT("someVar2")
    val addMap3 = mapT("someVar4")

    cursor = cursor.append(addMap1)
    cursor = cursor.append(addMap2)
    cursor = cursor.append(addMap3)

    var op = eval.makeAssign(cursor, new Val.Str("someVal4"))

    var newContext:Context = context.apply(op.getId(), op.getDeps(), op.getCursor(), op.getMutation())

    println(newContext.getDoc().getPres().contains("someVar2"))

    println(newContext.getDoc().toString())
    println(context.getDoc().toString())
    //Maybe one, not sure
    assert(newContext.cursor.getKeys().size == 0)
    //assert(!newContext.getJson().equals(json))
    assert(newContext.cursor.getTail().equals(addMap3))
   // assert()
  }
}
