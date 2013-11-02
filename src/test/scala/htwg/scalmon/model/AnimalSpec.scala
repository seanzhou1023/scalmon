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

  it should "have a valid image" in {
    val f = fixture
    f.pika.image should not equal null
    f.pika.image.getHeight(null) should be > 0
    f.mauzi.image should not equal null
    f.mauzi.image.getHeight(null) should be > 0
  }

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
  
  it should "be able to attack and the other should block (minimize) the attack" in {
    val f = fixture
    f.pika sly f.mauzi
    f.mauzi.healthPoints should be < f.mauzi.initHealthPoints
  }
}