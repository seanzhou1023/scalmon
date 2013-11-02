package htwg.scalmon.model

import java.security.MessageDigest
import scala.math.max
import htwg.scalmon.utils.ImageLoader

object AnimalType extends Enumeration {
  type AnimalType = Value
  val Earth, Water, Air, Fire = Value
}

trait Earth {
  def block
}

class Animal(val name: String) {
  val attributeValues: Array[Int] = MessageDigest.getInstance("MD5")
    .digest(name.toUpperCase.getBytes)
    .map("%02x".format(_))
    .map(x => Integer.parseInt(x, 16))

  lazy val image = ImageLoader.load(name)

  def initHealthPoints = max(attributeValues(0) + attributeValues(1), attributeValues(2) + attributeValues(3)) * 2
  def initSpeed = max(attributeValues(4), attributeValues(5))
  def baseBlockValue = max(attributeValues(6), attributeValues(7)) / 4
  def baseAttackValue = (attributeValues(8) + attributeValues(9) + attributeValues(10)) / 2
  
  private def attack = this.baseAttackValue  // + Kritisch, Boni, Random Schwankung
  private def block(attacker: Animal) = this.healthPoints = attacker.attack - this.baseBlockValue // + Kritisch, Boni, Random Schwankung
  def sly(victim: Animal) = victim.block(this)

  var healthPoints = initHealthPoints
  def alive = healthPoints > 0

  def reset {
    healthPoints = initHealthPoints
  }
}