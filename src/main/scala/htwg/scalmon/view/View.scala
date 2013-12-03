package htwg.scalmon.view

import htwg.scalmon.controller.Controller
import htwg.scalmon.model._
import htwg.scalmon.Listener

abstract class View(val model: Model, val controller: Controller) extends Listener {
  model.addListener(this)

  def show
  def update(info: Option[AbilityInfo])
}