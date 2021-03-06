import app.document.evaluator.Evaluator
import app.document.language.Cmd.{Assign, Delete, InsertAfter, Let}
import app.document.language.Expr.{Doc, Get, Idx, Var}
import app.document.language.Val.{EmptyList, EmptyMap, Number, Str}
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
    var cmd1 = Assign(EmptyMap)


    //eval.evalExpr(Get("someVar")).evalExpr(Get("someVar2")).evalCmd(cmd1)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalCmd Assign(EmptyMap)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":{}}}}"

    assert(s == expectedOutput)
  }
  "An assign cmd with an empty list" should " set the state of the eval Ap" in {

    val eval = new Evaluator(1)
    var cmd1 = Assign( EmptyList)


    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalCmd(cmd1)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[]}}}"

    assert(s == expectedOutput)
  }
  "An assign cmd with a reg " should " set the state of the eval Ap" in {

    val eval = new Evaluator(1)
    var cmd1 = Assign(new Str("MyVal"))


    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalCmd(cmd1)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":\"MyVal\"}}}"

    assert(s == expectedOutput)
  }
  "An insertAfter cmd " should " set the state of the eval Ap" in {


    val eval = new Evaluator(1)
    val insertSomeIndex = InsertAfter(new Str("someIndex"))

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex\"]}}}"

    assert(s == expectedOutput)
  }
  "An insertAfter cmd with insertion at the tail" should " set the state of the eval Ap" in {


    val eval = new Evaluator(1)
    val insertSomeIndex1 = InsertAfter(new Str("someIndex1"))
    val insertSomeIndex2 = InsertAfter(new Str("someIndex2"))
    val insertSomeIndex3 = InsertAfter(new Str("someIndex3"))
    val insertSomeIndex4 = InsertAfter(new Str("someIndex4"))

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex1

    var s:String = eval.toJsonString()
    var expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex1\"]}}}"
    assert(s == expectedOutput)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(1) evalCmd insertSomeIndex2

    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex1\",\"[1]\":\"someIndex2\"]}}}"
    assert(s == expectedOutput)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(2) evalCmd insertSomeIndex3

    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex1\",\"[1]\":\"someIndex2\",\"[2]\":\"someIndex3\"]}}}"
    assert(s == expectedOutput)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(3) evalCmd insertSomeIndex4
    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex1\",\"[1]\":\"someIndex2\",\"[2]\":\"someIndex3\",\"[3]\":\"someIndex4\"]}}}"
    assert(s == expectedOutput)
  }
  "An insertAfter cmd with insertion at the head" should " set the state of the eval Ap" in {


    val eval = new Evaluator(1)
    val insertSomeIndex1 = InsertAfter(new Str("someIndex1"))
    val insertSomeIndex2 = InsertAfter(new Str("someIndex2"))
    val insertSomeIndex3 = InsertAfter(new Str("someIndex3"))
    val insertSomeIndex4 = InsertAfter(new Str("someIndex4"))

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex1

    var s:String = eval.toJsonString()
    var expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex1\"]}}}"
    assert(s == expectedOutput)
    println(s)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex2


    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex2\",\"[1]\":\"someIndex1\"]}}}"
    assert(s == expectedOutput)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex3

    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex3\",\"[1]\":\"someIndex2\",\"[2]\":\"someIndex1\"]}}}"
    assert(s == expectedOutput)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex4
    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex4\",\"[1]\":\"someIndex3\",\"[2]\":\"someIndex2\",\"[3]\":\"someIndex1\"]}}}"
    assert(s == expectedOutput)
  }
  "An insertAfter cmd with insertion in the middle" should " set the state of the eval Ap" in {


    val eval = new Evaluator(1)
    val insertSomeIndex1 = InsertAfter(new Str("someIndex1"))
    val insertSomeIndex2 = InsertAfter(new Str("someIndex2"))
    val insertSomeIndex3 = InsertAfter(new Str("someIndex3"))
    val insertSomeIndex4 = InsertAfter(new Str("someIndex4"))

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex1

    var s:String = eval.toJsonString()
    var expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex1\"]}}}"
    assert(s == expectedOutput)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(1) evalCmd insertSomeIndex2

    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex1\",\"[1]\":\"someIndex2\"]}}}"
    assert(s == expectedOutput)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(2) evalCmd insertSomeIndex3

    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex1\",\"[1]\":\"someIndex2\",\"[2]\":\"someIndex3\"]}}}"
    assert(s == expectedOutput)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(1) evalCmd insertSomeIndex4
    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":\"someIndex1\",\"[1]\":\"someIndex4\",\"[2]\":\"someIndex2\",\"[3]\":\"someIndex3\"]}}}"
    assert(s == expectedOutput)
  }
  "An insertAfter cmd with a mapT" should " set the state of the eval Ap" in {
    val eval = new Evaluator(1)
    val insertSomeIndex1 = InsertAfter(EmptyMap)

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex1

    var s:String = eval.toJsonString()
    var expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":{}]}}}"
    assert(s == expectedOutput)

  }
  "An insertAfter cmd with a mixed types" should " set the state of the eval Ap" in {
    val eval = new Evaluator(1)
    val insertSomeIndex1 = InsertAfter(EmptyMap)
    val insertSomeIndex2 = InsertAfter(EmptyList)
    val insertSomeIndex3 = InsertAfter(new Number(10))

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex1 evalCmd insertSomeIndex2 evalCmd insertSomeIndex3

    var s:String = eval.toJsonString()
    var expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":10,\"[1]\":[],\"[2]\":{}]}}}"
    assert(s == expectedOutput)
  }
  "An insertAfter cmd with a mapT" should " be able to grab an item and modify it" in {
    val eval = new Evaluator(1)
    val insertSomeIndex1 = InsertAfter(EmptyMap)
    val insertreg= Assign(new Str("someVal"))

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalCmd insertSomeIndex1
    var s:String = eval.toJsonString()

    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalExpr Idx(0) evalExpr  Get("someVar3") evalCmd  insertreg

    s = eval.toJsonString()
    var expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":[\"[0]\":{\"someVar3\":\"someVal\"}]}}}"
    assert(s == expectedOutput)
  }
  "A LET cmd " should " take a named snapshot of the eval and VAR should be able to return it" in {

    val eval = new Evaluator(1)

    //Execute the let command
    eval evalExpr Doc() evalExpr Get("someVar") evalExpr Get("someVar2") evalCmd Let("x")

    //Check for side effects on the original cursor
    assert(eval.toCursor().getKeys().size == 0)
    assert(eval.node == null)


    //we should be able to get the snapshot of the state from the eval with a var
    eval evalExpr new Var("x") evalCmd Assign(EmptyMap)

    val s:String = eval.toJsonString()
    val expectedOutput = "{\"doc\":{\"someVar\":{\"someVar2\":{}}}}"

    assert(s == expectedOutput)
  }
  "A DELETE cmd " should " remove any items from a list" in {

    val eval = new Evaluator(1)

    val insertA = InsertAfter(Str("a"))
    val insertB = InsertAfter(Str("b"))
    val insertC = InsertAfter(Str("c"))
    val insertX = InsertAfter(Str("x"))

    //Execute the let command
    eval evalExpr Doc() evalExpr Get("items") evalExpr Idx(0) evalCmd insertA
    eval evalExpr Doc() evalExpr Get("items") evalExpr Idx(1) evalCmd insertB
    eval evalExpr Doc() evalExpr Get("items") evalExpr Idx(2) evalCmd insertC

    var expectedOutput = "{\"doc\":{\"items\":[\"[0]\":\"a\",\"[1]\":\"b\",\"[2]\":\"c\"]}}"

    var s:String = eval.toJsonString()
    assert(s == expectedOutput)

    eval evalExpr Doc() evalExpr Get("items") evalExpr Idx(1) evalCmd Delete()

    s = eval.toJsonString()
    expectedOutput = "{\"doc\":{\"items\":[\"[0]\":\"a\",\"[1]\":\"c\"]}}"

    assert(s == expectedOutput)
  }
}