import app.document.cursor.Cursor
import app.document.cursor.Key.{RootMapT, listT}
import app.document.language.Cmd.Let
import app.document.language.{Expr, Var}
import org.scalatest.FlatSpec

/**
  * Created by Nick on 5/3/2017.
  */
class CursorTest extends FlatSpec {
  "A Cursor" should "be able to append keys" in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)
    assert(cursor.getKeys().isEmpty)
    assert(cursor.getTail() == key)

    val addList = listT("myList")
    cursor = cursor.append(addList)

    assert(cursor.getKeys().size == 1)
    assert(cursor.getKeys().head == key)
    assert(cursor.getTail() == addList)
  }
}
