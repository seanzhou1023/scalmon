package htwg.scalmon.view

import htwg.scalmon.model._
import htwg.scalmon.controller._
import htwg.scalmon.view.gui._

class GUI(_model: Model, _controller: Controller) extends View(_model, _controller) {
  val initFrame = new InitFrame(model, controller)
  lazy val mainFrame = new ScalmonFrame(model, controller)

  def update(info: Option[AbilityInfo]) = {
    model.state match {
      case Init(_) => // nothing to do
      case Exited  => { initFrame.dispose; mainFrame.dispose }
      case _       => { initFrame.close; mainFrame.update(info) }
    }
  }

  def show = initFrame.visible = true
}