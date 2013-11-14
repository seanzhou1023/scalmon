package htwg.scalmon

import org.scalatest._
import scala.language.reflectiveCalls
import htwg.scalmon._
import htwg.scalmon.model._
import htwg.scalmon.controller._

class SystemTest extends FlatSpec with Matchers {

  def simulateRounds(controller: Controller, maxRounds: Int = -1) {
    while (controller.model.state.isInstanceOf[Round] &&
      (maxRounds == -1 || (controller.model.state.asInstanceOf[Round]).number < maxRounds)) {

      for (animal <- controller.model.playerA.animalsAlive)
        controller.handle(Ability(1, controller.model.playerB.animalsAlive(0))) // dummy AI: attack first animal of B

      controller.model.state shouldBe a [RunRound]

      while (controller.model.state.isInstanceOf[RunRound]) // run all abilities
        controller.handle(RunStep)
    }
  }

  def testGame(size: Int) = {
    // init
    val config = Config(UserInterface.Textual, size)
    val model = new Model(config.size)
    model.state shouldBe a [Init]

    val controller = new Controller(model) {
      var quit = false;
      override def cmdQuit = quit = true;
    }

    // init players
    val animalsA = (0 until config.size).toList.map("AnimalA" + _)
    val animalsB = (0 until config.size).toList.map("AnimalB" + _)
    controller.handle(SetPlayer("Human", animalsA))
    controller.handle(SetPlayer("KI", animalsB))
    model.state shouldBe a [Round]

    // start fight
    simulateRounds(controller)
    model.state shouldBe a [GameOver]

    // fight again
    controller.handle(Restart)
    model.state shouldBe a [Round]

    simulateRounds(controller)
    model.state shouldBe a [GameOver]

    // fight again and quit
    controller.handle(Restart)
    model.state shouldBe a [Round]

    simulateRounds(controller, 1)
    controller.handle(Quit)
    controller.quit
  }

  "A Systemtest" should "run the game with size 1" in {
    testGame(1) should be(true)
  }

  it should "run the game with size 2" in {
    testGame(2) should be(true)
  }

  it should "run the game with size 9" in {
    testGame(9) should be(true)
  }
}