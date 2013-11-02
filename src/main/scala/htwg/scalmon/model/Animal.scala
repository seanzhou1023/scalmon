package htwg.scalmon.model

import java.security.MessageDigest
import scala.math.max
import htwg.scalmon.utils.ImageLoader

trait AnimalType
trait EarthAnimal extends AnimalType
trait WaterAnimal extends AnimalType
trait AirAnimal extends AnimalType
trait FireAnimal extends AnimalType

class Animal(val name: String) extends AnimalType {
  val attributeValues: Array[Int] = MessageDigest.getInstance("MD5")
    .digest(name.toUpperCase.getBytes)
    .map("%02x".format(_))
    .map(x => Integer.parseInt(x, 16))

  lazy val image = ImageLoader.load(name)

  def initHealthPoints = max(attributeValues(0) + attributeValues(1), attributeValues(2) + attributeValues(3)) * 2
  def initSpeed = max(attributeValues(4), attributeValues(5))
  def baseBlockValue = max(attributeValues(6), attributeValues(7)) / 4
  def baseAttackValue = (attributeValues(8) + attributeValues(9) + attributeValues(10)) / 2
  // TODO: calculate AnimalType from name
  private def makeAttack = this.baseAttackValue  // TODO: + Kritisch, Boni, Random Schwankung
  private def makeBlockOn(attacker: AnimalType) = (attacker, this) match {  // TODO: Matrix muss noch ausgebaut werden
    case (a: EarthAnimal, t: EarthAnimal)  => this.baseBlockValue * 1
    case (a: FireAnimal, t: EarthAnimal) => this.baseBlockValue * 3
    case _ => this.baseBlockValue
  }
  def block(attacker: Animal): Animal = {
    this.healthPoints -= attacker.makeAttack - this.makeBlockOn(attacker) // TODO: + Kritisch, Boni, Random Schwankung
    this
  }
  def attack(victim: Animal) = victim.block(this)

  var healthPoints = initHealthPoints
  def alive = healthPoints > 0

  def reset {
    healthPoints = initHealthPoints
  }
}