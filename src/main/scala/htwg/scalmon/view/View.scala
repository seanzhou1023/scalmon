package htwg.scalmon.view

import htwg.scalmon.controller.Controller
import htwg.scalmon.model._
import htwg.scalmon.Listener

abstract class View(val model: Model, val controller: Controller) extends Listener {
  model.addListener(this)

  def show
  def update(info: Option[AbilityInfo])
}

object Helper {
  def roundAt(p: Int)(n: Double): Double = {
    val s = math pow (10, p)
    (math round n * s) / s
  }

  def parseInt(str: String) = {
    try {
      str.toInt
    } catch {
      case ex: Exception => -1
    }
  }
}