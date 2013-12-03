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

  override def main(args: Array[String]): Unit = {
    val configOpt = new Parser() parse (args, Config())

    if (configOpt == None)
      return ;

    val config = configOpt.get

    println(BuildInfo.name + " is ready with " + config)

    val model = new Model(config.size)
    val controller = new Controller(model)
    val views = generateView(config, model, controller)

    views.foreach(_.show)

    // test - replace by dialog to add users
    controller.handle(SetPlayer("A", List("Pikachu", "Fette Katze")))
    controller.handle(SetPlayer("B", List("Mauzi", "Arbok")))
  }
}