package com.tvunetworks.scala.spark

import org.apache.spark.rdd.RDD
import com.tvunetworks.scala.accesslog.pushlive.CloudLiveAccessLog

/**
 * @author RichardYao
 * @date 2017?4?26?
 */
class AnalysisIpAddress[T] extends AnalysisBase[T] {
  
  override def run(streamData: RDD[T]): Unit = {
    val ipData = streamData.asInstanceOf[RDD[CloudLiveAccessLog]]
    val filterIpData = ipData.map(record => (record.remoteIp, 1)).reduceByKey(_ + _).sortBy(record => {record._2})
    filterIpData.foreach(line => println("Count the same ip request result:" + line))
  }
}