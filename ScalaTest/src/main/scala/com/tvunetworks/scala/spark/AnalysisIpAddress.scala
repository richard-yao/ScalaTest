package com.tvunetworks.scala.spark

import org.apache.spark.rdd.RDD
import com.tvunetworks.scala.model.ServerLog

/**
 * @author RichardYao
 * @date 2017?4?26?
 */
class AnalysisIpAddress[T] extends AnalysisBase[T] {
  
  override def run(streamData: RDD[T]): Unit = {
    val ipData = streamData.asInstanceOf[RDD[ServerLog]]
    val filterIpData = ipData.map(record => (record.ip, 1)).reduceByKey(_ + _).sortBy(record => {record._2})
    filterIpData.foreach(line => println("map reduce result:" + line))
  }
}