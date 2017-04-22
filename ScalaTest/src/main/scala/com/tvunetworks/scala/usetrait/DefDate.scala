package com.tvunetworks.scala.usetrait

/**
 * @author RichardYao
 * @date 2017?4?22?
 */
class DefDate(y: Int, m: Int, d: Int) extends Ord {
  
  def year = y
  def month = m
  def day = d
  
  override def < (that: Any): Boolean = {
    if(! that.isInstanceOf[DefDate])
      error("Cannot compare " + that + " and a DefDate")
    val o = that.asInstanceOf[DefDate]
    (year < o.year) || (year == o.year && (month < o.month || (month == o.month && day < o.day)))
  }
  
  override def toString(): String = year + "-" + month + "-" + day
  
  /**
   * isInstanceOf用来判断参数是否是某个对象实例
   * asInstanceOf则是将某个对象进行强制类型转换
   */
  override def equals(that: Any): Boolean = that.isInstanceOf[DefDate] && {
      val o = that.asInstanceOf[DefDate]
      o.day == day && o.month == month && o.year == year
  }
}