package com.tvunetworks.test

trait Equal {
  
  def isEqual(x: Any): Boolean
  def isNotEqual(x: Any): Boolean = !isEqual(x)
}

class Point(xc: Int, yc: Int) extends Equal {
  
  var x = xc
  var y = yc
  
  /**
   * 这里如果不指定返回类型就变成了无参函数会导致继承的Equal在编译期无法通过
   */
  def isEqual(obj: Any): Boolean = {
    obj.isInstanceOf[Point] &&
    obj.asInstanceOf[Point].x == x
  }
}

object Test {
  
  def main(args: Array[String]) {
    val p1 = new Point(2, 3)
    val p2 = new Point(2, 4)
    val p3 = new Point(3, 4)
    
    println(p1.isNotEqual(p2))
    println(p1.isNotEqual(p3))
    println(p2.isNotEqual(p3))
  }
}