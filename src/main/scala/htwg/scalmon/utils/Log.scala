package htwg.scalmon.utils

import java.io._

object Log {
  val writer = new PrintWriter(new FileWriter("./log.txt", true), true);

  def apply(ex: Exception) {
    try {
      ex.printStackTrace(writer)
      writer.println
    } catch { case _: Throwable => }
  }

  def apply(str: String) {
    try {
      writer.println(str)
      writer.println
    } catch { case _: Throwable => }
  }
}