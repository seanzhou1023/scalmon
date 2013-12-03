package htwg.scalmon.view

import htwg.scalmon.BuildInfo
import htwg.scalmon.model._
import htwg.scalmon.controller._

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

class ScalmonFrame(val model: Model, val controller: Controller) extends swing.Frame {
  title = BuildInfo.name + " " + BuildInfo.version
  contents = new swing.BorderPanel {
    add(new swing.Label("Battlefield"), swing.BorderPanel.Position.Center)
    add(drawPlayers(model.playerA), swing.BorderPanel.Position.North)
    add(drawPlayers(model.playerB), swing.BorderPanel.Position.South)
  }

  override def closeOperation {
    controller.handle(Quit)
    super.closeOperation
  }

  def update(info: Option[AbilityInfo]) = {
    visible = true
    // TODO GUI: actualize text of views
  }

  def drawPlayers(player: Player) = new swing.FlowPanel {
    if (player != null)
      for (animal <- player.animals)
        contents += drawAnimal(animal)
  }

  def drawAnimal(a: Animal) = new swing.BoxPanel(swing.Orientation.Vertical) {
    contents += new swing.Label(a.name)
    contents += new swing.Label {
      icon = new javax.swing.ImageIcon(a.image, a.name)
    }
    contents += new swing.Label(
      s"Life: ${a.healthPoints}/${a.initHealthPoints}")
    contents += new swing.Label(
      s"Speed: ${a.initSpeed}")
    contents += new swing.Label(
      s"Block: ${a.baseBlockValue}")
    contents += new swing.Label(
      s"Crit: ${roundAt(2)(a.criticalChance * 100)}%")
    contents += new swing.Separator
    contents += new swing.Button(
      s"<html>DMG: ${a.variationBetween(a.baseAttackValue)}<br /></html>")
    contents += new swing.Button(
      s"<html>HEAL: ${a.variationBetween(a.baseAttackValue)}<br /></html>")
    contents += new swing.Button(
      s"<html>DMG: ${a.variationBetween(a.baseAttackValue * 2)}<br />" +
      s"SELF DMG: ${a.variationBetween(a.baseAttackValue / 2)}</html>")
  }

  def roundAt(p: Int)(n: Double): Double = {
    val s = math pow (10, p)
    (math round n * s) / s
  }
}

class InitFrame(val model: Model, val controller: Controller) extends swing.Frame {
  title = BuildInfo.name + " " + BuildInfo.version
  contents = new swing.Label("Welcome to SCALMON!\nPlease wait...")
  // TODO GUI: input dialogs

  override def closeOperation {
    controller.handle(Quit)
    super.closeOperation
  }
}
