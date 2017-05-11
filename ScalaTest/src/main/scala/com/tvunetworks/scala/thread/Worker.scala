package com.tvunetworks.scala.thread

import akka.actor.Actor
import akka.actor.ActorRef

/**
 * @author RichardYao
 * @date 2017?5?10?
 */
class Worker extends Actor {
  private def sumOffFactors(number: Int) = {
    (1 /: (2 until number)) { (sum, i) => if(number % i == 0) sum+i else sum }
  }
  
  def isPerfect(num: Int): Boolean = {
    num == sumOffFactors(num)
  }
  
  def receive = {
    case Work(num: Int, replyTo: ActorRef) =>
      replyTo ! Result(num, isPerfect(num)) //replyTo ! xxx 接收者向发送者发送消息
      print("[" + num + "]")
  }
}