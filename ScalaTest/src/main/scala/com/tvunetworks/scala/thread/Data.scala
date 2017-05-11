package com.tvunetworks.scala.thread

import akka.actor.ActorRef
/**
 * @author RichardYao
 * @date 2017?5?10?
 */
sealed trait Message
case class StartFind(start: Int, end: Int, replyTo: ActorRef) extends Message
case class Work(num: Int, replyTo: ActorRef) extends Message
case class Result(num: Int, isPerfect: Boolean) extends Message
case class PerfectNumbers(list: List[Int]) extends Message
