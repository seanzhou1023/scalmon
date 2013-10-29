package htwg.scalmon.model

import java.security.MessageDigest
import scala.math.max

object AnimalType extends Enumeration {
  type AnimalType = Value
  val Earth, Water, Air, Fire = Value
}

class Animal(val name: String) {
  val attributeValues: Array[Int] = MessageDigest.getInstance("MD5")
    .digest(name.toUpperCase.getBytes)
    .map("%02x".format(_))
    .map(x => Integer.parseInt(x, 16))

  def initHealthPoints = max(attributeValues(0) + attributeValues(1), attributeValues(2) + attributeValues(3)) * 2
  def initSpeed = max(attributeValues(4), attributeValues(5))
}