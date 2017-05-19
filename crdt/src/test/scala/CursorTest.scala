import app.document.cursor.Cursor
import app.document.cursor.Key.{RootMapT, listT, mapT}
import app.document.language.Expr
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
  "A Cursor" should " keep the keys in the same order as appended " in {
    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val addList = listT("myList")
    val addMap = mapT("myMap1");
    val addMap2 = mapT("myMap2");

    cursor = cursor.append(addMap)
    cursor = cursor.append(addList)
    cursor = cursor.append(addMap2)

    assert(cursor.getKeys().size == 3)
    assert(cursor.getKeys().head == key)
    assert(cursor.getTail() == addMap2)
  }
}
