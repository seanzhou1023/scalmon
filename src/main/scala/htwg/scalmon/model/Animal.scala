package htwg.scalmon.model

import java.security.MessageDigest
import scala.math.{ min, max }
import htwg.scalmon.utils.ImageLoader
import htwg.scalmon.controller.Ability

object AnimalType extends Enumeration {
  type AnimalType = Value
  val EarthAnimal, WaterAnimal, AirAnimal, FireAnimal = Value
}

class Animal(val name: String) {

  val attributeChoices: Seq[Int] = MessageDigest.getInstance("MD5")
    .digest(name.toUpperCase.getBytes)
    .map("%02x".format(_))
    .map(x => Integer.parseInt(x, 16))

  lazy val image = ImageLoader.get(name)

  val initHealthPoints =
    max(attributeChoices(0) + attributeChoices(1),
      attributeChoices(2) + attributeChoices(3)) * 2

  val initSpeed = max(attributeChoices(4), attributeChoices(5))

  val baseBlockValue = max(attributeChoices(6), attributeChoices(7)) / 4

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
  
  def variationBetween(value: Int): Tuple2[Int, Int] = (
      value - (value * (attributeChoices(14) / 255.0 * 0.3)).toInt,
      value + (value * (attributeChoices(15) / 255.0 * 0.6)).toInt
  )

  private def makeAttack(add: Int) = this.baseAttackValue + add // TODO: + Kritisch, Boni, Random Schwankung

  private def makeBlockOn(attacker: Animal) = {
    import AnimalType._

    baseBlockValue * ((attacker.animalType, this.animalType) match {
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
  }

  def block(attacker: Animal, additionalDmg: Int = 0): Animal = {
    val dmg = 100 * attacker.makeAttack(additionalDmg) / this.makeBlockOn(attacker)
    this.healthPoints -= dmg.toInt // TODO: + Kritisch, Boni, Random Schwankung
    this
  }

  def attack(victim: Animal) = victim.block(this)

  def heal(on: Animal) =
    on.healthPoints = min(on.healthPoints + baseAttackValue, initHealthPoints)
    
  //def spreadHeal = 

  def sacrificeAttack(victim: Animal) = {
    val additionalDmg = this.baseAttackValue
    this.healthPoints -= additionalDmg / 2
    victim.block(this, additionalDmg)
  }
  
  //def taunt =  // spotten
    
  //def fade =  // verblassen
    
  //def sacrificeOtherAttack = 
    
  def ability(ability: Ability) = ability.skill match {
    case 1 => attack(ability.target)
    case 2 => heal(ability.target) // TODO: add more abilities
    case 3 => sacrificeAttack(ability.target)
    case x => throw new Exception("ability " + x + " not available")
  }

  val enabledAbilities = 1 :: 2 :: Nil // TODO: abhaenig vom namen machen + TESTS

  var healthPoints = initHealthPoints

  def alive = healthPoints > 0

  def reset {
    healthPoints = initHealthPoints
  }

  override def toString = "Animal: " + name + " of type " + animalType

}
