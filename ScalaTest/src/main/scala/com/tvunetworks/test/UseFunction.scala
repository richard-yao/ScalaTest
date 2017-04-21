package com.tvunetworks.test

import java.util.Date
import org.scalatest.junit.AssertionsForJUnit

object UseFunction {
  
  def main(args: Array[String]) {
    delayed(displayTime(System.currentTimeMillis()))
    printInt(value2 = 7, value1 = 5)
    useVariableLengthParameters(5,"123","abc")
    println(apply(layout, 10))
    var richard = new Person(21, "richard", true)
    richard.printPersonInfo()
    println(richard.toString())
  }
  
  /**
   * 普通方法传参是传值调用
   */
  def displayTime(time: Long):Long = {
    println("Input parameter is "+time)
    println("The current millisecond ")
    return System.currentTimeMillis()
  }
  
  /**
   * => 用来指定方法传名调用，每次在调用时计算
   * 如果不使用=>,则传入的方法只会执行一次
   */
  def delayed(time: => Long) = {
    println("delayed funciton")
    println("parameters: " + time)
    Thread sleep 100
    println("parameters: " + time)
  }
  
  /**
   * 指定参数名，如果传入的参数名不存在会抛出异常
   */
  def printInt(value1: Int, value2: Int) {
    println("value1 is " + value1)
    println("value2 is " + value2)
  }
  
  /**
   * scala中允许函数的最后一个参数是可变长的
   */
  def useVariableLengthParameters(length: Int, args: String*) {
    println("Input length is " + length)
    for(temp <- args) {
      println("parameter is " + temp)
    }
  }
  
  /**
   * scala中使用递归计算阶乘
   */
  def countFactorial(n: BigInt): BigInt = {
    if(n <= 1)
      return 1
    else 
      return n * countFactorial(n - 1)
  }
  
  /**
   * scala中可以指定函数的默认参数，不传参数调用会使用默认参数
   */
  def useDefaultPara(value1: Int = 5, value2: Int = 7) {
    println(value1 + value2)
  }
  
  /**
   * 传名调用函数f，限制f的输入类型是Int，输出结果是String
   * 对于简单调用，scala允许直接以=使用方法
   */
  def apply(f: Int => String, v: Int) = f(v)
  
  /**
   * 函数layout输入类型为A，类似java泛型使用
   */
  def layout[A](x: A) = "[" + x.toString() + "]"
  
  /**
   * scala支持函数内嵌套调用函数
   */
  def factorial(number: Int): Int = {
    def fact(temp: Int): Int = {
      if(temp <= 1)
        return 1
      else 
        return temp * fact(temp - 1)
    }
    return fact(number)
  }
  
  /**
   * 自定义匿名函数
   */
  def useNonameMethod() {
    var inc = (x: Int) => x + 1
    var mul = (x: Int, y: Int) => x*y
    var print = () => {println("nothing")}
    println(inc(5))
    println(mul(10, 2))
    print()
  }
  
  /**
   * scala偏应用函数，对于有相同参数的某个方法的调用可以用 _ 占位以减少重复参数书写
   */
  def useApplicationFunc() {
    def logData(date: Date, message: String) = {
      println(date + "---" + message)
    }
    val date = new Date
    val logWithDataBound = logData(date, _: String)
    logWithDataBound("Message1")
    Thread.sleep(1000)
    logWithDataBound("Message2")
    Thread.sleep(1000)
    logWithDataBound("Message3")
  }
  
  /**
   * 函数柯里化，干什么的？？
   */
  def useStrcat() {
    def strcat(x: String)(y: String) = {
      x + y
    }
    def intcat(x: Int)(y: Int) = x+y
    println(strcat("hello")("world"))
    println(intcat(1)(3))
  }
  
  def useArray() {
    var array = new Array[String](3)
    array(0) = "123"
    array(1) = "abc"
    array(2) = "456"
    var array2 = Array("123", "abc", "456")
    println(assertEqual(array, array2))
  }
  
  def assertEqual[A](array1: Array[A], array2: Array[A]): Boolean = {
    if(array1.length == array2.length && array1.length != 0 && array2.length != 0) {
      var i = 0;
      for(i <- 0 to (array1.length-1)) {
        if(!String.valueOf(array1(i)).equals(String.valueOf(array2(i)))) {
          return false
        }
      }
      return true
    } else {
      return false
    }
  }
}