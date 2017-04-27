package com.tvunetworks.scala.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

import org.apache.log4j.Logger


/**
 * @author RichardYao
 * @date 2017?4?26?
 */
object HttpUtil {
  
  val logger: Logger = Logger.getLogger("HttpUtil")
  
  private def getNowTime = (Calendar.getInstance.getTimeInMillis).toString()
  
  def sendPostWithForm(urlAddress: String, parameters: String): String = {
    sendPost(urlAddress, parameters, "application/x-www-form-urlencoded")
  }
  
  def sendPostWithJson(urlAddress: String, parameters: String): String = {
    sendPost(urlAddress, parameters, "application/json")
  }
  
  def sendPost(urlAddress: String, parameters: String, requestType: String): String = {
    var connect: HttpURLConnection = null
    var out: OutputStreamWriter = null
		try {
			var url: URL = new URL(urlAddress)
			connect = url.openConnection().asInstanceOf[HttpURLConnection]
			connect.setDoOutput(true)
			connect.setRequestMethod("POST")
			connect.setUseCaches(false)
			connect.setRequestProperty("Content-Type", requestType)
			connect.setConnectTimeout(3000);
			connect.setReadTimeout(3000);
			connect.connect();
			out = new OutputStreamWriter(connect.getOutputStream())
			out.write(new String(parameters.getBytes("UTF-8")))
			out.flush();
			out.close();
			var code: Int = connect.getResponseCode()
			if (code == 200) {
				var resultData: String = "";
				var reader: BufferedReader = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"))
				var inputLine: String = null
				while({inputLine = reader.readLine();inputLine} != null) {
				  resultData += inputLine
				}
				reader.close()
				return resultData
			}
		} catch {
			case ex: Exception => logger.error(ex)
		} finally {
			if (out != null) {
				try {
					out.close()
					connect.disconnect()
				} catch {
				  case e: Exception => logger.error(e)
				}
			}
		}
		return null
  }
  
  def main(args: Array[String]): Unit = {
    var url = "http://pushlive.tvunetworks.cn/pushlive/admin/startLiveMonitorInCC.action"
    var parameters = "monitorDevices=[%22097cc7eb25dec7b2%22]"
    println(sendPostWithForm(url, parameters))
  }
}