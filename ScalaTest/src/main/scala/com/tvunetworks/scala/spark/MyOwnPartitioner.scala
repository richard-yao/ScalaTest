package com.tvunetworks.scala.spark

import org.apache.spark.Partitioner

/**
 * @author RichardYao
 * @date 2017?5?23?
 * 对于Key-value类型数据是可以通过partitionBy()来使用自定义Partitioner
 */
class MyOwnPartitioner(numParts: Int) extends Partitioner {
  
  override def numPartitions: Int = numParts
  
  override def getPartition(key: Any): Int = {
    val domain = new java.net.URL(key.toString()).getHost
    val code = (domain.hashCode() % numPartitions)
    if(code < 0) 
      code + numPartitions
    else 
      code
  }
  
  override def equals(other: Any): Boolean = other match {
    case partitioner: MyOwnPartitioner => partitioner.numPartitions == numPartitions
    case _ => false
  }
  
  override def hashCode: Int = numPartitions
}