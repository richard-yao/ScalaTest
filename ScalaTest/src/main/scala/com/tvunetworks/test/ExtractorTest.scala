package com.tvunetworks.test

/**
 * @author RichardYao
 * @date 2017?4?22?
 */
object ExtractorTest {
  
  def main(args: Array[String]) {
    println("Use Apply function: " + ExtractorTest("richard", "gmail.com"))
    println("Use Unapply function: " + unapply("123@qq.com"))
    println("Use Unapply function: " + unapply("richard yao"))
    
    val test = ExtractorTest("no-reply", "gmail.com")
    println(test)
    test match {
      case ExtractorTest(str) => println(test + " is not equal " + str)
      case _ => println("how to use this?")
    }
  }
  
  /**
   * 注入方法(可选)
   */
  def apply(user: String, domain: String) = {
    user + "@" + domain
  }
  
  /**
   * 提取方法(必填)
   * 提取器是从传递给它的对象中提取出构造该对象的参数
   */
  def unapply(str: String): Option[(String, String)] = {
    val parts = str split "@"
    if(parts.length == 2) {
      Some(parts(0), parts(1))
    } else {
      None
    }
  }
}