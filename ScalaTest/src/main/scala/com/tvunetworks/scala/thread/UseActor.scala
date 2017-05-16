package com.tvunetworks.scala.thread

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

/**
 * @author RichardYao
 * @date 2017?5?12?
 */
class UseActor extends Actor {
  
  def receive = {
    case _: String =>
    for(i <- 1 to 5) {
      println(s"Run times: $i")
      Thread.sleep(1000)
    }
  }
}

object SimpleActor {
  def main(str: Array[String]): Unit = {
    val system = ActorSystem("MyActorSystem")
    val myActor = system.actorOf(Props(new UseActor), "myActor")
    myActor ! "start"
  }
}