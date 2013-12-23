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
      case Web       => new WUI(model, controller) :: Nil
      case All       => List(new TUI(model, controller), new GUI(model, controller), new WUI(model, controller))
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

    if (config.defaultInit) {
      val animalsA = for (i <- 1 to config.size) yield "A" + i
      val animalsB = for (i <- 1 to config.size) yield "B" + i
      controller.handle(SetPlayer("Player A", animalsA.toList))
      controller.handle(SetPlayer("Player B", animalsB.toList))
    }
  }
}