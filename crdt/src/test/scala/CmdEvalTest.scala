import app.document.evaluator.Evaluator
import app.document.language.Cmd.Assign
import app.document.language.Expr
import app.document.language.Expr.Get
import app.document.language.Val.EmptyMap
import org.scalatest.FlatSpec

/**
  * Created by victoraxelsson on 2017-05-16.
  */
class CmdEvalTest extends FlatSpec{
  "An assign cmd " should " set the state of the eval Ap" in {

    val eval = new Evaluator(1)
    var cmd1 = Assign(Expr.Doc(), EmptyMap)

    
    eval.evalExpr(Get(Expr.Doc(), "someVar")).evalExpr(Get(Expr.Doc(), "someVar2")).evalCmd(cmd1)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":{}}}}"

    assert(s == expectedOutput)
  }
}

