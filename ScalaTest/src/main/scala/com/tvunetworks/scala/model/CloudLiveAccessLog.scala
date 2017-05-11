package com.tvunetworks.scala.model

import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date
import java.util.Locale
import com.tvunetworks.scala.util.StringUtil

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
  val requestAddress = filterRandomParameter(splitWord(6)) //this request server's resources path 
  val serverType = splitWord(7).filter(ch => ch != '"') // HTTP/1.1
  val requestResult = splitWord(8)
  val requestDatausage: Long = (if(splitWord(9) == "-") 0L else splitWord(9).toLong)// request resource used datausage, unit is B
  val processTime = splitWord(10) //this request used time, unit is second
  
  override def toString = remoteIp+","+requestMethod+","+requestAddress+","+requestResult
  
  //10.12.22.101 - - [02/May/2017:02:49:39 +0000] "POST /pushlive/admin/getCloudDevices4CC.action HTTP/1.1" 200 242
  private def splitLine2Word(line: String): Array[String] = {
    line.split(" ")
  }
  
  def filterRandomParameter(str: String): String = {
    if(StringUtil.isNullEmp(str) || str.indexOf("?") == -1 || str.indexOf("?") == str.length()-1) {
      str
    } else {
      val startLoc = str.indexOf("?")
      val parameters = str.substring(startLoc+1, str.length())
      val parasArray = parameters.split('&').filter(para => para.indexOf("r=") == -1)
      if(parasArray != null && parasArray.length > 0) {
      	str.substring(0, startLoc)+"?"+parasArray.reduce((a, b) => a+"&"+b)
      } else {
      	str.substring(0, startLoc)
      }
    }
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

object Test {
  def main(str: Array[String]) {
    val log = new CloudLiveAccessLog("10.12.22.234 - - [10/May/2017:16:35:58 +0800] GET /tvucc/js/util/datetimepicker/css/bootstrap-datetimepicker.css?version=version3.0.84%20build84 HTTP/1.1 304 - 0.001")
    val str = "/tvucc/taskInfoFromMemory.action?"
    println(log.filterRandomParameter(str))
  }
}