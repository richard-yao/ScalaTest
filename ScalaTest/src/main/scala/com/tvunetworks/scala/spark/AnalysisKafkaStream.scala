package com.tvunetworks.scala.spark

import org.apache.spark.rdd.RDD
import com.tvunetworks.scala.xml._
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.Seconds
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.kafka.clients.consumer.ConsumerRecord
import com.tvunetworks.scala.util.UserMemcachedClientImpl

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
    val kafkaParam = Map[String, Object](
       "bootstrap.servers" -> kafkaConfig("bootstrap.servers"),
       "group.id" -> kafkaConfig("group.id"),
       "auto.offset.reset" -> "latest",
       "enable.auto.commit" -> (false: java.lang.Boolean),
       "key.deserializer" -> classOf[StringDeserializer],
       "value.deserializer" -> classOf[StringDeserializer]
    )
    //启动多个DStream
    //val kafkaDStreams = (1 to consumerThreads).map(idx => {
      //val stream: InputDStream[(String, String)] = createStream(ssc, kafkaParam, topics)
      val stream = createStream(ssc, kafkaParam, topics)
      val batchData = stream.map(record => (record.key(), record.value())).map(_._2)
        .flatMap(_.split(" ")) //将字符串按空格划分
        .map(r => (r, 1)) //将每个单词映射成一个pair
        .updateStateByKey[Int](updateFunc) //用当前batch的数据区更新已有数据, 对于每个key都会调用func函数处理先前的状态和所有新的状态
      batchData.foreachRDD(rdd => {
       //rdd.collectAsMap().foreach(println)
        saveRddDataToMemcache(rdd)
      }) //每个duration统计数据
      //batchData.countByWindow(Seconds(duration * 6), Seconds(duration * 6)).print() //时间窗口数据统计
      
      //提交offset更新到zookeeper
      stream.foreachRDD(rdd => {
       val offsetRange = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
       stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRange)
      })
    //})
  }
  
  def saveRddDataToMemcache(parameter: RDD[(String, Int)]) {
    val memcacheConfig = XmlAnalysis.memcacheConfigurationMap
    val memcacheClient = UserMemcachedClientImpl.getInstance(memcacheConfig("hosts"), memcacheConfig("timeout").toLong)
    val key = "spark_split_word_result"
    memcacheClient.set(key, parameter.collectAsMap(), 0)
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
  def createStream(ssc: StreamingContext, kafkaParam: Map[String, Object], topics: Set[String]): InputDStream[ConsumerRecord[String, String]] = {
    //KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParam, topics)
    KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, Subscribe[String, String](topics, kafkaParam))
  }
}