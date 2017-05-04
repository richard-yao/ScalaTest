package com.tvunetworks.scala.accesslog.pushlive

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date
import java.util.Locale

/**
 * @author RichardYao
 * @date 2017?5?3?
 */
class CloudLiveAccessLog(line: String) extends Serializable {
  
  require(line != null && !line.equals(""))
  private val splitWord = splitLine2Word(line)
  val remoteIp = splitWord(0)
  val requestTime = splitWord(3).filter(ch => ch != '[')
  val requestZone = splitWord(4).filter(ch => ch != ']')
  val formatTime = converTime(requestTime, requestZone)
  val requestMethod = splitWord(5).filter(ch => ch != '"')
  val requestAddress = splitWord(6)
  val serverType = splitWord(7).filter(ch => ch != '"') // HTTP/1.1
  val requestResult = splitWord(8)
  val requestInterval = splitWord(9) // ms
  
  override def toString = remoteIp+","+requestMethod+","+requestAddress+","+requestResult
  
  //10.12.22.101 - - [02/May/2017:02:49:39 +0000] "POST /pushlive/admin/getCloudDevices4CC.action HTTP/1.1" 200 242
  private def splitLine2Word(line: String): Array[String] = {
    line.split(" ")
  }
  
  private def converTime(requestTime: String, requestZone: String): String = {
    try {
      val pattern = "dd/MMM/yyyy:hh:mm:ss"
      val sdf = new SimpleDateFormat(pattern, Locale.ENGLISH)
      val timezone = TimeZone.getTimeZone(requestZone)
      sdf.setTimeZone(timezone)
      val utcTime = sdf.parse(requestTime).getTime
      convertTimeToStr(utcTime)
    } catch {
      case ex: Exception => println("Convert time wrong! the time is "+requestTime);requestTime
    }
  }
  
  //转化为日期时间
  private def convertTimeToStr(timestamp: Long): String = {
    val pattern = "yyyy-MM-dd"
    val sdf = new SimpleDateFormat(pattern)
    sdf.format(new Date(timestamp))
  }
}