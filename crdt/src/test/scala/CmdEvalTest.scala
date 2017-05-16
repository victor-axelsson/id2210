import app.document.context.{Context, NodeDoc}
import app.document.cursor.Cursor
import app.document.cursor.Key.{RootMapT, mapT, regT}
import app.document.evaluator.{Evaluator, Operation}
import app.document.language.Cmd.{Assign, Let}
import app.document.language.Val.Str
import app.document.language.{Expr, Val, Var}
import org.scalatest.FlatSpec

/**
  * Created by victoraxelsson on 2017-05-16.
  */
class CmdEvalTest extends FlatSpec{
  "A context" should "be able to perform DESCEND" in {

    val eval = new Evaluator(1)
    val x = new Var.VarString("asd")
    val cmd = Let(x, Expr.Doc())

    var cmd1 = Assign(Expr.Doc(), new Str("someKey"))
    //eval.evalCmd(cmd1)
  }
}

