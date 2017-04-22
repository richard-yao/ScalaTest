package com.tvunetworks.scala.spark

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path

/**
 * @author RichardYao
 * @date 2017?4?22?
 */
object SparkUse {
  
  val hdfsAddress = "hdfs://"
  val localAddress = "file://"
  
  def main(args: Array[String]) {
    if(args.length > 0) {
      val master = args(0)
      val appNmae = args(1)
      val logFilePath = args(2)
      val sparkConf = new SparkConf().setMaster(master).setAppName(appNmae)
      val sparkContxt = new SparkContext(sparkConf)
      if(isHdfsFileExist(logFilePath)) {
        val rdd = sparkContxt.textFile(logFilePath)
        val lineLengths = rdd.map(s => s.split(" ").length)
        val totalLength = lineLengths.reduce(_ + _)
        println("-----------Total word length: " + totalLength + "--------")
      } else {
        error("The file is not exist")
      }
    } else {
      error("Not enough arguments")
    }
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
    var portIdx = path.substring(startAddr).indexOf(":")
    var addrIdx = path.substring(portIdx, path.length()).indexOf("/") + portIdx
    path.substring(0, addrIdx)
  }
}