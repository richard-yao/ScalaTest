package com.tvunetworks.scala.feature

import scala.util.Random

/**
 * @author RichardYao
 * @date 2017?5?22?
 * 水塘抽样
 */
object PondSampling {
  
  def reservoirSampling(nowTotalArray: Array[Int], resultNumber: Int): Array[Int] = {
    require(nowTotalArray.length >= resultNumber)
    val result = new Array[Int](resultNumber)
    for(i <- 0 until resultNumber) {
      result(i) = nowTotalArray(i)
    }
    for(i <- resultNumber until nowTotalArray.length) {
      val j = Random.nextInt(i)
      if(j < resultNumber) {
        result(j) = nowTotalArray(i)
      }
    }
    result
  }
  
  def main(args: Array[String]) {
    val totalArray = Array(1,2,3,4,5,6,7,8,9,22)
    reservoirSampling(totalArray, 2).foreach(println)
  }
}