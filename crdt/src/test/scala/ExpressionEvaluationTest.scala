import app.document.context.{Context, NodeDoc}
import app.document.cursor.Cursor
import app.document.cursor.Key.{RootMapT, mapT, regT}
import app.document.evaluator.{Evaluator, Operation}
import app.document.language.Expr.{Doc, Get}
import app.document.language.{Expr, Val}
import org.scalatest.FlatSpec

/**
  * Created by victoraxelsson on 2017-05-16.
  */
class ExpressionEvaluationTest extends FlatSpec{
  "A get expression" should " create a cursor without side effects" in {

    //// Build up the document ////
    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]())
    var context = new Context(nodeDoc)
    var eval:Evaluator = new Evaluator(1)

    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val addMap1 = mapT("someVar1")
    val addMap2 = mapT("someVar2")
    val reg3 = regT("someVar4")

    cursor = cursor.append(addMap1)
    cursor = cursor.append(addMap2)
    cursor = cursor.append(reg3)
    val op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    val newContext:Context = context.apply(op)

    //// END Build up the document ////

    val expr1:Expr = Get(Doc(), "someVar1")
    val expr2:Expr = Get(expr1, "someVar2")
    val expr3:Expr = Get(expr2, "someVar4")

    var c:Cursor = eval.evalExpr(expr1).evalExpr(expr2).evalExpr(expr3).toCursor()

    //Check for right structure
    assert(c.getKeys().size == 3)
    assert(c.getKeys().head.isInstanceOf[RootMapT])
    assert(c.getKeys().tail.head.isInstanceOf[mapT])
    assert(c.getKeys().tail.tail.head.isInstanceOf[mapT])
    assert(c.getTail().isInstanceOf[regT])

    //Check for side effects on the original cursor
    assert(eval.toCursor().getKeys().size == 0)
    assert(eval.node == null)

  }
}
