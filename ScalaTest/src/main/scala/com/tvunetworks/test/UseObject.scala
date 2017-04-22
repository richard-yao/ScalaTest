package com.tvunetworks.test

/**
 * @author RichardYao
 * @date 2017?4?22?
 */
object UseObject {
  
  def main(args: Array[String]) {
    println(ExtractorTest.unapply("richardyao"))
    
    val obj = UseObject(5)
    println(obj)
    
    obj match {
      /**
       * 当在提取器对象中使用match语句时，unapply将自动执行
       */
      case UseObject(num) => println(obj+" is " + num + " twice more")
      case _ => println("Cannot count")
    }
  }
  
  def apply(x: Int) = x*2
  def unapply(z: Int): Option[Int] = if (z%2 == 0) Some(z/2) else None
}