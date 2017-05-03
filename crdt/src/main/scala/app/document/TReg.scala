package app.document

/**
  * Created by victoraxelsson on 2017-05-03.
  */
class TReg(var value: Any) extends DocItem{


  override def assign(tReg: TReg): Unit = {
    value = tReg.value
  }


  override def toString: String = value.toString

  override def getType(): String = "treg"
}
