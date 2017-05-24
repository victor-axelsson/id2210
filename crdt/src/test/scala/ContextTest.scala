import app.document.context.{Context, NodeDoc, Timestamp}
import app.document.cursor.Cursor
import app.document.cursor.Key.{RootMapT, listT, mapT, regT}
import app.document.evaluator.{Evaluator, Operation}
import app.document.language.{Expr, Val}
import org.scalatest.FlatSpec





/**
  * Created by victoraxelsson on 2017-05-06.
  */
class ContextTest extends FlatSpec{
  "A context" should "be able to perform DESCEND" in {

    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.mutable.HashMap[Timestamp, Operation]())
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

    var op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    //var op = eval.makeAssign(cursor, new Val.EmptyMap());

    var newContext:Context = context.apply(op)
    assert(newContext.op.getCursor().getKeys().size == 0)
    assert(newContext.op.getCursor().getTail().equals(reg3))
  }
  "A context" should "be able to perform ASSIGN" in {



    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.mutable.HashMap[Timestamp, Operation]())
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

    var op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    var newContext:Context = context.apply(op)

    var expectedOutput = "{\"doc\":{\"someVar1\":{\"someVar2\":{\"someVar4\":\"someVal4\"}}}}"

    assert(newContext.op.getCursor().getKeys().size == 0)
    assert(newContext.op.getCursor().getTail().equals(reg3))
    assert(newContext.getDoc().toString() == expectedOutput)
  }
  "A context" should "be able to perform EMPTY-MAP" in {

    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.mutable.HashMap[Timestamp, Operation]())
    var context = new Context(nodeDoc)
    var eval:Evaluator = new Evaluator(1)

    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val addMap1 = mapT("someVar1")
    val addMap2 = mapT("someVar2")
    val addMap3 = mapT("someVar4")

    cursor = cursor.append(addMap1)
    cursor = cursor.append(addMap2)
    cursor = cursor.append(addMap3)

    var op = eval.makeAssign(cursor, Val.EmptyMap);
    var newContext:Context = context.apply(op)

    var expectedOutput = "{\"doc\":{\"someVar1\":{\"someVar2\":{\"someVar4\":{}}}}}"

    assert(newContext.op.getCursor().getKeys().size == 0)
    assert(newContext.op.getCursor().getTail().equals(addMap3))
    assert(newContext.getDoc().toString() == expectedOutput)
  }
  "A context" should "be able to perform EMPTY-LIST" in {

    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.mutable.HashMap[Timestamp, Operation]())
    var context = new Context(nodeDoc)
    var eval:Evaluator = new Evaluator(1)

    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val addMap1 = mapT("someVar1")
    val addMap2 = mapT("someVar2")
    val addList = listT("someVar4")

    cursor = cursor.append(addMap1)
    cursor = cursor.append(addMap2)
    cursor = cursor.append(addList)

    var op = eval.makeAssign(cursor, Val.EmptyList);
    var newContext:Context = context.apply(op)

    var expectedOutput = "{\"doc\":{\"someVar1\":{\"someVar2\":{\"someVar4\":[]}}}}"

    assert(newContext.op.getCursor().getKeys().size == 0)
    assert(newContext.op.getCursor().getTail().equals(addList))
    assert(newContext.getDoc().toString() == expectedOutput)
  }
  "A context" should "clear out old stuff from deps when using a regT" in {

    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.mutable.HashMap[Timestamp, Operation]())
    var context = new Context(nodeDoc)
    var eval:Evaluator = new Evaluator(1)

    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val addMap1 = mapT("someVar1")
    val addMap2 = mapT("someVar2")
    val addReg = regT("someVar4")

    cursor = cursor.append(addMap1)
    cursor = cursor.append(addMap2)
    cursor = cursor.append(addReg)

    var op = eval.makeAssign(cursor, new Val.Str("someVal4"));
    var newContext:Context = context.apply(op)

    println(newContext.getDoc().toString())
  }
}
