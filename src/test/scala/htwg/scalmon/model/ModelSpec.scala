package htwg.scalmon.model

import org.scalatest._
import scala.language.reflectiveCalls
import htwg.scalmon.Listener

class ModelSpec extends FlatSpec with Matchers with GivenWhenThen {

  def fixture = new {
    val m1 = new Model(1)
    val m5 = new Model(5)
  }

  "A Model" should "have a game size for scaling" in {
    fixture.m1.gameSize should be(1)
    fixture.m5.gameSize should be(5)
  }

  it should "have players" in {
    Given("a new model")
    Then("no players should be set")
    fixture.m1.playerA should be(null)
    fixture.m1.playerB should be(null)
    fixture.m5.playerA should be(null)
    fixture.m5.playerB should be(null)

    When("player A and B are set")
    val m = fixture.m1
    val a1 = new Animal("Animal1")
    val a2 = new Animal("Animal2")
    m.playerA = new Player("A", Array(a1))
    m.playerB = new Player("B", Array(a2))
    m.playerA should not equal null
    m.playerB should not equal null

    Then("resetAnimals should reset all animals of both players")
    a1.healthPoints = 0
    a2.healthPoints = 0
    m.resetAnimals
    a1.healthPoints should be(a1.initHealthPoints)
    a2.healthPoints should be(a2.initHealthPoints)
  }

  it should "have a state" in {
    Given("a newly created model")
    val m = fixture.m1

    Then("its state should be Init")
    m.state should be(Init())

    When("the state is changed")
    m.state = RunRound(13, List())

    Then("the model should return the new state")
    m.state should be(RunRound(13, List()))
  }

  it should "notify its attached listeners" in {
    Given("new listeners which are not attached to the model")
    val ls = new {
      val l1, l2 = new Listener {
        var count = 0
        var lastInfo: Option[AbilityInfo] = None
        def update(info: Option[AbilityInfo]) = { count += 1; lastInfo = info }
      }

      def count = (l1.count, l2.count)
    }

    val m = fixture.m1
    m.notifyListeners()

    Then("they should not be notified")
    ls.count should be(0, 0)

    When("a listener is added to the model")
    m.addListener(ls.l1)
    ls.count should be(0, 0)

    Then("it should be notified correctly")
    m.notifyListeners()
    ls.count should be(1, 0)

    When("multiple listeners are added")
    m.addListener(ls.l2)

    Then("all of them should be notified")
    m.notifyListeners()
    ls.count should be(2, 1)
    ls.l1.lastInfo should be(None)
    ls.l2.lastInfo should be(None)

    When("an ability info is included into the notification")
    m.notifyListeners(Option(AttackInfo(null, null, 100)))

    Then("the listener should have this info received")
    ls.count should be(3, 2)
    ls.l1.lastInfo shouldBe a[Some[AttackInfo]]
    ls.l2.lastInfo shouldBe a[Some[AttackInfo]]

    When("a listener is removed")
    m.removeListener(ls.l1)

    Then("it should not be notified anymore")
    m.notifyListeners()
    ls.count should be(3, 3)
  }
}