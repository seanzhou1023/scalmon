package htwg.scalmon

import htwg.scalmon.controller._
import htwg.scalmon.model._
import htwg.scalmon.view._
import htwg.scalmon.UserInterface._

object Main extends App {
  def generateView(config: Config, model: Model, controller: Controller): List[View] = {
    config.userInterface match {
      case Textual   => new TUI(model, controller) :: Nil
      case Graphical => new GUI(model, controller) :: Nil
      case Web       => Nil
      case All       => List(new TUI(model, controller), new GUI(model, controller))
    }
  }

  override def main(args: Array[String]) = {
    val config = new Parser() parse (args, Config()) getOrElse sys.exit()

    println(BuildInfo.name + " is ready with " + config)

    val model = new Model(config.size)
    val controller = new Controller(model)
    val views = generateView(config, model, controller)

    views.foreach(_.show)
  }
}