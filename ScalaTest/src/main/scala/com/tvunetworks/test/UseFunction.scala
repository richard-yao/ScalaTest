package com.tvunetworks.test

object UseFunction {
  
  def main(args: Array[String]) {
    delayed(displayTime(System.currentTimeMillis()))
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
   */
  def delayed(time: => Long) = {
    println("delayed funciton")
    println("parameters: " + time)
    Thread sleep 100
    println("parameters: " + time)
  }
}