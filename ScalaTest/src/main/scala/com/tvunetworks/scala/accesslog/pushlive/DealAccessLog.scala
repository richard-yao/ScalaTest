package com.tvunetworks.scala.accesslog.pushlive

import org.apache.spark.rdd.RDD
import com.tvunetworks.scala.spark.AnalysisBase

/**
 * @author RichardYao
 * @date 2017?5?3?
 */
class DealAccessLog[T] extends AnalysisBase[T] {
  
  override def run(streamData: RDD[T]): Unit = {
    val linesData = streamData.asInstanceOf[RDD[CloudLiveAccessLog]]
    val failedRequst = linesData.filter(record => !record.requestResult.equals("200")).foreach(record => println("Failed request list: "+ record))
    val successXhrRequest = dealWithLineData(linesData.filter(filterXhrMethod)).foreach(record => println("Success ajax request list: "+ record))
    val successRequest = linesData.filter(_.requestResult.equals("200"))
    val postRequest = dealWithLineData(successRequest.filter(_.requestMethod.equals("POST"))).foreach(record => println("POST request: "+record))
    val getRequest = dealWithLineData(successRequest.filter(_.requestMethod.equals("GET"))).foreach(record => println("GET request: "+record))
    val pvData = dealPVData(successRequest).map(record => {record._1._1 + "\t" + record._1._2 + "\t" + record._2}).foreach(println)
  }
  
  def filterXhrMethod(record: CloudLiveAccessLog): Boolean = {
    record.requestResult.equals("200") && (record.requestAddress.indexOf(".action") > -1 || record.requestAddress.indexOf(".spr") > -1)
  }
  
  //按请求地址计算请求数量
  def dealWithLineData(requestData: RDD[CloudLiveAccessLog]): RDD[(String, Int)] = {
    requestData.map(record => (record.requestAddress, 1)).reduceByKey(_+_).sortBy(_._2, false)
  }
  
  //统计PV
  def dealPVData(requestData: RDD[CloudLiveAccessLog]): RDD[((String, String), Int)] = {
    val reduceResult = requestData.map(record => ((record.formatTime, record.requestAddress), 1)).reduceByKey(_+_)
    reduceResult.sortBy(record => PVSort(record._1._1, record._2))
  }
}

case class PVSort(date: String, count: Int) extends Ordered[PVSort] with Serializable {
  
  override def compare(that: PVSort): Int = {
    val compare  = this.date.compareTo(that.date)
    if(compare == 0)
      this.count.compare(that.count)
    else
      compare
  }
}