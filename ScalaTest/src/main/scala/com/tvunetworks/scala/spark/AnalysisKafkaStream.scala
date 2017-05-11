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
import java.util.HashMap

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
    val stream = createStream(ssc, kafkaParam, topics)
    
    dealWithStreamingData(stream)
    
    stream.foreachRDD(rdd => {
     val offsetRange = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
     
     //dealWithRddData(rdd, ssc)
     
     offsetRange.foreach(offset => println("Partition: "+ offset.partition + ", offset: " + offset.fromOffset))
     stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRange) //提交offset更新到zookeeper
    })
  }
  
  /**
   * 针对RDD单独的过滤没法获取到整个的结果，缺少updateStateByKey
   */
  def dealWithRddData(rdd: RDD[ConsumerRecord[String, String]], ssc: StreamingContext) {
    val dataKey = "spark_split_word_result_another"
    val remainData = convertMemcacheDataToRdd(dataKey, ssc)
    val batchData = rdd.map(record => (record.key(), record.value())).map(_._2)
      .flatMap(_.split(" ")) //将字符串按空格划分
      .map(r => (r, 1)) //将每个单词映射成一个pair
      .join(remainData).map(record => (record._1, record._2._1 + record._2._2))
      .reduceByKey(_ + _)
    saveRddDataToMemcache(batchData, dataKey)
  }
  
  /**
   * Deal kafka produce messages
   */
  def dealWithStreamingData(stream: InputDStream[ConsumerRecord[String, String]]): Unit = {
    val batchData = stream.map(record => (record.key(), record.value())).map(_._2)
      .flatMap(_.split(" ")) //将字符串按空格划分
      .map(r => (r, 1)) //将每个单词映射成一个pair
      .updateStateByKey[Int](updateFunc) //用当前batch的数据区更新已有数据以保证整体数据一致性, 对于每个key都会调用func函数处理先前的状态和所有新的状态
      
      //每个duration统计数据
      batchData.foreachRDD(rdd => {
        //rdd.collectAsMap().foreach(println)
        saveRddDataToMemcache(rdd, "spark_split_word_result")
      }) 
      //batchData.countByWindow(Seconds(duration * 6), Seconds(duration * 6)).print() //时间窗口数据统计
  }
  
  val updateFunc = (currentValues: Seq[Int], preValue: Option[Int]) => {
    val curr = currentValues.sum
    val pre = preValue.getOrElse(0)
    Some(curr + pre)
  }
  
  /**
   * Store spark deal result to memcache
   */
  def saveRddDataToMemcache(parameter: RDD[(String, Int)], key: String) {
    val memcacheConfig = XmlAnalysis.memcacheConfigurationMap
    val memcacheClient = UserMemcachedClientImpl.getInstance(memcacheConfig("hosts"), memcacheConfig("timeout").toLong)
    memcacheClient.set(key, scalaMapToJavaMap(parameter.collectAsMap()), 0)
  }
  
  def convertMemcacheDataToRdd(key: String, ssc: StreamingContext): RDD[(String, Int)] = {
    val memcacheConfig = XmlAnalysis.memcacheConfigurationMap
    val memcacheClient = UserMemcachedClientImpl.getInstance(memcacheConfig("hosts"), memcacheConfig("timeout").toLong)
    val cacheData = memcacheClient.get(key).asInstanceOf[java.util.HashMap[String, Int]]
    if(cacheData != null) {
      val result = javaMapToScalaMap(cacheData)
      ssc.sparkContext.parallelize(result.toSeq)
    } else {
      ssc.sparkContext.parallelize(Seq())
    }
  }
  
  /**
   * Convert scala.collection.Map type data to java.util.Map data 
   */
  def scalaMapToJavaMap(map: scala.collection.Map[String, Int]): java.util.Map[String, Int] = {
    val result: java.util.Map[String, Int] = new java.util.HashMap[String, Int]
    map.foreach(record => result.put(record._1, record._2))
    result
  }
  
  import collection.JavaConversions._
  import collection.mutable._
  /**
   * Convert java.util.HashMap data to scala.collection.Map type data 
   */
  def javaMapToScalaMap(map: java.util.HashMap[String, Int]): scala.collection.mutable.Map[String, Int] = {
    val result: scala.collection.mutable.Map[String, Int] = map //直接隐式转换会导致结果没有被序列化
    result
  }
  
  /**
   * @param ssc spark streaming上下文
   * @param kafkaParam kafka相关配置
   * @param topics 需要消费的topic集合
   */
  def createStream(ssc: StreamingContext, kafkaParam: scala.collection.immutable.Map[String, Object], topics: scala.collection.immutable.Set[String]): InputDStream[ConsumerRecord[String, String]] = {
    //KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParam, topics)
    KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, Subscribe[String, String](topics, kafkaParam))
  }
}