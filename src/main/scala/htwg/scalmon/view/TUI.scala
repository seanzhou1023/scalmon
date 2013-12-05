package htwg.scalmon.view

import htwg.scalmon.model._
import htwg.scalmon.controller.Controller

class TUI(_model: Model, _controller: Controller) extends View(_model, _controller) {
  def update(info: Option[AbilityInfo]) = println("TUI update: " + info)
  def show = println("TUI shows up")
}
