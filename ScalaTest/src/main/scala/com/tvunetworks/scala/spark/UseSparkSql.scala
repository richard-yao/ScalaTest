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
    val sparkConf = new SparkConf().setAppName("TestUseSparkSql").setMaster("spark://ip:7077")
    val sc = new SparkContext(sparkConf)
    val sparkSql = new SQLContext(sc)
    val prop = new Properties
    prop.put("user", "xxxx")
    prop.put("password", "xxxx")
    
    val dbUrl = "jdbc:mysql://ip:3306/bigdata"
    val lowerBound = 2L
    val upperBound = 10000L
    val tableName = "hotel_copy"
    val df = sparkSql.read.jdbc(dbUrl, tableName, "'index'", lowerBound, upperBound, 3, prop)
    df.show()
  }
}