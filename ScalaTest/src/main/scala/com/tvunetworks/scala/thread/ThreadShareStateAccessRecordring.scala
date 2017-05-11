package com.tvunetworks.scala.thread

object ThreadShareStateAccessRecordring extends App {
  for(i <- 0 until 100000) {
    var a = false
    var b = false
    var x = -1
    var y = -1
    
    val t1 = new Thread(new Runnable() {
      override def run(): Unit = {
        a = true
        y = if(b) 0 else 1
      }
    })
    
    val t2 = new Thread(new Runnable() {
      override def run(): Unit = {
        b = true
        x = if(a) 0 else 1
      }
    })
    
    t1.start()
    t2.start()
    assert(!(x == 1 && y ==1), s"x = $x, y = $y")
  }
}