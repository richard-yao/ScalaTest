package com.tvunetworks.scala.usetrait

trait Ord {
  //抽象方法待实现
  def < (that: Any): Boolean
  //其他断言依赖第一个断言的实现
  def <=(that: Any): Boolean = (this < that) || (this == that)
  def > (that: Any): Boolean = !(this <= that)
  def >=(that: Any): Boolean = !(this < that)
}