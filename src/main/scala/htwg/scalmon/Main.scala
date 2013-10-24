package htwg.scalmon

import htwg.scalmon.model.Model
import htwg.scalmon.controller.Controller
import htwg.scalmon.view._

object Main extends App {
  def generateView(args: Array[String], model: Model, controller: Controller): View = {
    args.foreach(_ match {
      case "-g" => return null //new GUI(model, controller)
      case "-w" => return null //new WUI(model, controller)
    })
    
    new TUI(model, controller)
  }

  override def main(args: Array[String]) = {
    println("scalmon is ready")

    val model = new Model
    val controller = new Controller(model)
    val view: View = generateView(args, model, controller)
    model.notifyListeners
  }
}