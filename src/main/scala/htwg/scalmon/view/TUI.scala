package htwg.scalmon.view

import htwg.scalmon.model.Model
import htwg.scalmon.controller.Controller

class TUI(_model: Model, _controller: Controller) extends View(_model, _controller) {
  def update = println("TUI update")
}