package com.tvunetworks.test

class Timer {
  
  /**
   * () => Unit表示无参数无返回的函数
   */
  def oncePerSeconds(callback: () => Unit) {
    for(a <- 1 to 10) {
      callback()
      Thread sleep 1000
    }
  }
  
  def executeTimer() {
    oncePerSeconds(() => { //匿名函数，()表示无参数，{}内是函数内容
      println("Time flies like an arrow...")
    })
  }
}