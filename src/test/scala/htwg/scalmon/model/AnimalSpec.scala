package htwg.scalmon.model

import collection.mutable.Stack
import org.scalatest._

class AnimalSpec extends FlatSpec with Matchers {
  
  def fixture = new {
    val pika = new Animal("Pika")
    val mauzi = new Animal("Mauzi")
  }

  "A Animal" should "have a name" in {
    fixture.pika.name should be ("Pika")
    fixture.mauzi.name should be ("Mauzi")
  }

  it should "have maximum health points (calculated from the name)" in {
    fixture.pika.initHealthPoints should be (848)
    fixture.mauzi.initHealthPoints should be (866)
  }

  it should "have initial speed (calculated from the name)" in {
    fixture.pika.initSpeed should be (236)
    fixture.mauzi.initSpeed should be (185)
  }
}