package com.tvunetworks.scala.feature

import scala.collection.mutable.ListBuffer

/**
 * @author RichardYao
 * @date 2017?5?10?
 */
object PerfectNumber {
  
  def sumOffNumbers(number: Int): Int = {
    (1 /: (2 until number)) { (sum, i) =>
      if(number % i ==0)
        sum +i
      else 
        sum
    }
  }
  
  def isPerfect(num: Int): Boolean = {
    num == sumOffNumbers(num)
  }
  
  def findPerfectNumbers(start: Int, end: Int) = {
    require(start > 1 && end >= start)
    val perfectNumbers = new ListBuffer[Int]
    (start to end).foreach(num => if(isPerfect(num)) perfectNumbers += num)
    perfectNumbers.toList
  }
  
  def main(args: Array[String]): Unit = {
    val list = findPerfectNumbers(2, 100)
    println("\nFound Perfect Numbers:" + list.mkString(","))
    useMyThread()
  }
  
  class MyThread extends Thread {
	  override def run(): Unit = {
	    (0 to 100).map(i => {
	      println(s"Number $i is execute")
	      Thread.sleep(1)
	    })
	  }
  }
  
  def useMyThread() {
    val t = new MyThread
    t.start()
    t.join() //如果没有join调用，则该线程很可能在下面println之后执行
    println("Which is first execute!")
  }
}