package com.tvunetworks.scala.accesslog.pushlive

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.StreamingContext

/**
 * @author RichardYao
 * @date 2017?5?5?
 */
object CreateKafkaStream {
  
  val kafkaParams = Map[String, Object] (
    "bootstrap.servers" -> "10.12.22.100:9092,10.12.22.78:9092,10.12.23.4:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "test_topic_grop",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )
  
  val topics = Array("test_topic")
  
}