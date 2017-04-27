package com.tvunetworks.test

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream.toPairDStreamFunctions
import com.tvunetworks.scala.xml.XmlAnalysis
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.storage.StorageLevel

object SparkStreaming extends App {
  
  val conf = new SparkConf().setAppName("UseScalaDealSocketStreaming").setMaster("spark://10.12.23.4:7077");
  val ssc = new StreamingContext(conf, Seconds(1));
  val lines = ssc.socketTextStream("hadoop-master", 9999);
  val words = lines.flatMap(_.split(" "));
  val pairs = words.map(word => (word, 1));
  val counts = pairs.reduceByKey(_+_);
  counts.print();
  ssc.start();
  ssc.awaitTermination();
  
  def useKafkaStreaming(ssc: StreamingContext): Unit = {
    val kafkaParams = XmlAnalysis.kafkaConfigurationMap
    
    val consumerThreads = 3
    val topics = Map(kafkaParams("topic") -> consumerThreads)
    val kafkaDStreams = KafkaUtils.createStream(ssc, kafkaParams("zookeeper"), kafkaParams("group.id"), topics, StorageLevel.MEMORY_ONLY)
  }
}