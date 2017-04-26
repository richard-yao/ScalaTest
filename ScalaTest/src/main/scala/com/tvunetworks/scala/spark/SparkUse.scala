package com.tvunetworks.scala.spark

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.spark.rdd.RDD
import java.io.File
import java.util.HashMap
import java.util.ArrayList
import java.sql.{Connection, DriverManager, Statement}

/**
 * @author RichardYao
 * @date 2017?4?22?
 */
object SparkUse {
  
  val hdfsAddress = "hdfs://"
  
  def main(args: Array[String]) {
    if(args.length > 0) {
      val master = args(0)
      val appNmae = args(1)
      val logFilePath = args(2)
      val sparkConf = new SparkConf().setMaster(master).setAppName(appNmae)
      val sparkContxt = new SparkContext(sparkConf)
      if(isHdfsFileExist(logFilePath)) {
        //val accumulator = sparkContxt.longAccumulator("Count-word") //Accumulator用于在多worker间同步数据似乎会导致整个系统处理变慢
        val rdd = sparkContxt.textFile(logFilePath).cache()
        val lineLengths = rdd.map(s => s.split(" ").length) //传名调用匿名函数
        //lineLengths.foreach(num => accumulator.add(num))
        val totalLength = lineLengths.reduce((a, b) => a+b)
        println("-----------Total word length: " + totalLength + "--------")
        //println("-----------Count total word length with accumulator and the word length is :"+accumulator.value+"---------")
        println("-----------Start statistic ip count--------")
        statisticIpCount(rdd)
        println("-----------End statistic ip count--------")
      } else {
        error("The file is not exist")
      }
    } else {
      error("Not enough arguments")
    }
  }
  
  /**
   * Statistic the log file first key's number
   */
  def statisticIpCount(rdd: RDD[String]): Unit = {
    val rightLogLine = 23
    var filterPairs = rdd.filter(line => line.split(" ").length >= rightLogLine)
    var ipMap = filterPairs.map(line => line.split(" ")(0))
    var mapResult = ipMap.map(word => (word, 1))
    var reduceRdd = mapResult.reduceByKey(_ + _).sortByKey(true)
    reduceRdd.foreach(pair => {
      println("ip-address: " + pair._1 + ", appear times: " + pair._2);
    })
    reduceRdd.foreachPartition(addPairsList)
  }
  
  def addPairsList(pairs: Iterator[(String, Int)]): Unit = {
    val dbUrl = "jdbc:mysql://10.12.22.78:3306/statistic_data"
    val dbDriver = "com.mysql.jdbc.Driver"
    val dbUser = "root"
    val dbPasswd = "tvu1p2ack3"
    val connection: Connection = DriverManager.getConnection(dbUrl, dbUser, dbPasswd)
    val statement = connection.createStatement()
    val sql = "insert into ip_statistic values('{0}', {1})"
    connection.setAutoCommit(false)
    while(pairs.hasNext) {
      var tempPair = pairs.next()
      var tempSql = sql.replace("{0}", tempPair._1).replace("{1}", String.valueOf(tempPair._2))
      statement.addBatch(tempSql)
    }
    statement.executeBatch()
    connection.commit()
    statement.close()
    connection.close()
  }
  
  /**
   * This code used to split long sentence to alone word with " "
   */
  def statisticWordCount(rdd: RDD[String]): Unit = {
    val rightLogLine = 23
    var filterPairs = rdd.filter(line => line.split(" ").length >= rightLogLine)
    var flatMap = filterPairs.flatMap(line => line.split(" ").map(key => (key, 1)))
    var reduceWord = flatMap.reduceByKey(_ + _)
    reduceWord.sortByKey(true).foreach(pair => println("key-word: " + pair._1 + ", appear times: " + pair._2))
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
    var addrSplit = path.substring(portIdx, path.length()).indexOf("/")
    if(addrSplit == -1)
      path
    else
      path.substring(0, portIdx + addrSplit)
  }
}