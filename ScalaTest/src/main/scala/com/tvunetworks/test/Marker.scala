package com.tvunetworks.test

/**
 * @author RichardYao
 * @date 2017?4?21?
 */
//私有构造方法
class Marker private(var color: String) {
  
  /*
   * Marker的实例化会执行该语句，隐式调用toString()方法
   */
  println("create " + this)
  
  override def toString(): String = "marked color:" + color
}

//伴生对象，与类共享名字
object Marker {
  
  private val markers: Map[String, Marker] = Map(
    "red" -> new Marker("red"),
    "blue" -> new Marker("blue"),
    "green" -> new Marker("green")
  )
  
  /**
   * apply方法可以被Marker(xxx)隐式调用
   */
  def apply(color: String) = {
    if(markers.contains(color)) markers(color) else null
  }
  
  def getMarker(color: String) = {
    if(markers.contains(color)) markers(color) else null
  }
  
  def main(args: Array[String]) = {
    println(Marker("green"))
    val marker = new Marker("purple")
    println(marker.toString())
    println(Marker.getMarker("blue"))
  }
}