package com.tvunetworks.scala.model

/**
 * @author RichardYao
 * @date 2017?4?25?
 */
class ServerLog(array: Array[String]) {
  
  var ip = array(0)
  var logtime = array(3).substring(1)
  var isvalid = {
    if(array(8).equals("") || Integer.parseInt(array(8)) != 400)
      false
    true
  }
  
  override def toString(): String = {
    ip + ":::"+logtime+":::"+isvalid
  }
}