package htwg.scalmon

import htwg.scalmon.controller.Controller
import htwg.scalmon.model._
import htwg.scalmon.view._
import htwg.scalmon.UserInterface._

object Main extends App {
  def generateView(config: Config, model: Model, controller: Controller): View = {
    config.userInterface match {
      case Textual => new TUI(model, controller)
      case Graphical => null
      case Web => null
    }
  }

  override def main(args: Array[String]) = {
    val config = new Parser() parse (args, Config()) getOrElse { exit(); }

    println("scalmon is ready with " + config)

    val model = new Model(config.size)
    val controller = new Controller(model)
    val view: View = generateView(config, model, controller)
    model.notifyListeners

    println(new Animal("Pika").initHealthPoints)
    println(new Animal("Mauzi").initHealthPoints)

    val animalsA = Array(new Animal("Pika"))
    val animalsB = Array(new Animal("Mauzi"))
    model.playerA = new Player("Human", animalsA)
    model.playerB = new Player("KI", animalsB)
  }
}