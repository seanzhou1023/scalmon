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
}