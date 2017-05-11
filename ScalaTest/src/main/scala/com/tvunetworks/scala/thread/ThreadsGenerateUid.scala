package com.tvunetworks.scala.thread

object ThreadsGenerateUid extends App {
  
  var uidCount = 0L
  
  def getUniquId() = this.synchronized {
    val freshUid = uidCount + 1
    uidCount = freshUid
    freshUid
  }
  
  def printUniqueIds(n: Int): Unit = {
    val uids = for(i <- 0 until n) yield getUniquId()
    println(s"Generated uids: $uids")
  }
  
  class MyThread extends Thread {
    override def run(): Unit = {
      printUniqueIds(5)
    }
  }
  
  val t = new MyThread
  t.start()
  printUniqueIds(5)
  t.join()
}

