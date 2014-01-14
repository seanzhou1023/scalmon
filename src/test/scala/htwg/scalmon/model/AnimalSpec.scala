package htwg.scalmon.model

import collection.mutable.Stack
import org.scalatest._
import scala.language.reflectiveCalls

class AnimalSpec extends FlatSpec with Matchers {

  def fixturePredictable = new {
    val pika = new Animal("Pika", predictable = true)
    val mauzi = new Animal("Mauzi", predictable = true)
  }

  def fixtureNotPredictable = new {
    val pika = new Animal("Pika")
    val mauzi = new Animal("Mauzi")
  }

  "An Animal" should "have a name" in {
    fixturePredictable.pika.name should be("Pika")
    fixturePredictable.mauzi.name should be("Mauzi")
  }

  //  it should "have a valid image" in {
  //    val f = fixture
  //    f.pika.image should not equal null
  //    f.pika.image.getHeight(null) should be > 0
  //    f.mauzi.image should not equal null
  //    f.mauzi.image.getHeight(null) should be > 0
  //  }

  it should "have maximum health points (calculated from the name)" in {
    fixturePredictable.pika.initHealthPoints should be(848)
    fixturePredictable.mauzi.initHealthPoints should be(866)
  }

  it should "have initial speed (calculated from the name)" in {
    fixturePredictable.pika.initSpeed should be(236)
    fixturePredictable.mauzi.initSpeed should be(185)
  }

  it should "have a base block value" in {
    fixturePredictable.pika.baseBlockValue should be(86)
    fixturePredictable.mauzi.baseBlockValue should be(106)
  }

  it should "have a base attack value" in {
    fixturePredictable.pika.baseAttackValue should be(166)
    fixturePredictable.mauzi.baseAttackValue should be(216)
  }

  it should "have a base heal value" in {
    fixturePredictable.pika.baseHealValue should be(153)
    fixturePredictable.mauzi.baseHealValue should be(133)
  }

  it should "have a critical chance value" in {
    fixturePredictable.pika.criticalChance shouldEqual 0.07 +- 0.01
    fixturePredictable.mauzi.criticalChance shouldEqual 0.02 +- 0.01
  }

  it should "offer variations to values" in {
    fixturePredictable.pika.variationBetween(100)._1 should be(74)
    fixturePredictable.pika.variationBetween(100)._2 should be(104)
    fixturePredictable.mauzi.variationBetween(100)._1 should be(89)
    fixturePredictable.mauzi.variationBetween(100)._2 should be(131)
  }

  it should "generate a value between a range" in {
    val (from, to) = fixturePredictable.pika.variationBetween(100)
    fixturePredictable.pika.valueBetween(from, to) should (
      be >= (from) and
      be <= (to))
  }

  it should "roll if it's a critical hit or not" in {
    fixturePredictable.pika.rollCriticalHit should (be(true) or be(false))
  }

  it should "be able to attack and the other should block (minimize the attack)" in {
    val f = fixturePredictable
    f.pika attack f.mauzi
    f.mauzi.healthPoints should be < (866)

    var iter = 0
    var critDmg = 0
    while (critDmg == 0) {
      val f2 = fixtureNotPredictable
      f2.pika attack f2.mauzi
      val dmg = f2.mauzi.initHealthPoints - f2.mauzi.healthPoints
      if (dmg > 0.75 * f2.pika.baseAttackValue)
        critDmg = dmg
      f2.mauzi.healthPoints should be < (866)
      iter += 1
      if (iter > 1000 && critDmg == 0)
        critDmg should be > (f2.pika.baseAttackValue)
    }

  }

  it should "have an elemental type (calculated from the name)" in {
    val f = fixturePredictable
    f.pika.animalType should be(AnimalType.WaterAnimal)
    f.mauzi.animalType should be(AnimalType.AirAnimal)
  }

  it should "have a element type, where a air animal blocks good water animal damage" in {
    val f = fixturePredictable
    f.pika attack f.mauzi
    f.mauzi.healthPoints should be(779)
  }

  it should "be able to wound itself, to enhance it's attack" in {
    val f = fixturePredictable
    f.pika.healthPoints should be(848)
    f.pika sacrificeAttack f.mauzi
    f.mauzi.healthPoints should be(692)
    f.pika.healthPoints should be(765)
  }

  it should "be able to heal other animals" in {
    val f = fixturePredictable
    f.pika.initHealthPoints should be(848)
    f.pika.healthPoints -= 100
    f.pika.healthPoints should be(748)
    f.mauzi.heal(f.pika)
    f.pika.healthPoints should be(848)
  }

}