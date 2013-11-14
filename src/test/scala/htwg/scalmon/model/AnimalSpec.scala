package htwg.scalmon.model

import collection.mutable.Stack
import org.scalatest._
import scala.language.reflectiveCalls

class AnimalSpec extends FlatSpec with Matchers {

  def fixture = new {
    val pika = new Animal("Pika")
    val mauzi = new Animal("Mauzi")
  }

  "An Animal" should "have a name" in {
    fixture.pika.name should be("Pika")
    fixture.mauzi.name should be("Mauzi")
  }

  //  it should "have a valid image" in {
  //    val f = fixture
  //    f.pika.image should not equal null
  //    f.pika.image.getHeight(null) should be > 0
  //    f.mauzi.image should not equal null
  //    f.mauzi.image.getHeight(null) should be > 0
  //  }

  it should "have maximum health points (calculated from the name)" in {
    fixture.pika.initHealthPoints should be(848)
    fixture.mauzi.initHealthPoints should be(866)
  }

  it should "have initial speed (calculated from the name)" in {
    fixture.pika.initSpeed should be(236)
    fixture.mauzi.initSpeed should be(185)
  }

  it should "have a base block value" in {
    fixture.pika.baseBlockValue should be(43)
    fixture.mauzi.baseBlockValue should be(53)
  }

  it should "have a base attack value" in {
    fixture.pika.baseAttackValue should be(166)
    fixture.mauzi.baseAttackValue should be(216)
  }
  
  it should "have a critical chance value" in {
    fixture.pika.criticalChance shouldEqual 0.16 +- 0.01
    fixture.mauzi.criticalChance shouldEqual 0.39 +- 0.01
  }
  
  it should "offer variations to values" in {
    fixture.pika.variationBetween(100)._1 should be (74)
    fixture.pika.variationBetween(100)._2 should be (104)
    fixture.mauzi.variationBetween(100)._1 should be (89)
    fixture.mauzi.variationBetween(100)._2 should be (131)
  }

  it should "be able to attack and the other should block (minimize the attack)" in {
    val f = fixture
    f.pika attack f.mauzi
    f.mauzi.healthPoints should be < (866)
  }

  it should "have an elemental type (calculated from the name)" in {
    val f = fixture
    f.pika.animalType should be(AnimalType.WaterAnimal)
    f.mauzi.animalType should be(AnimalType.AirAnimal)
  }

  it should "have a element type, where a air animal blocks good water animal damage" in {
    val f = fixture
    f.pika attack f.mauzi
    f.mauzi.healthPoints should be(692)
  }
  
  it should "be able to wound itself, to enhance it's attack" in {
    val f = fixture
    f.pika.healthPoints should be(848)
    f.pika sacrificeAttack f.mauzi
    f.mauzi.healthPoints should be(518)
    f.pika.healthPoints should be(765)
  }
}