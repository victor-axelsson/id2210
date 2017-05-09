import app.document.context.{NodeDoc, NodeList, NodeMap, NodeReg}
import app.document.language
import app.document.language.Val
import org.scalatest.FlatSpec

/**
  * Created by Nick on 5/9/2017.
  */
class NodeTest extends FlatSpec {
  "A node" should "be able to print itself" in {
    var doc = new NodeDoc(Map.empty)
    var list = new NodeList("list", Map.empty)
    var map = new NodeMap("map", Map.empty)
    var values = List[Val]()
    values = values :+ language.Val.Number(5)
    var reg = new NodeReg("reg", values, Map.empty)
    doc.children += list.getName() -> list
    doc.children += map.getName() -> map
    doc.children += reg.getName() -> reg

    val expected = "{\"doc\":{\"list\":[],\"reg\":\"Number(5)\",\"map\":{}}}"
    println(expected)
    println(doc.toString())
    assert(doc.toString() == expected)
  }

  "A node" should "be able to print itself stuff" in {
    var doc = new NodeDoc(Map.empty)
    var list = new NodeList("list", Map.empty)
    var map = new NodeMap("map", Map.empty)

    var values1 = List[Val]()
    values1 = values1 :+ language.Val.Number(5)
    var reg1 = new NodeReg("reg1", values1, Map.empty)

    var values2 = List[Val]()
    values2 = values2 :+ language.Val.Number(2)
    var reg2 = new NodeReg("reg2", values2, Map.empty)

    map.children += reg1.getName() -> reg1
    map.children += reg2.getName() -> reg2

    list.children = list.children :+ map
    doc.children += list.getName() -> list

    val expected = "{\"doc\":{\"list\":[\"map\":{\"reg2\":\"Number(2)\",\"reg1\":\"Number(5)\"}]}}"
    println(expected)
    println(doc.toString())
    assert(doc.toString() == expected)
  }
}
