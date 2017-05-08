package com.tvunetworks.scala.spark

import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import com.tvunetworks.scala.xml.XmlAnalysis
import com.tvunetworks.scala.model.ServerLog
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.SparkConf
import com.tvunetworks.scala.model.CloudLiveAccessLog

/**
 * @author RichardYao
 * @date 2017?4?25?
 */
object LogAnalysisApp {
  
  val hdfsAddress = "hdfs://"
  /**
   * @args ["config-file-path"]
   */
  def main(args: Array[String]): Unit = {
    require(args.size == 1, "Please input the config file's path in hdfs!")
    val configPath = args(0)
    if(isHdfsFileExist(configPath)) {
      readConfigFile(configPath)
      
      val sparkConfigMap = XmlAnalysis.getSparkConfigurationMap
      val hdfsConfigMap = XmlAnalysis.getHdfsConfigurationMap
      val logConfigMap = XmlAnalysis.getLogConfigurationMap
      
      val master = sparkConfigMap("sparkMaster")
      val appName = sparkConfigMap("sparkAppName")
      val duration = Integer.parseInt(sparkConfigMap("duration"))
      val sparkConf = new SparkConf().setMaster(master).setAppName(appName)
      val ssc = new StreamingContext(sparkConf, Seconds(duration)); //每10s统计一次数据
      val sc = ssc.sparkContext //一个JVM上只能有一个SparkContext是激活的
      //val sc = new SparkContext(sparkConf)
      val resultRdd = logConfigMap.map(log => {
        val logTopic = log._1 //对日志数据进行处理的项目名
        val propMap = log._2 //进行处理的相关参数
        val appClass = propMap("appClass")
        val sourceFile = propMap("sourceFilePath")
        val items = propMap("items").split(",")
        val filterData = sc.textFile(sourceFile).filter(line => line.split(" ").length >= 10).map(line => new CloudLiveAccessLog(line))
        val clz = Class.forName(appClass)
        val constructors = clz.getConstructors()
        val constructor = constructors(0).newInstance()
        val outputRecord = constructor.asInstanceOf[AnalysisBase[CloudLiveAccessLog]].run(filterData)
      })
      
      //对于updateStateByKey这种会改变状态的transformation操作需要使用checkpoint
      /*ssc.checkpoint("/user/hadoop/checkpoint") //checkpoint文件保存地址
      val kafkaStreaming = new AnalysisKafkaStream()
      kafkaStreaming.useKafkaStreaming(ssc)
      ssc.start()
      ssc.awaitTermination()*/
    }
  }
  
  /**
   * Load config xml file from hdfs to cache
   */
  def readConfigFile(filePath: String): Unit = {
    val hdfsAddr = substrHdfsAddress(filePath, hdfsAddress.length())
    val configuration = new Configuration
    configuration.set("fs.defaultFS", hdfsAddr);
    val fs = FileSystem.get(configuration)
    val path = new Path(filePath)
    XmlAnalysis.analysis(fs.open(path))
  }
  
  /**
   * Check file exist of not in hdfs
   */
  def isHdfsFileExist(filePath: String): Boolean = filePath.startsWith(hdfsAddress) && {
    val hdfsAddr = substrHdfsAddress(filePath, hdfsAddress.length())
    val configuration = new Configuration
    configuration.set("fs.defaultFS", hdfsAddr);
    val fs = FileSystem.get(configuration)
    val path = new Path(filePath)
    fs.exists(path)
  }
  
  /**
   * substring hdfs master address
   */
  def substrHdfsAddress(path: String, startAddr: Int): String = {
    val portIdx = path.substring(startAddr).indexOf(":")
    val addrSplit = path.substring(portIdx, path.length()).indexOf("/")
    if(addrSplit == -1)
      path
    else
      path.substring(0, portIdx + addrSplit)
  }
}