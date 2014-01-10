package htwg.scalmon.model

import java.security.MessageDigest
import scala.math.{ min, max, floor, random }
import htwg.scalmon.utils.ImageLoader
import htwg.scalmon.controller.Ability

object AnimalType extends Enumeration {
  type AnimalType = Value
  val EarthAnimal, WaterAnimal, AirAnimal, FireAnimal = Value
}

class Animal(val name: String, val predictable: Boolean = false) {

  val attributeChoices: Seq[Int] = MessageDigest.getInstance("MD5")
    .digest(name.toUpperCase.getBytes)
    .map("%02x".format(_))
    .map(x => Integer.parseInt(x, 16))

  lazy val image = ImageLoader.get(name)

  val initHealthPoints =
    max(attributeChoices(0) + attributeChoices(1),
      attributeChoices(2) + attributeChoices(3)) * 2

  val initSpeed = max(attributeChoices(4), attributeChoices(5))

  val baseBlockValue = max(attributeChoices(6), attributeChoices(7)) / 2

  val baseAttackValue =
    (attributeChoices(8) + attributeChoices(9) + attributeChoices(10)) / 2

  val animalType = attributeChoices(11) match {
    case x if (x <= 64)  => AnimalType.EarthAnimal
    case x if (x <= 128) => AnimalType.WaterAnimal
    case x if (x <= 192) => AnimalType.AirAnimal
    case x if (x <= 256) => AnimalType.FireAnimal
    case _               => throw new Exception("no animal type defined")
  }

  val criticalChance: Double = max(
    attributeChoices(12), attributeChoices(13)) / 255.0 * 0.4

  /*
   * From: http://stackoverflow.com/questions/20018423/
   */
  def rollCriticalHit: Boolean = random <= criticalChance

  def variationBetween(value: Int): Tuple2[Int, Int] = (
    value - (value * (attributeChoices(14) / 255.0 * 0.3)).toInt,
    value + (value * (attributeChoices(15) / 255.0 * 0.6)).toInt)

  /*
   * From: http://stackoverflow.com/questions/4959975/
   */
  def valueBetween(from: Int, to: Int): Int =
    floor(random * (to - from + 1) + from).toInt

  private def makeAttack(add: Int): Int = {
    if (predictable)
      baseAttackValue + add
    else {
      var base = (baseAttackValue + add).toDouble
      if (rollCriticalHit)
        base = base * 1.5
      val (from, to) = variationBetween(base.toInt)
      valueBetween(from, to)
    }
  }

  private def makeBlockOn(attacker: Animal) = {
    import AnimalType._

    var base = baseBlockValue * ((attacker.animalType, this.animalType) match {
      case (EarthAnimal, WaterAnimal) => 0.7
      case (EarthAnimal, AirAnimal)   => 2.0
      case (EarthAnimal, FireAnimal)  => 0.5

      case (WaterAnimal, EarthAnimal) => 1.7
      case (WaterAnimal, AirAnimal)   => 1.8
      case (WaterAnimal, FireAnimal)  => 1.1

      case (AirAnimal, EarthAnimal)   => 2.0
      case (AirAnimal, WaterAnimal)   => 0.6
      case (AirAnimal, FireAnimal)    => 0.5

      case (FireAnimal, EarthAnimal)  => 1.6
      case (FireAnimal, WaterAnimal)  => 2.0
      case (FireAnimal, AirAnimal)    => 1.2

      case _                          => 1.0
    })

    if (predictable)
      base
    else {
      val (from, to) = variationBetween(base.toInt)
      valueBetween(from, to)
    }
  }

  def block(attacker: Animal, additionalDmg: Int = 0) = {
    val dmg = 100 * attacker.makeAttack(additionalDmg) / this.makeBlockOn(attacker)
    this.healthPoints -= dmg.toInt
    AttackInfo(attacker, this, dmg.toInt)
  }

  def attack(victim: Animal) = victim.block(this)

  def heal(on: Animal) = {
    val health = min(this.baseAttackValue, on.initHealthPoints - on.healthPoints)
    on.healthPoints += health
    HealInfo(this, on, health)
  }

  //def spreadHeal = 

  def sacrificeAttack(victim: Animal) = {
    val additionalDmg = this.baseAttackValue
    this.healthPoints -= additionalDmg / 2
    victim.block(this, additionalDmg)
  }

  //def taunt =  // spotten

  //def fade =  // verblassen

  //def sacrificeOtherAttack = 

  // def stackAttack = // mehrfach Angriff (3-5 mal), innerhalb des Angriffs erhoeht sich die Krit Chance temporaer

  def ability(ability: Ability): AbilityInfo = ability.skill match {
    case 1 => attack(ability.target)
    case 2 => heal(ability.target) // TODO: add more abilities
    case 3 => sacrificeAttack(ability.target)
    case x => throw new Exception("ability " + x + " not available")
  }

  private var pHealthPoints = initHealthPoints
  def healthPoints = pHealthPoints
  def healthPoints_=(points: Int): Unit = pHealthPoints = max(min(points, initHealthPoints), 0)

  def alive = healthPoints > 0

  def reset {
    healthPoints = initHealthPoints
  }

  override def toString = "Animal: " + name + " of type " + animalType

}
