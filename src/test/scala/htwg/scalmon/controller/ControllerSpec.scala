package htwg.scalmon.controller

import org.scalatest._
import htwg.scalmon.model._;

class ControllerSpec extends FlatSpec with Matchers with GivenWhenThen {

  val playerA = new Player("P_A", List("A1", "A2").map(name => new Animal(name)).toArray)
  val playerB = new Player("P_B", List("B1", "B2").map(name => new Animal(name)).toArray)

  val attacks = List(
    (playerA.animals(0), Ability(2, playerA.animals(1))),
    (playerA.animals(1), Ability(2, playerA.animals(0))),
    (playerB.animals(0), Ability(2, playerB.animals(1))),
    (playerB.animals(1), Ability(2, playerB.animals(0))))

  val states = Map(
    "initA" -> Init(),
    "initB" -> Init(true),
    "round0" -> Round(1, playerA.animals(0), Map()),
    "runround0" -> RunRound(1, attacks),
    "aWon" -> GameOver(playerA),
    "bWon" -> GameOver(playerB),
    "bothBeaten" -> GameOver(null))

  def controller(state: State = null, setPlayers: Boolean = true) = {
    val m = new Model(2)

    if (state != null)
      m.state = state;

    if (setPlayers) {
      m.playerA = playerA;
      m.playerB = playerB;
    }

    new Controller(m)
  }

  def testOtherStates(cmd: Command, filterList: List[String]) {
    Given("another state of the model")
    val otherStates = states.filterNot(kvp => filterList.contains(kvp._1)).values

    Then("the command should be ignored")

    for (state <- otherStates) {
      val c = controller(state)
      val oldState = c.model.state
      c.handle(cmd)
      c.model.state should be(oldState)
    }
  }

  "A Controller" should "handle the 'SetPlayer' command" in {
    val cmd = SetPlayer("Player", List("Animal1", "Animal2"))

    Given("a newly created game with state 'Init'")
    val c1 = controller(states("initA"), false)

    Then("player A should be set")
    c1.handle(cmd)
    c1.model.playerA shouldBe a[Player]
    c1.model.playerB should be(null)
    c1.model.state should be(Init(true))

    When("player A is already set")

    Then("player B should be set by 'SetPlayer' command")
    c1.handle(cmd)
    c1.model.playerA shouldBe a[Player]
    c1.model.playerB shouldBe a[Player]
    c1.model.state shouldBe a[Round]
    c1.model.state.asInstanceOf[Round].attacks.size should be(2) // contains choosen abilities for animals of player B

    testOtherStates(cmd, List("initA", "initB"))
  }

  it should "handle the 'Ability' command" in {
    Given("the begin of a round with game size 2")
    val c = controller(states("round0"))
    val cmd = Ability(1, c.model.playerB.animals(0))

    Then("the 'Ability' command should choose the ability for the first animal of player A")
    c.handle(cmd)
    c.model.state shouldBe a[Round]
    val r1 = c.model.state.asInstanceOf[Round]
    r1.chooseAttackFor should be(c.model.playerA.animals(1))
    r1.attacks.size should be(1)
    r1.attacks(c.model.playerA.animals(0)) should be(cmd)

    When("the ability is choosen for the last animal of player A")
    c.handle(cmd)

    Then("the state should switch to 'RunRound', containing all attacks sorted by the speed of the animals")
    c.model.state shouldBe a[RunRound]
    val r2 = c.model.state.asInstanceOf[RunRound]
    r2.number should be(1)
    r2.attacks.size should be(2)
    r2.attacks(0)._2 should be(cmd)
    r2.attacks(1)._2 should be(cmd)
    r2.attacks(0)._1.initSpeed should be > (r2.attacks(1)._1.initSpeed)

    testOtherStates(cmd, List("round0"))
  }

  it should "handle the 'RunStep' command" in {
    Given("a game in state 'RunRound'")
    val c = controller(states("runround0"))
    c.model.state.asInstanceOf[RunRound].attacks.size should be(4)

    Then("the 'RunStep' command should execute one ability")
    c.handle(RunStep)
    c.model.state shouldBe a[RunRound]
    c.model.state.asInstanceOf[RunRound].attacks.size should be(3)

    When("all abilities are executed")

    for (i <- 1 to 3)
      c.handle(RunStep)

    Then("the state should switch to round #2")
    c.model.state shouldBe a[Round]
    c.model.state.asInstanceOf[Round].number should be(2)

    testOtherStates(RunStep, List("runround0"))
  }

  it should "handle the 'Restart' command" in {
    Given("a game in state 'GameOver'")
    val gameOverStates = states("aWon") :: states("bWon") :: states("bothBeaten") :: Nil

    Then("the 'Restart' command should start a new fight")
    for (state <- gameOverStates) {
      val c = controller(state)
      val allAnimals = c.model.playerA.animals ++ c.model.playerB.animals
      allAnimals.foreach(_.healthPoints = 0) // kill animals to test resetAnimals()

      c.handle(Restart)

      c.model.state shouldBe a[Round]
      c.model.state.asInstanceOf[Round].number should be(1) // first round
      c.model.state.asInstanceOf[Round].attacks.size should be(2) // contains choosen abilities for animals of player B
      allAnimals.forall(a => a.healthPoints == a.initHealthPoints) should be(true) // all animals should be healen
    }

    testOtherStates(Restart, List("aWon", "bWon", "bothBeaten"))
  }

  it should "handle the 'Quit' command" in {
    Given("any state of the game")
    Then("the 'Quit' command should switch to state 'Exited'")

    for (state <- states.values) {
      val c = controller(state)
      c.handle(Quit)
      c.model.state should be(Exited)
    }
  }

  it should "throw an IllegalArgumentException on unknown commands" in {
    val c = controller()
    val oldState = c.model.state
    an[IllegalArgumentException] should be thrownBy c.handle(new Command() {})
    c.model.state should be(oldState)
  }
}