import app.document.context.{Context, Node, NodeDoc}
import app.document.cursor.Cursor
import app.document.cursor.Key.{RootMapT, listT, mapT, regT}
import app.document.evaluator.{Evaluator, Operation}
import app.document.language.Expr._
import app.document.language.Val.{EmptyMap, Str}
import app.document.language.{Expr, Val}
import org.scalatest.FlatSpec

/**
  * Created by victoraxelsson on 2017-05-16.
  */
class ExpressionEvaluationTest extends FlatSpec{
  "A get expression" should " create a cursor without side effects" in {

    //// Build up the document ////
    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]())
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
    val op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    val newContext:Context = context.apply(op)

    //// END Build up the document ////

    val expr1:Expr = Get(Doc(), "someVar1")
    val expr2:Expr = Get(expr1, "someVar2")
    val expr3:Expr = Get(expr2, "someVar4")

    var c:Cursor = eval.evalExpr(expr1).evalExpr(expr2).evalExpr(expr3).toCursor()

    //Check for right structure
    assert(c.getKeys().size == 3)
    assert(c.getKeys().head.isInstanceOf[RootMapT])
    assert(c.getKeys().tail.head.isInstanceOf[mapT])
    assert(c.getKeys().tail.tail.head.isInstanceOf[mapT])
    assert(c.getTail().isInstanceOf[regT])

    //Check for side effects on the original cursor
    assert(eval.toCursor().getKeys().size == 0)
    assert(eval.node == null)

  }
  "A KEY1 expression" should " return the keys in a given map" in {

    //// Build up the document ////
    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]())
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
    val op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    val newContext:Context = context.apply(op)

    //// END Build up the document ////

    val expr1:Expr = Get(Doc(), "someVar1")
    val expr2:Expr = Get(expr1, "someVar2")
    val expr3:Expr = Keys(expr2)

    val k:scala.collection.mutable.Set[String] = eval.evalExpr(expr1).evalExpr(expr2).evalExpr(expr3).toKeys()

    //Check that the key was found
    assert(k.size == 1)
    assert(k.contains("someVar4"))

    //Check for side effects
    assert(eval.toKeys().size == 0)

  }
  "A KEY2 expression" should " return the keys in a given map from a size = 0 cursor, given a context" in {

    //// Build up the document ////
    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]())
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
    val op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    val newContext:Context = context.apply(op)

    // We are looking for: someVar2
    val searchCursor:Cursor = new Cursor(scala.collection.immutable.List(), addMap2)

    //Give the context and query for the keys
    val k:scala.collection.mutable.Set[String] = searchCursor.keys(newContext)

    //Check that the key was found
    assert(k.size == 1)
    assert(k.contains("someVar4"))

    //Check for side effects
    assert(eval.toKeys().size == 0)
  }

  "A KEY3 expression" should " return the keys in a given map from a size > 0 cursor, given a context" in {

    //// Build up the document ////
    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]())
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
    val op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    val context2:Context = context.apply(op)

    val addMap3 = mapT("someVar3")
    var cursor2 = new Cursor(keys, key)
    cursor2 = cursor2.append(addMap3)
    val op2 = eval.makeAssign(cursor2, EmptyMap)
    val context3:Context = context2.apply(op2)

    // We are looking for: someVar2
    var searchCursor:Cursor = new Cursor(scala.collection.immutable.List(), key)
    searchCursor = searchCursor.append(addMap1)
    searchCursor = searchCursor.append(addMap2)

    //Check that the cursor has elements, since we are testing KEY3
    assert(searchCursor.getKeys().size > 0)

    //Give the context and query for the keys
    val k:scala.collection.mutable.Set[String] = searchCursor.keys(context3)

    //Check that the key was found
    assert(k.size == 1)
    assert(k.contains("someVar4"))

    //Check for side effects
    assert(eval.toKeys().size == 0)
  }
  "A VAL1 expression" should " return the value in a given regT" in {

    //// Build up the document ////
    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]())
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
    val op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    val context2:Context = context.apply(op)
    /// END building the doc

    val expr1:Expr = Get(Doc(), "someVar1")
    val expr2:Expr = Get(expr1, "someVar2")
    val expr3:Expr = Get(expr2, "someVar4")
    var expr4:Expr = Values(expr3)

    val v:List[Val] = eval.evalExpr(expr1).evalExpr(expr2).evalExpr(expr3).evalExpr(expr4).toVals()

    assert(v.size == 1)
    assert(v.head.isInstanceOf[Str])
    assert(v.head.asInstanceOf[Str].getVal() == "someVal4")

  }
  "A VAL2 expression" should " return the values in a given regT from a cursor with only a key" in {

    //// Build up the document ////
    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]())
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
    val op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    val context2:Context = context.apply(op)

    var searchCursor:Cursor = new Cursor(scala.collection.immutable.List(), reg3)

    //Check that the cursor has elements, since we are testing VAL2
    assert(searchCursor.getKeys().size <= 0)

    //Give the context and query for the values
    val v:List[Val] = searchCursor.values(context2)

    //Check that is was found
    assert(v.size == 1)
    assert(v.head.isInstanceOf[Str])
    assert(v.head.asInstanceOf[Str].getVal() == "someVal4")
  }
  "A VAL3 expression" should " return the values in a given regT from a cursor" in {


    //// Build up the document ////
    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]())
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
    val op = eval.makeAssign(cursor, new Val.Str("someVal4"))
    val context2:Context = context.apply(op)


    //Check that the cursor has elements, since we are testing VAL2
    assert(cursor.getKeys().size > 0)


    //Give the context and query for the values
    val v:List[Val] = cursor.values(context2)

    //Check that is was found
    assert(v.size == 1)
    assert(v.head.isInstanceOf[Str])
    assert(v.head.asInstanceOf[Str].getVal() == "someVal4")
  }

  "An IDx expression" should " return null for empty listT" in {

    //// Build up the document ////
    var nodeDoc:NodeDoc = new NodeDoc(new scala.collection.immutable.HashMap[Int, Operation]())
    var context = new Context(nodeDoc)
    var eval:Evaluator = new Evaluator(1)

    val doc = Expr.Doc()
    val key = RootMapT(doc)
    val keys = scala.collection.immutable.List()
    var cursor = new Cursor(keys, key)

    val addMap1 = mapT("someVar1")
    val addMap2 = mapT("someVar2")
    val list1 = listT("someVar4")

    cursor = cursor.append(addMap1)
    cursor = cursor.append(addMap2)
    cursor = cursor.append(list1)
    val op = eval.makeAssign(cursor, Val.EmptyList)
    val context2:Context = context.apply(op)
    /// END building the doc
    // We are looking for: someVar2

    val searchCursor:Cursor = new Cursor(scala.collection.immutable.List(), list1)

    assert(searchCursor.idx(context, 0) == null)

  }
}
