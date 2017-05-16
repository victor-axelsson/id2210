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
  "A get expression" should " create the context for an evaluator" in {

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

    var cursor1:Cursor = eval.evalExpr(expr1)
    var cursor2:Cursor = eval.evalExpr(expr2)
    var cursor3:Cursor = eval.evalExpr(expr3)

    assert(cursor1.getKeys().size == 1)
    assert(cursor1.getKeys().size == 2)
    assert(cursor1.getKeys().size == 3)
    /*
    val epxr1:Expr = Get(Doc(), "someVar1")
    val epxr2:Expr = Get(epxr1, "someVar2")
    val epxr3:Expr = Get(epxr2, "someVar3")

    var cursor:Cursor = eval.evalExpr(epxr1)
    var cursor2:Cursor = eval.evalExpr(epxr2)
    var cursor3:Cursor = eval.evalExpr(epxr3)
    */

    /*
    var cursor:Cursor = eval.evalExpr(Get(Doc(), "someVar1")).evalExpr(Get(Doc(), "someVar2")).commitExpr()
    var eval2:Evaluator = eval.evalExpr(Get(Doc(), "someVar1")).evalExpr(Get(Doc(), "someVar1"))
    eval.commitExpr()
    */
    //eval.evalExpresssion()

  }
}
