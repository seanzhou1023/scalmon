package htwg.scalmon

import htwg.scalmon.model.Model
import htwg.scalmon.controller.Controller
import htwg.scalmon.view._

object Main extends App {
  def getGameSize(args: Array[String]) = {
    var size = 1
    val idx = args.indexOf("-s") + 1

    if (idx > 0 && idx < args.length) {
      try {
        size = args(idx).toInt
      }
    }

    if (size > 1)
      size
    else
      1
  }

  def generateView(args: Array[String], model: Model, controller: Controller): View = {
    args.foreach(_ match {
      case "-g" => return null //new GUI(model, controller)
      case "-w" => return null //new WUI(model, controller)
      case _ =>
    })

    new TUI(model, controller)
  }

  override def main(args: Array[String]) = {
    println("scalmon is ready")

    val model = new Model(getGameSize(args))
    val controller = new Controller(model)
    val view: View = generateView(args, model, controller)
    model.notifyListeners
  }
}