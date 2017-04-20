package com.tvunetworks.test

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream.toPairDStreamFunctions

object SparkStreaming extends App {
  
  val conf = new SparkConf().setAppName("UseScalaDealSocketStreaming");
  val ssc = new StreamingContext(conf, Seconds(1));
  val lines = ssc.socketTextStream("hadoop-master", 9999);
  val words = lines.flatMap(_.split(" "));
  val pairs = words.map(word => (word, 1));
  val counts = pairs.reduceByKey(_+_);
  counts.print();
  ssc.start();
  ssc.awaitTermination();
}