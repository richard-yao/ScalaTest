package com.tvunetworks.scala.thread

import akka.actor.Actor
import akka.actor.ActorRef
import scala.collection.mutable.ListBuffer
import akka.actor.Props
import akka.routing.FromConfig

/**
 * @author RichardYao
 * @date 2017?5?10?
 */
sealed class Helper(count: Int, replyTo: ActorRef) extends Actor {
  val perfectNumbers = new ListBuffer[Int]
  var nrOfResult = 0
  
  def receive = {
    case Result(num: Int, isPerfect: Boolean) =>
      nrOfResult += 1
      if(isPerfect)
        perfectNumbers += num
      if(nrOfResult == count)
        replyTo ! PerfectNumbers(perfectNumbers.toList)
  }
}

class Master extends Actor {
  val worker = context.actorOf(Props[Worker].withRouter(FromConfig()), "workerRouter")
  
  def receive = {
    case StartFind(start: Int, end: Int, replyTo: ActorRef) if(start > 1 && end >= start) =>
      val count = end - start + 1
      val helper = context.actorOf(Props(new Helper(count, replyTo)))
      (start to end).foreach(num => worker ! Work(num, helper))
  }
}