package app.document.context

/**
  * Created by victoraxelsson on 2017-05-24.
  */
class Timestamp(c:Int, p:Int){

  def getC() = c
  def getP() = p

   def isGreaterThan(ts:Timestamp): Boolean= {
     if(c == ts.getC()){
       return ts.getP() > p
     }

     return ts.getC() > c
  }

  def isLessThan(ts:Timestamp): Boolean= {
    if(c == ts.getC()){
      return ts.getP() < p
    }

    return ts.getC() < c
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
