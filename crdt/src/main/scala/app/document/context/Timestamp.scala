package app.document.context

/**
  * Created by victoraxelsson on 2017-05-24.
  */
class Timestamp(c:Int, p:Int){

  def getC() = c
  def getP() = p

   def isGreaterThan(ts:Timestamp): Boolean= {
    if(p == ts.getP()){
      return c > ts.getC()
    }

    return c > ts.getC()
  }

  def isLessThan(ts:Timestamp): Boolean= {
    if(p == ts.getP()){
      return c < ts.getC()
    }

    return c < ts.getC()
  }

  override def equals(obj: scala.Any): Boolean = {
    if(!obj.isInstanceOf[Timestamp]){
      return false
    }

    return (obj.asInstanceOf[Timestamp].getP() == p) && (obj.asInstanceOf[Timestamp].getC() == c)
  }

  override def hashCode(): Int = {
    (c + ":" + p).hashCode
  }
}
