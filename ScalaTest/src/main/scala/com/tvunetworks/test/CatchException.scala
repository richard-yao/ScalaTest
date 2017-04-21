package com.tvunetworks.test

import java.io.FileReader
import java.io.FileNotFoundException
import org.apache.log4j.Logger
import org.apache.log4j.xml.DOMConfigurator
import java.io.IOException
import java.io.InputStreamReader
import scala.io.Source

/**
 * @author RichardYao
 * @date 2017?4?21?
 */
object CatchException {
  
  val logger: Logger = Logger.getLogger("CatchException")
  
  def main(args: Array[String]) {
    try {
      if(args.length == 0) {
        throw new Exception("Input parameter empty")
      } else {
        DOMConfigurator.configure(args(0));
        if(args.length == 3) {
          val file = new FileReader(args(1))
          var buffer = new Array[Char](1024)
          val readBuffer = (buffer: Array[Char]) => file.read(buffer)
          while(readBuffer(buffer) != -1) {
            for(temp <- buffer; if temp.hashCode() != 0) {
              print(temp)
            }
          }
          logger.info("-------Print file content------")
          val printChar = (char: Char) => print(char)
          Source.fromFile(args(1)).foreach(printChar)
          logger.info("-------Print file content------")
          val lines = Source.fromFile(args(2))("UTF-8")
          for (line <-lines) {
            print(line)
          }
        } else {
          throw new Exception("Input parameters' number is not match!")
        }
      }
    } catch {
      case ex: FileNotFoundException => {
        logger.error(ex)
      }
      case ex: IOException => {
        logger.error(ex)
      }
      case ex: Exception => {
        logger.error(ex)
      }
    } finally {
      println("程序结束")
    }
  }
}