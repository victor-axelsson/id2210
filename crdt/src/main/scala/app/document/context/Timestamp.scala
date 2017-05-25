package app.document.context

/**
  * Created by victoraxelsson on 2017-05-24.
  */
class Timestamp(c:Int, p:Int){

  var saved:Timestamp = null

  def preserveHashCode(timestamp: Timestamp) = {
    saved = timestamp
  }

  def getC() = c
  def getP() = p

   def isGreaterThan(ts:Timestamp): Boolean= {
     if(c == ts.getC()){
       return ts.getP() > p
     }

     return ts.getC() > c
  }

  def isLessThan(ts:Timestamp): Boolean = {
    if(c == ts.getC()){
      return ts.getP() < p
    }

    return ts.getC() < c
  }

  def isConcurrent(ts:Timestamp): Boolean = {
    return c == ts.getC()
  }

  override def equals(obj: scala.Any): Boolean = {
    if(!obj.isInstanceOf[Timestamp]){
      return false
    }


    if (saved == null)
      (obj.asInstanceOf[Timestamp].getP() == p) && (obj.asInstanceOf[Timestamp].getC() == c)
    else
      saved.equals(obj)

  }

  override def hashCode(): Int = {
    if (saved == null)
      (c + ":" + p).hashCode
    else
      saved.hashCode()
  }
}
