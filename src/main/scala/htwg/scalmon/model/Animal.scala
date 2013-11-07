package htwg.scalmon.model

import java.security.MessageDigest
import scala.math.max
import htwg.scalmon.utils.ImageLoader

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

  def is(comp: AnimalType.Value) = this.animalType == comp

  // TODO: calculate AnimalType from name
  private def makeAttack = this.baseAttackValue // TODO: + Kritisch, Boni, Random Schwankung

  private def makeBlockOn(attacker: Animal) = {
    import AnimalType._
    (attacker, this) match { // this is blocker. is he strong (>1) or weak (<1) against attacker elemental type
      case (a, t) if ((a is EarthAnimal) && (t is WaterAnimal)) => (this.baseBlockValue * 0.6).toInt // speak: Water blocks weak Earth
      case (a, t) if ((a is EarthAnimal) && (t is AirAnimal)) => this.baseBlockValue * 5
      case (a, t) if ((a is EarthAnimal) && (t is FireAnimal)) => (this.baseBlockValue * 0.3).toInt

      case (a, t) if ((a is WaterAnimal) && (t is EarthAnimal)) => this.baseBlockValue * 2
      case (a, t) if ((a is WaterAnimal) && (t is AirAnimal)) => this.baseBlockValue * 3
      case (a, t) if ((a is WaterAnimal) && (t is FireAnimal)) => (this.baseBlockValue * 1.5).toInt

      case (a, t) if ((a is AirAnimal) && (t is EarthAnimal)) => this.baseBlockValue * 5
      case (a, t) if ((a is AirAnimal) && (t is WaterAnimal)) => (this.baseBlockValue * 0.5).toInt
      case (a, t) if ((a is AirAnimal) && (t is FireAnimal)) => (this.baseBlockValue * 0.3).toInt

      case (a, t) if ((a is FireAnimal) && (t is EarthAnimal)) => this.baseBlockValue * 4
      case (a, t) if ((a is FireAnimal) && (t is WaterAnimal)) => this.baseBlockValue * 6
      case (a, t) if ((a is FireAnimal) && (t is AirAnimal)) => this.baseBlockValue * 2

      case _ => this.baseBlockValue
    }
  }

  def block(attacker: Animal): Animal = {
    this.healthPoints -= attacker.makeAttack - this.makeBlockOn(attacker) // TODO: + Kritisch, Boni, Random Schwankung
    this
  }

  def attack(victim: Animal) = victim.block(this)

  def heal(on: Animal) = {
    val heal = on.healthPoints + this.baseAttackValue
    if (heal > on.initHealthPoints)
      on.healthPoints = on.initHealthPoints
    else
      on.healthPoints = heal
  }

  def ability(idx: Int, on: Animal) = idx match {
    case 1 => attack(on)
    case 2 => heal(on)  // TODO: add more abilities
    case x => throw new Exception("ability " + x + " not available")
  }
  
  val enabledAbilities = 1 :: 2 :: Nil  // TODO: abhaenig vom namen machen + TESTS

  var healthPoints = initHealthPoints

  def alive = healthPoints > 0

  def reset {
    healthPoints = initHealthPoints
  }

  override def toString = "Animal: " + name + " of type " + animalType

}
