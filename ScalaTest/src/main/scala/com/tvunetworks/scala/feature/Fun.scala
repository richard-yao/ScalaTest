package com.tvunetworks.scala.feature

import scala.io.Source
import java.io.File
import java.io.PrintWriter
import java.util.Date
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.charset.Charset
import scala.annotation.tailrec
import com.tvunetworks.test.Person
import scala.concurrent.Await
import scala.concurrent.duration.Duration

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
  
  //自动资源管理
  def defineAutoResourceManage() {
    def using[T <: AutoCloseable, R](res: T)(func: T => R): R = {
      try {
        func(res)
      } finally {
        if(res != null)
          res.close()
      }
    }
    
    val allLine = using(Files.newBufferedReader(Paths.get("e:/synclog.txt"), Charset.forName("UTF-8"))) { reader =>
      //该注解用来表明改函数是尾递归，非尾递归使用会抛出异常
      @tailrec
      def readAll(buffer: StringBuilder, line: String): String = {
        if(line == null) 
          buffer.toString()
        else {
          buffer.append(line).append('\n')
          readAll(buffer, reader.readLine())
        }
      }
      readAll(new StringBuilder(), reader.readLine())
    }
    println(allLine)
  }
  
  def useMatchWithImplicitConversion() {
    for {
      x <- Seq(1, false, 2.7, "one", 'fout, new java.util.Date(), new RuntimeException("Runtime exception"))
    } {
      val str = x match {
        case d: Double => s"double: $d" //这里s是隐式apply函数，用于将$表达式用参数代替
        case false => "boolean false"
        case d: java.util.Date => s"java.util.Date: $d"
        case 1 => "int 1"
        case s: String => s"string: $s"
        case symbol: Symbol => s"symbol: $symbol"
        case unexpected => s"unexpected value: $unexpected"
      }
      println(str)
    }
  }
  
  //Use case class to match different result
  def useCaseClass() {
    trait Person
    case class Man(name: String, age: Int) extends Person
    case class Woman(name: String, age: Int) extends Person
    case class Boy(name: String, age: Int) extends Person
    val father = Man("Father", 33)
    val mather = Woman("Mother", 30)
    val son = Man("Son", 7)
    val daughter = Woman("Daughter", 3)
    for(person <- Seq[Person](father, mather, son, daughter)) {
      person match {
        case Man("Father", age) => println(s"father's age is ${age}")
        case man: Man if man.age < 10 => println(s"man is $man")
        case Woman(name, 30) => println(s"${name} is 30 years old")
        case Woman(name, age) => println(s"${name} is ${age} years old")
      }
    }
  }
  
  import scala.concurrent.Future
  import scala.util.{Success, Failure}
  import scala.concurrent.ExecutionContext.Implicits.global
  def useMultiThread() {
    val futures = (1 to 2) map {
      case 1 => Future.successful("1 is odd number")
      case 2 => Future.failed(new RuntimeException("2 is not odd number"))
    }
    futures.foreach(_.onComplete {
      case Success(i) => println(i)
      case Failure(t) => println(t)
    })
    Thread.sleep(2000L)
  }
  
  def useMultiThread2() {
    val futures = (0 until 10).map { i =>
      Future {
        val s = i.toString
        print(s)
        s
      }
    }
    val future = Future.reduce(futures)((x, y) => x+y)
    val result = Await.result(future, Duration.Inf)
  }
  
}
