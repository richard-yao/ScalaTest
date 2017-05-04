package com.tvunetworks.scala.spark

import com.tvunetworks.scala.model.ServerLog
import org.apache.spark.rdd.RDD

/**
 * @author RichardYao
 * @date 2017?4?25?
 * Task need extends Serializable
 */
trait AnalysisBase[T] extends Serializable {
  
  def run(streamData: RDD[T]): Unit
}