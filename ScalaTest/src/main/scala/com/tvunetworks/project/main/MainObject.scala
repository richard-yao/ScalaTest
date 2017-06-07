package com.tvunetworks.project.main

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import com.tvunetworks.scala.xml.XmlAnalysis
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.tvunetworks.scala.model.CloudLiveAccessLog
import org.apache.spark.rdd.RDD

object MainObject {
  
  val hdfsAddress = "hdfs://"
  
  def main(args: Array[String]): Unit = {
    require(args != null && args.size == 1, "Please add config file's path in hdfs!")
    val configPath = args(0);
    if(isHdfsFileExist(configPath)) {
      readConfigFile(configPath)
      realMainMethod()
    }
  }
  
  /**
   * Real task execute part
   */
  def realMainMethod() {
    val sparkConfigMap = XmlAnalysis.getSparkConfigurationMap
    
    val masterPara = sparkConfigMap("sparkMaster")
    val appName = sparkConfigMap("sparkAppName")
    val sparkConf = new SparkConf().setAppName(appName).setMaster(masterPara)
    val sparkContext = new SparkContext(sparkConf)
    
    val logConfigMap = XmlAnalysis.getLogConfigurationMap
    val logFileConfig = logConfigMap("IpAddressAnalysis")
    val logFilePath = logFileConfig("sourceFilePath")
    val logRdd = sparkContext.textFile(logFilePath).filter(line => line.split(" ").length >= 10).map(line => new CloudLiveAccessLog(line))
    dealWithLineData(logRdd.filter(record => !record.requestResult.equals("200"))).map(record => "Failed request list: "+ record).saveAsTextFile("/user/hadoop/dataresult/failedRequest")
  }
  
  //按请求地址计算请求数量
  def dealWithLineData(requestData: RDD[CloudLiveAccessLog]): RDD[(String, Int)] = {
    requestData.map(record => (record.requestAddress, 1)).reduceByKey(_+_).sortBy(_._2, false)
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