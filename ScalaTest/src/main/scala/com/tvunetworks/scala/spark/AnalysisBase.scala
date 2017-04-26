package com.tvunetworks.scala.spark

import com.tvunetworks.scala.model.ServerLog
import org.apache.spark.rdd.RDD

/**
 * @author RichardYao
 * @date 2017?4?25?
 */
trait AnalysisBase[T] {
  
  def run(streamData: RDD[T]): Unit
}