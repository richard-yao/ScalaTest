package com.tvunetworks.scala.spark

import org.apache.spark.SparkConf
import java.util.Properties
import org.apache.spark.sql.SQLContext
import org.apache.spark.SparkContext

/**
 * @author RichardYao
 * @date 2017?5?23?
 */
object UseSpackSql {
  
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("TestUseSparkSql").setMaster("spark://hadoop-master:7077")
    val sc = new SparkContext(sparkConf)
    val sparkSql = new SQLContext(sc)
    val prop = new Properties
    prop.put("user", "root")
    prop.put("password", "root")
    
    val dbUrl = "jdbc:mysql://10.12.23.146:3306/bigdata"
    val lowerBound = 2L
    val upperBound = 10000000L
    val tableName = "hotel_copy"
    val df = sparkSql.read.jdbc(dbUrl, tableName, "'index'", lowerBound, upperBound, 3, prop).cache()
    df.show()
  }
}