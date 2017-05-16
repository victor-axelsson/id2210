import app.document.cursor.Cursor
import app.document.evaluator.Evaluator
import app.document.language.Expr
import app.document.language.Expr.{Doc, Get}
import org.scalatest.FlatSpec

/**
  * Created by victoraxelsson on 2017-05-16.
  */
class ExpressionEvaluationTest extends FlatSpec{
  "A get expression" should " create the context for an evaluator" in {

    val eval = new Evaluator(1)
    val epxr1:Expr = Get(Doc(), "someVar1")
    val epxr2:Expr = Get(epxr1, "someVar2")
    val epxr3:Expr = Get(epxr2, "someVar3")

    var cursor:Cursor = eval.evalExpr(epxr1)
    var cursor2:Cursor = eval.evalExpr(epxr2)
    var cursor3:Cursor = eval.evalExpr(epxr3)


    /*
    var cursor:Cursor = eval.evalExpr(Get(Doc(), "someVar1")).evalExpr(Get(Doc(), "someVar2")).commitExpr()
    var eval2:Evaluator = eval.evalExpr(Get(Doc(), "someVar1")).evalExpr(Get(Doc(), "someVar1"))
    eval.commitExpr()
    */
    //eval.evalExpresssion()

  }
}
