package com.tvunetworks.scala.xml

import scala.beans.BeanProperty
import scala.collection.mutable.Map
import scala.xml.XML
import java.io.InputStream

/**
 * @author RichardYao
 * @date 2017?4?25?
 */
object XmlAnalysis extends Serializable {
  
  @BeanProperty val sparkConfigurationMap = Map[String, String]()
  @BeanProperty val hdfsConfigurationMap = Map[String, String]()
  @BeanProperty val kafkaConfigurationMap = Map[String, String]()
  @BeanProperty val memcacheConfigurationMap = Map[String, String]()
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
      val duration = (sparkNode \ "duration").text.toString.trim()
      sparkConfigurationMap += ("sparkMaster" -> sparkMaster)
      sparkConfigurationMap += ("sparkAppName" -> sparkAppName)
      sparkConfigurationMap += ("duration" -> duration)
      
      //-------------------hdfs configuration properties---------------------
      val hdfsNode = xmlFile \ "hdfsConfig"
      val hdfsAddress = (hdfsNode \ "hdfsAddress").text.toString.trim()
      hdfsConfigurationMap += ("hdfsAddress" -> hdfsAddress)
      
      //-------------------kafka-streaming configuration properties---------------------
      val kafkaNode = xmlFile \ "kafkaConfig"
      val groupId = (kafkaNode \ "groupId").text.toString.trim()
      val bootstrapServer = (kafkaNode \ "bootstrapServers").text.toString.trim()
      val zookeeperCluster = (kafkaNode \ "zookeeper").text.toString.trim()
      val topic = (kafkaNode \ "topic").text.toString.trim()
      kafkaConfigurationMap += ("group.id" -> groupId)
      kafkaConfigurationMap += ("bootstrap.servers" -> bootstrapServer)
      kafkaConfigurationMap += ("zookeeper" -> zookeeperCluster)
      kafkaConfigurationMap += ("topic" -> topic)
      
      //-------------------memcache configuration properties---------------------
      val memcacheNode = xmlFile \ "memcacheConfig"
      val memcacheHosts = (memcacheNode \ "hosts").text.toString.trim()
      val memcacheTimeout = (memcacheNode \ "timeout").text.toString.trim()
      memcacheConfigurationMap += ("hosts" -> memcacheHosts)
      memcacheConfigurationMap += ("timeout" -> memcacheTimeout)
      
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