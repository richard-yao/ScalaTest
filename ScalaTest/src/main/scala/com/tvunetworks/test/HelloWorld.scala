package com.tvunetworks.test

import java.util.Date
import java.text.SimpleDateFormat
import org.apache.log4j.Logger
import org.apache.log4j.xml.DOMConfigurator

object HelloWorld {
  
  val logger: Logger = Logger.getLogger("HelloWorld")
  /**
   * The first scala program
   */
  def main(args: Array[String]): Unit = {
    DOMConfigurator.configure("log4j.xml");
    logger.info("Use log4j to print out log")
    println("Hello world!")
    println("Hello world!")
    testMethod(args)
    var array = Array("123", "abc")
    testMethod(array)
    testString()
    testVarVal()
    val addOne = (x: Int) => x +1 //scala中匿名函数
    println(addOne(5))
    val date = new Date();
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    println(df format date)
    
    val com = new Complex(1.2, 3.5)
    println("Complex real value: " + com.re() + " and imaginary value is "+com.im)
    
    val timer = new Timer();
    timer.executeTimer()
    testWhile(5)
    println(System.currentTimeMillis())
  }
  
  /**
   * Scala中for循环
   */
  def testMethod(args: Array[String]): Unit = {
    if(args.length > 0) {
      var a = ""
      for(a <- args) {
        println("Input parameter value is " + a)
      }
    }
  }
  
  /**
   * scala中"""用来表示一个字符串
   */
  def testString(): Unit = {
    val input = """abc 456 789"""
    println(input)
  }
  
  /**
   * var用来声明变量，val用来声明常量
   */
  def testVarVal(): Unit = {
    var age = 0
    val age1 = 20
    if(age == 0)
      age = 13
    else 
      age = 20
    println(age + age1)
  }
  
  def testWhile(number1: Int): Int = {
    var sum = 0;
    var number = number1
    while(number > 0) {
      sum += number
      number -= 1
    }
    return sum
  }
  
  def testDoWhile(times: Int): Int = {
    var sum = 0
    var times1 = times
    do {
      sum += times1
      times1-=1
    } while(times1 > 0)
    return sum
  }
  
  def testFor(number1: Int, number2: Int) {
    var temp = 0
    for(temp <- 0 to number1) { //for循环中to可以到最大值
      println("value of " + temp)
    }
    for(temp <- 0 until number1) { //for循环中until到比最大值小一
      println("value of " + temp)
    }
    var temp1 = 0
    for(temp <- 0 to number1; temp1 <- 0 to number2) {
      println("value of number1 " + temp+" and value of number2 " + temp1)
    }
  }
  
  /**
   * scala的for循环中可以加上if条件语句
   */
  def testListFor() {
    var temp = 0
    var numList = List(1,2,3,4,5,6,7,8,9)
    for(temp <- 0 to numList.length 
        if(temp !=3);if(temp !=7)) {
      println("value of " + temp)
    }
  }
  
  /**
   * for...yield返回的是一个集合
   */
  def testListForYield() {
    var temp = 0
    var numList = List(1,2,3,4,5,6,7,8,9)
    var relval = for { temp <- numList
      if temp != 3; if temp !=7
    }yield temp
    for(temp <- relval) {
      println("value of " + temp)
    }
  }
}