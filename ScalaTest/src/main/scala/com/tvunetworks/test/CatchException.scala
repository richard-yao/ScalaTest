package com.tvunetworks.test

import java.io.FileReader
import java.io.FileNotFoundException
import org.apache.log4j.Logger
import java.io.IOException
import java.io.InputStreamReader

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
        val file = new FileReader(args(0))
        var buffer = new Array[Char](1024)
        val readBuffer = (buffer: Array[Char]) => file.read(buffer)
        while(readBuffer(buffer) != -1) {
          for(temp <- buffer;if temp != "") {
            print(temp)
          }
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
      println("Program over")
    }
  }
}