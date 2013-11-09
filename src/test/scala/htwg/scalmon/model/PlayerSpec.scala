package htwg.scalmon.model

import org.scalatest._
import scala.language.reflectiveCalls

class PlayerSpec extends FlatSpec with Matchers with GivenWhenThen {

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

  it should "be can beaten" in {
    Given("some healthy animals")
    val ps = fixture

    Then("the player should not be beaten")
    ps.human.beaten should be(false)
    ps.human.animalsAlive should be(ps.human.animals)
    ps.ai.beaten should be(false)
    ps.ai.animalsAlive should be(ps.ai.animals)

    When("some of the animals are knocked out")
    ps.ai.animals(1).healthPoints = 0

    Then("the player should not be beaten too")
    ps.ai.beaten should be(false)
    ps.ai.animalsAlive should not be (ps.ai.animals)
    ps.ai.animalsAlive.length should be(2)

    When("all of the animals are knocked out")
    (ps.human.animals ++ ps.ai.animals).foreach(_.healthPoints = 0)

    Then("the player should be beaten")
    ps.human.beaten should be(true)
    ps.human.animalsAlive.length should be(0)
    ps.ai.beaten should be(true)
    ps.ai.animalsAlive.length should be(0)
  }
}