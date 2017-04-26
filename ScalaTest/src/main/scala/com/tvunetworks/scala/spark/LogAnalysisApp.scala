package com.tvunetworks.scala.spark

import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import com.tvunetworks.scala.xml.XmlAnalysis
import com.tvunetworks.scala.model.ServerLog
import org.apache.spark.SparkContext
import com.tvunetworks.scala.model.ServerLog

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
      val sc = new SparkContext(master, appName)
      val resultRdd = logConfigMap.map(log => {
        val logTopic = log._1 //对日志数据进行处理的项目名
        val propMap = log._2 //进行处理的相关参数
        val appClass = propMap("appClass")
        val sourceFile = propMap("sourceFilePath")
        val items = propMap("items").split(",")
        //标准数据格式应该是23个
        val filterData = sc.textFile(sourceFile).filter(line => line.split(" ").length >= 23).map(line => new ServerLog(line.split(" ")))
        val clz = Class.forName(appClass)
        val constructors = clz.getConstructors()
        val constructor = constructors(0).newInstance()
        val outputRecord = constructor.asInstanceOf[AnalysisBase[ServerLog]].run(filterData)
      })
    }
  }
  
  /**
   * Load config xml file from hdfs to cache
   */
  def readConfigFile(filePath: String): Unit = {
    var hdfsAddr = substrHdfsAddress(filePath, hdfsAddress.length())
    var configuration = new Configuration
    configuration.set("fs.defaultFS", hdfsAddr);
    var fs = FileSystem.get(configuration)
    var path = new Path(filePath)
    XmlAnalysis.analysis(fs.open(path))
  }
  
  /**
   * Check file exist of not in hdfs
   */
  def isHdfsFileExist(filePath: String): Boolean = filePath.startsWith(hdfsAddress) && {
    var hdfsAddr = substrHdfsAddress(filePath, hdfsAddress.length())
    var configuration = new Configuration
    configuration.set("fs.defaultFS", hdfsAddr);
    var fs = FileSystem.get(configuration)
    var path = new Path(filePath)
    fs.exists(path)
  }
  
  /**
   * substring hdfs master address
   */
  def substrHdfsAddress(path: String, startAddr: Int): String = {
    var portIdx = path.substring(startAddr).indexOf(":")
    var addrSplit = path.substring(portIdx, path.length()).indexOf("/")
    if(addrSplit == -1)
      path
    else
      path.substring(0, portIdx + addrSplit)
  }
}