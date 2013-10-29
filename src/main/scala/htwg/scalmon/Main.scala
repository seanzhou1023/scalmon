package htwg.scalmon

import htwg.scalmon.controller._
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
    val config = new Parser() parse (args, Config()) getOrElse sys.exit()

    println(BuildInfo.name + " is ready with " + config)

    val model = new Model(config.size)
    val controller = new Controller(model)
    val view: View = generateView(config, model, controller)
    model.notifyListeners

    controller.handle(SetPlayer("Human", List("Animal1", "Animal2")));
    controller.handle(SetPlayer("KI", List("Animal3", "Animal4")));
  }
}