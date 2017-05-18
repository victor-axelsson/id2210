import app.document.evaluator.Evaluator
import app.document.language.Cmd.{Assign, Let}
import app.document.language.Expr
import app.document.language.Expr.{Get, Var}
import app.document.language.Val.{EmptyList, EmptyMap, Str}
import app.document.language.Var.VarString
import org.scalatest.FlatSpec

/**
  * Created by victoraxelsson on 2017-05-16.
  1, Let
  2, Assign,
  3, insertAfter
  4, Delete
  5, Yeild
  */
class CmdEvalTest extends FlatSpec{
  "An assign cmd with an empty map" should " set the state of the eval Ap" in {

    val eval = new Evaluator(1)
    var cmd1 = Assign(Expr.Doc(), EmptyMap)


    eval.evalExpr(Get(Expr.Doc(), "someVar")).evalExpr(Get(Expr.Doc(), "someVar2")).evalCmd(cmd1)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":{}}}}"

    assert(s == expectedOutput)
  }
  "An assign cmd with an empty list" should " set the state of the eval Ap" in {

    val eval = new Evaluator(1)
    var cmd1 = Assign(Expr.Doc(), EmptyList)


    eval.evalExpr(Get(Expr.Doc(), "someVar")).evalExpr(Get(Expr.Doc(), "someVar2")).evalCmd(cmd1)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[]}}}"

    assert(s == expectedOutput)
  }
  "An assign cmd with a reg " should " set the state of the eval Ap" in {

    val eval = new Evaluator(1)
    var cmd1 = Assign(Expr.Doc(), new Str("MyVal"))


    eval.evalExpr(Get(Expr.Doc(), "someVar")).evalExpr(Get(Expr.Doc(), "someVar2")).evalCmd(cmd1)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":\"MyVal\"}}}"

    assert(s == expectedOutput)
  }
  /*
  "An insertAfter cmd " should " set the state of the eval Ap" in {


    val eval = new Evaluator(1)
    var cmd1 = InsertAfter(Expr.Doc(), new Str("someIndex"))


    eval.evalExpr(Get(Expr.Doc(), "someVar")).evalExpr(Get(Expr.Doc(), "someVar2")).evalExpr(Idx(Expr.Doc(), 0)).evalCmd(cmd1)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"MyVal\"]}}}"

    assert(s == expectedOutput)
  }
  */
  "A LET cmd " should " take a named snapshot of the eval" in {

    val eval = new Evaluator(1)
    val letX = Let(new VarString("x"), Expr.Doc())


    eval.evalExpr(Get(Expr.Doc(), "someVar")).evalExpr(Get(Expr.Doc(), "someVar2")).evalCmd(letX)

    //Check for side effects on the original cursor
    assert(eval.toCursor().getKeys().size == 0)
    assert(eval.node == null)

    val assignCmd = Assign(Expr.Doc(), EmptyMap)

    //we should be able to get the snapshot of the state from the eval with a var
    eval.evalExpr(new Var(new VarString("x"))).evalCmd(assignCmd)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":{}}}}"

    assert(s == expectedOutput)
  }
}

