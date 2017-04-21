package com.tvunetworks.test

/**
 * @author RichardYao
 * @date 2017?4?21?
 */
object UseMatch {
  
  def main(args: Array[String]) {
    println(useMatch(1))
    println(useMatch("two"))
    println(useMatch(8))
    println(useMatch("123"))
  }
  
  /**
   * scala中模式匹配类似于switch语句
   */
  def useMatch(param: Any): Any = param match {
    case 1 => "one"
    case "two" => 2
    case _: Int => "scala.Int"
    case _ => "many"
  }
}