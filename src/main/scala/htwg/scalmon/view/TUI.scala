package htwg.scalmon.view

import htwg.scalmon.BuildInfo
import htwg.scalmon.model._
import htwg.scalmon.controller._
import htwg.scalmon.utils.Log

class TUI(_model: Model, _controller: Controller) extends View(_model, _controller) {

  val inputThread = new Thread(new Runnable {
    override def run() {
      try {
        readInput
      } catch {
        case ex: Exception => Log(ex)
      }
    }
  })

  def readInput {
    while (model.state != Exited) {
      val input = readLine

      input match {
        case "q" => controller.handle(Quit)
        case _ => input.split(',').toList.map(_.trim) match {
          case playerName :: animalNames if (animalNames.size == model.gameSize) => controller.handle(SetPlayer(playerName, animalNames))
          case _ =>
        }
      }
    }
  }

  def show {
    inputThread.start

    println("\nWelcome to " + BuildInfo.name.toUpperCase() + "!")
    update(None)
  }

  def update(info: Option[AbilityInfo]) = model.state match {
    case Init(b) => printInit(b)
    case _       => println("TUI update: " + info)
  }

  def printInit(playerB: Boolean) {
    val p1 = if (playerB) "opponent's" else "your"
    val p2 = if (playerB) "his" else "your"
    val plural = if (model.gameSize > 1) "s" else ""

    println(s"Please enter ${p1} name and the name of ${p2} ${model.gameSize} animal${plural} (comma separated):")
  }
}