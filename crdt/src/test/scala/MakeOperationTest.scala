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

    val (id, deps, cur, mut) = evaluator.makeInsert(cursor, value)
    assert(id == 1)
    assert(deps == List.empty[Int])
    assert(cur == cursor)
    assert(mut.isInstanceOf[Insert])
  }

  "An Evaluator" should "create operations for assign" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val value = language.Val.Str("stuff")

    val evaluator = new Evaluator(1)

    val (id, deps, cur, mut) = evaluator.makeAssign(cursor, value)
    assert(id == 1)
    assert(deps == List.empty[Int])
    assert(cur == cursor)
    assert(mut.isInstanceOf[Assign])
  }

  "An Evaluator" should "create operations for delete" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val evaluator = new Evaluator(1)

    val (id, deps, cur, mut) = evaluator.makeDelete(cursor)
    assert(id == 1)
    assert(deps == List.empty[Int])
    assert(cur == cursor)
    assert(mut.isInstanceOf[Delete])
  }

  "An Evaluator" should "increase the id after an operation was created" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val value = language.Val.Str("stuff")

    val evaluator = new Evaluator(1)

    val (id, _, _, _) = evaluator.makeInsert(cursor, value)
    val (id2, _, _, _) = evaluator.makeInsert(cursor, value)
    assert(id == 1)
    assert(id2 > id)
  }


  "An Evaluator" should " not create an id that is 0" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val value = language.Val.Str("stuff")

    val evaluator = new Evaluator(1)

    val (id, _, _, _) = evaluator.makeInsert(cursor, value)
    assert(id != 0)
  }
}
