package htwg.scalmon.model

import org.scalatest._
import scala.language.reflectiveCalls

class PlayerSpec extends FlatSpec with Matchers {

  def fixture = new {
    val human = new Player("Human", Array(new Animal("A1")))
    val ai = new Player("AI", Array(new Animal("A2"), new Animal("A3"), new Animal("A4")))
  }

  "A Player" should "have a name" in {
    fixture.human.name should be("Human")
    fixture.ai.name should be("AI")
  }

  it should "have a valid image" in {
    fixture.human.image should not equal null
    fixture.human.image.getHeight(null) should be > 0
    fixture.ai.image should not equal null
    fixture.ai.image.getHeight(null) should be > 0
  }

  it should "have some animals" in {
    fixture.human.animals should not equal null
    fixture.human.animals.length should be(1)
    fixture.ai.animals should not equal null
    fixture.ai.animals.length should be(3)
  }
}