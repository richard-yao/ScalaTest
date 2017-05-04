package com.tvunetworks.scala.spark

import org.apache.spark.rdd.RDD
import com.tvunetworks.scala.xml._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.storage.StorageLevel
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.Seconds

/**
 * @author RichardYao
 * @date 2017?4?26?
 */
class AnalysisKafkaStream extends Serializable {
  
  def useKafkaStreaming(ssc: StreamingContext): Unit = {
    val duration = Integer.parseInt(XmlAnalysis.sparkConfigurationMap("duration"))
    val kafkaConfig = XmlAnalysis.kafkaConfigurationMap
    
    val prefixPath = "/user/hadoop/output/kafka"
    val suffixPath = "kafk-streaming"
    val consumerThreads = 3
    val topics = Set(kafkaConfig("topic"))
    //Map("metadata.broker.list" -> kafkaConfig("bootstrap.servers"))
    val kafkaParam = Map("bootstrap.servers" -> kafkaConfig("bootstrap.servers"),
                         "group.id" -> kafkaConfig("group.id"),
                         "zookeeper" -> kafkaConfig("zookeeper"))
    
    //启动多个DStream
    val kafkaDStreams = (1 to consumerThreads).map(idx => {
      val stream: InputDStream[(String, String)] = createStream(ssc, kafkaParam, topics)
      val batchData = stream.map(_._2) //取出value
        .flatMap(_.split(" ")) //将字符串按空格划分
        .map(r => (r, 1)) //将每个单词映射成一个pair
        .updateStateByKey[Int](updateFunc) //用当前batch的数据区更新已有数据, 对于每个key都会调用func函数处理先前的状态和所有新的状态
      batchData.print() //每个duration统计数据
      batchData.countByWindow(Seconds(duration * 6), Seconds(duration * 6)).print() //时间窗口数据统计
    })
    //val kafkaDStreams = KafkaUtils.createStream(ssc, kafkaConfig("zookeeper"), kafkaConfig("group.id"), topics, StorageLevel.MEMORY_AND_DISK_SER)
    //kafkaDStreams.saveAsTextFiles(prefixPath, suffixPath)
  }
  
  val updateFunc = (currentValues: Seq[Int], preValue: Option[Int]) => {
    val curr = currentValues.sum
    val pre = preValue.getOrElse(0)
    Some(curr + pre)
  }
  
  /**
   * @param ssc spark streaming上下文
   * @param kafkaParam kafka相关配置
   * @param topics 需要消费的topic集合
   */
  def createStream(ssc: StreamingContext, kafkaParam: Map[String, String], topics: Set[String]) = {
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParam, topics)
  }
}