package com.tvunetworks.scala.thread

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props
import akka.actor.Actor

/**
 * @author RichardYao
 * @date 2017?5?10?
 */
object PerfectNumber {
  
  def main(args: Array[String]) {
    val system = ActorSystem("MasterApp", ConfigFactory.load.getConfig("multiThread"))
    system.actorOf(Props(new Actor() {
      val master = context.system.actorOf(Props[Master], "master")
      master ! StartFind(2, 100, self)
      def receive = {
        case PerfectNumbers(list: List[Int]) =>
          println("\nFound Perfect Numbers:" + list.mkString(","))  
          system.shutdown()
      }
    }))
  }
}