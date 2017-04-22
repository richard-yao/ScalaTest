package com.tvunetworks.scala.usetrait

/**
 * @author RichardYao
 * @date 2017?4?22?
 */
object TestUse {
  
  def main(args: Array[String]) {
    var date1 = new DefDate(2017, 4,10)
    var date2 = new DefDate(2018, 4,10)
    var date3 = new DefDate(2017, 4, 22)
    println(date1.equals(date2))
    println(date1 < "123")
  }
}