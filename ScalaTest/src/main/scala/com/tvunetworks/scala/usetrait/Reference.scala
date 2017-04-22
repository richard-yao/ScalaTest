package com.tvunetworks.scala.usetrait

/**
 * @author RichardYao
 * @date 2017?4?22?
 */
class Reference[T] {
  
  /**
   * 定义参数contents是各种类型的默认值
   * 数字类型的默认值是 0，Boolean 型的默认值是 false，Unit 类型是()，而所有的对象类型（object type）的默认值为 null
   */
  private var contents: T = _
  
  def set(value: T) {contents = value}
  def get: T = contents
}