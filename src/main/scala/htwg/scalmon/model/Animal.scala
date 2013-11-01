package htwg.scalmon.model

import java.security.MessageDigest
import scala.math.max
import htwg.scalmon.utils.ImageLoader

object AnimalType extends Enumeration {
  type AnimalType = Value
  val Earth, Water, Air, Fire = Value
}

class Animal(val name: String) {
  val attributeValues: Array[Int] = MessageDigest.getInstance("MD5")
    .digest(name.toUpperCase.getBytes)
    .map("%02x".format(_))
    .map(x => Integer.parseInt(x, 16))

  val image = ImageLoader.load(name)

  def initHealthPoints = max(attributeValues(0) + attributeValues(1), attributeValues(2) + attributeValues(3)) * 2
  def initSpeed = max(attributeValues(4), attributeValues(5))

  var healthPoints = initHealthPoints
  def alive = healthPoints > 0

  def reset {
    healthPoints = initHealthPoints
  }
}