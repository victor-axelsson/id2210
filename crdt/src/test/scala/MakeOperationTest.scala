import app.document.cursor.Cursor
import app.document.cursor.Key.{RootMapT, listT, mapT}
import app.document.evaluator.Evaluator
import app.document.evaluator.Mutation.{Assign, Delete, Insert}
import app.document.language
import app.document.language.{Expr, Val}
import org.scalatest.FlatSpec

/**
  * Created by Nick on 5/3/2017.
  */
class MakeOperationTest extends FlatSpec {
  "An Evaluator" should "create operations for insert" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val value = language.Val.Str("stuff")

    val evaluator = new Evaluator(1)

    val op = evaluator.makeInsert(cursor, value)
    assert(op.getId() == 1)
    assert(op.getDeps() == List.empty[Int])
    assert(op.getCursor() == cursor)
    assert(op.getMutation().isInstanceOf[Insert])
  }

  "An Evaluator" should "create operations for assign" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val value = language.Val.Str("stuff")

    val evaluator = new Evaluator(1)

    val op = evaluator.makeAssign(cursor, value)
    assert(op.getId() == 1)
    assert(op.getDeps() == List.empty[Int])
    assert(op.getCursor() == cursor)
    assert(op.getMutation().isInstanceOf[Assign])
  }

  "An Evaluator" should "create operations for delete" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val evaluator = new Evaluator(1)

    val op = evaluator.makeDelete(cursor)
    assert(op.getId() == 1)
    assert(op.getDeps() == List.empty[Int])
    assert(op.getCursor() == cursor)
    assert(op.getMutation().isInstanceOf[Delete])
  }

  "An Evaluator" should "increase the id after an operation was created" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val value = language.Val.Str("stuff")

    val evaluator = new Evaluator(1)

    val op1 = evaluator.makeInsert(cursor, value)
    val op2 = evaluator.makeInsert(cursor, value)
    assert(op1.getId() == 1)
    assert(op2.getId() > op1.getId())
  }


  "An Evaluator" should " not create an id that is 0" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val value = language.Val.Str("stuff")

    val evaluator = new Evaluator(1)

    val op = evaluator.makeInsert(cursor, value)
    assert(op.getId() != 0)
  }
}
