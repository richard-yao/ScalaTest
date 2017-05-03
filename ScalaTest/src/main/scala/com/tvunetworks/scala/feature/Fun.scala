package com.tvunetworks.scala.feature

import scala.io.Source
import java.io.File
import java.io.PrintWriter
import java.util.Date

object Fun {

  def main(args: Array[String]) {
      println(++(9))
      var arrs = Array("what", "can", "I", "do", "for", "you")
      arrs.foreach(println)
      useTurple
      readFile("E:/test.txt")
      for(c <- "abcdefg") println(c)
      useMatcher()
  }
  def ++(value: Int): Int = {
      var result = value + 1
      result
  }
  //元组对应数学概念上的矢量，是可以有多重不同数据类型的元素的，并且元组的值位置从1开始
  def useTurple() {
      var pair = (99,.8,"Naive",Array(1,2,3))
      println(pair._1,pair._2,pair._3,pair._4.foreach(println))
  }

  def readFile(path: String) {
      if(path.length > 0) {
          for(line <- Source.fromFile(path).getLines()) 
              println(line.length + "-" + line)
      } else {
          Console.err.println("Please input valid file")
      }
  }

  def checkValue(b: Byte): Int = {
      if(b > 0)
          b & 0xFF
      else
          ~(b & 0xFF) + 1 
  }
  
  def testNestedForLoop(dir: String) {
    val files = (new File("/")).listFiles()
    def fileLines(file: File) = Source.fromFile(file).getLines.toList
    def grep(pattern: String) = {
      for(file <- files
          if file.isFile
          if file.getName.endsWith("config.xml")
        )
        for(line <- fileLines(file)
            if line.trim.matches(pattern)
            )
          println(file + ":" + line)
    }
    grep(".*9000.*") // matched regex like .*xxx
  }
  
  // scala中并没有continue or break的控制函数，因此需要以递归方式返回指定过滤条件下的对象
  def testForWithoutContinue() {
    val files = (new File("/")).listFiles
    def searchXml(i: Int): String = {
      if(i >= files.length) "Empty"
      else if(!files(i).isFile()) searchXml(i+1)
      else if(files(i).getName.endsWith(".xml")) files(i).getName
      else searchXml(i+1)
    }
    println("The first .xml file is "+searchXml(0))
  }
  
  def useClosePackage() {
    //相当于定义了两个参数的一个函数, 函数柯里化
    def makeIncrease(more: Int) = (x: Int) => x + more
    val incrOne = makeIncrease(1)
    val incr999 = makeIncrease(999)
    println(incrOne(10))
    println(incr999(10))
  }
  
  def usePrintArgs() {
    def printArgs(args: String*) = for(arg <- args) println(arg)
    printArgs("123","abc")
    val arrs = Array("123","abc")
    printArgs(arrs: _*) //将默认传入的数组以每个元素的形式传入到方法中, 直接传入数组会出错
  }
  
  /**
   * 函数参数可以指定缺省值，调用函数时可以通过传递命名参数的方式指定传参
   */
  def usePrintTime() {
    def printTime(divisor: Int = 1) = println(System.currentTimeMillis() / divisor)
    printTime()
    printTime(divisor = 1000)
  }
  
  def useMatcher() {
    val files = (new File("/")).listFiles
    def fileMatching(matcher: String => Boolean) = {
      for(file <- files; if file.isFile; if matcher(file.getName)) yield file
    }
    def filesEnding(query: String) = fileMatching(_.endsWith(query))
    def filesContain(query: String) = fileMatching(_.contains(query))
    val arrayFiles = filesEnding(".xml")
    arrayFiles.foreach(println)
    val arrayFilter = filesContain("test")
    arrayFilter.foreach(println)
  }
  
  def useCurried() {
    def curriedSum(x: Int)(y: Int) = x+y
    def onePlus = curriedSum(1)_
    def twoPlus = curriedSum(2)_
    println(onePlus(2))
    println(twoPlus(4))
  }
  
  def withPrintWriter(file: File)(op: PrintWriter => Unit) {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }
  
  def useWithPrintWriter() {
    def funWriter(writer: PrintWriter): Unit = {
      writer.print(new Date)
    }
    val file = new File("/config.xml")
    withPrintWriter(file)(writer => writer.print(new Date))
    //等价于下面这种
    withPrintWriter(file)(funWriter)
  }
}
