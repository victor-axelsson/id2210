import app.document.language.Cmd.Let
import app.document.language.{Expr, Var}
import org.scalatest.FlatSpec

/**
  * Created by Nick on 5/3/2017.
  */
class LetTest extends FlatSpec {
  "A Let command" should "assign a Doc to a Var" in {
    val x = new Var.VarString("x")
    val cmd = Let(x, Expr.Doc)
    println(x.toString())
    println(cmd.toString())
  }
}
