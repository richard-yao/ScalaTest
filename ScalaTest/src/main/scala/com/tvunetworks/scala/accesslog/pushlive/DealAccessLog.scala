package com.tvunetworks.scala.accesslog.pushlive

import org.apache.spark.rdd.RDD
import com.tvunetworks.scala.spark.AnalysisBase
import com.tvunetworks.scala.model.CloudLiveAccessLog
import com.tvunetworks.scala.model.CloudLiveAccessLog
import com.tvunetworks.scala.util.StringUtil
import com.tvunetworks.scala.xml.XmlAnalysis

/**
 * @author RichardYao
 * @date 2017?5?3?
 */
class DealAccessLog[T] extends AnalysisBase[T] {
  
  override def run(streamData: RDD[T]): Unit = {
    val hdfsConfig = XmlAnalysis.getHdfsConfigurationMap
    val resultPath = hdfsConfig("hdfsAddress")+"user/hadoop/dataresult/"
    val linesData = streamData.asInstanceOf[RDD[CloudLiveAccessLog]]
    val failedRequst = dealWithLineData(linesData.filter(record => !record.requestResult.equals("200"))).map(record => "Failed request list: "+ record).saveAsTextFile(resultPath+"faildRequest")
    val successXhrRequest = dealWithLineData(linesData.filter(filterXhrMethod)).map(record => "Success ajax request list: "+ record).saveAsTextFile(resultPath+"successAjax")
    val successRequest = linesData.filter(_.requestResult.equals("200"))
    val postRequest = dealWithLineData(successRequest.filter(_.requestMethod.equals("POST"))).map(record => "POST request: "+record).saveAsTextFile(resultPath+"postRequest")
    val getRequest = dealWithLineData(successRequest.filter(_.requestMethod.equals("GET"))).map(record => "GET request: "+record).saveAsTextFile(resultPath+"getRequest")
    //val pvData = dealPVData(successRequest).map(record => {record._1._1 + "\t" + record._1._2 + "\t" + record._2}).foreach(println)
    
    val pvAverageTime = dealPVAndAverageTimeData(successRequest).map(record => record._1._1 + "\t" + record._1._2 + "\t" + record._2._2 + "次\t" + countDivide(record._2._1, record._2._2)+"ms").saveAsTextFile(resultPath+"pvAverageTime")
    val dataUsage = dealEveryIntervalDatausage(successRequest).map(record => record._1._1 + "\t" + record._1._2 + "\t" + record._2 + "KB").saveAsTextFile(resultPath+"dataUsage")
    
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
  
  //统计PV的数据同时，计算每个访问当天的平均相应速度
  def dealPVAndAverageTimeData(requestData: RDD[CloudLiveAccessLog]): RDD[((String, String), (Long, Int))] = {
    val averageTime = requestData.map(record => ((record.formatTime, record.requestAddress), (((record.processTime).toDouble*1000).toLong, 1))).reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2))
    averageTime.sortBy(record => PVSort(record._1._1, record._2._2))
  }
  
  //统计每个时间段的数据使用量
  def dealEveryIntervalDatausage(requestData: RDD[CloudLiveAccessLog]): RDD[((String, String), Double)] = {
    val averageTime = requestData.map(record => ((record.formatTime, record.requestAddress), record.requestDatausage)).reduceByKey(_+_)
    val needSortRdd = averageTime.map(rdd => (rdd._1, StringUtil.divideAndGet2PointValue(rdd._2, 1000L)))
    needSortRdd.sortBy(record => ResultSort(record._1._1, record._2))
  }
  
  def countDivide(sum: Long, number: Int): Int = {
    (sum / number).toInt
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

case class ResultSort(date: String, number: Double) extends Ordered[ResultSort] with Serializable {
  
  override def compare(that: ResultSort): Int = {
    val compare  = this.date.compareTo(that.date)
    if(compare == 0) 
      this.number.compare(that.number)
    else
      compare
  }
}