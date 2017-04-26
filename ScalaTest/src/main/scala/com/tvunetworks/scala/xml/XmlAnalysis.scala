package com.tvunetworks.scala.xml

import scala.beans.BeanProperty
import scala.collection.mutable.Map
import scala.xml.XML
import java.io.InputStream

/**
 * @author RichardYao
 * @date 2017?4?25?
 */
object XmlAnalysis {
  
  @BeanProperty val sparkConfigurationMap = Map[String, String]()
  @BeanProperty val hdfsConfigurationMap = Map[String, String]()
  @BeanProperty val logConfigurationMap = Map[String, Map[String, String]]()
  
  //properties file location:
  //hdfs://hadoop-master:9000/user/hadoop/input/config.xml
  def analysis(inputStream: Any) = {
    if(inputStream.isInstanceOf[InputStream]) {
      val xmlFile = XML.load(inputStream.asInstanceOf[InputStream])
      //-------------------spark configuration properties---------------------
      val sparkNode = xmlFile \ "sparkConfig"
      val sparkMaster = (sparkNode \ "master").text.toString.trim()
      val sparkAppName = (sparkNode \ "appName").text.toString.trim()
      sparkConfigurationMap += ("sparkMaster" -> sparkMaster)
      sparkConfigurationMap += ("sparkAppName" -> sparkAppName)
      
      //-------------------hdfs configuration properties---------------------
      val hdfsNode = xmlFile \ "hdfsConfig"
      val hdfsAddress = (hdfsNode \ "hdfsAddress").text.toString.trim()
      hdfsConfigurationMap += ("hdfsAddress" -> hdfsAddress)
      
      //-------------------log configuration properties---------------------
      val mixLogs = xmlFile \ "logProperties" \ "log"
      mixLogs.map(p => {
        val mixLogMap = Map[String, String]()
        val statisticTopic = (p \ "statisticTopic").text.toString.trim()
        val appClass = (p \ "appClass").text.toString.trim()
        val sourceFilePath = (p \ "sourceFilePath").text.toString.trim()
        val items = (p \ "items").text.toString.trim()
        mixLogMap += ("statisticTopic" -> statisticTopic)
        mixLogMap += ("appClass" -> appClass)
        mixLogMap += ("sourceFilePath" -> sourceFilePath)
        mixLogMap += ("items" -> items)
        logConfigurationMap += (statisticTopic -> mixLogMap)
      })
    } else {
      println("Input wrong parameter")
    }
  }
}