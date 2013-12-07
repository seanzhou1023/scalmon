package htwg.scalmon.utils

import java.io._
import java.util.Date

object Log {
  val writer = new PrintWriter(new FileWriter("./log.txt", true), true);

  def apply(ex: Exception) {
    try {
      writer.println(new Date)
      ex.printStackTrace(writer)
      writer.println
    } catch { case _: Throwable => }
  }

  def apply(str: String) {
    try {
      writer.println(new Date)
      writer.println(str)
      writer.println
    } catch { case _: Throwable => }
  }
}