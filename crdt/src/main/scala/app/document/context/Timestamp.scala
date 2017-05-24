package app.document.context

/**
  * Created by victoraxelsson on 2017-05-24.
  */
class Timestamp(c:Int, p:Int) {

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

   def equals(obj: Timestamp): Boolean = {
    return (obj.getP() == p) && (obj.getC() == c)
  }
}
