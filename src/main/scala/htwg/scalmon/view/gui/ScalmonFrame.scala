package htwg.scalmon.view.gui

import htwg.scalmon.BuildInfo
import htwg.scalmon.controller._
import htwg.scalmon.model._

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
    contents += new ImageLabel(a.image)
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
      s"<html><center>DMG: ${a.variationBetween(a.baseAttackValue * 2)}<br />" +
      s"SELF DMG: ${a.variationBetween(a.baseAttackValue / 2)}</center></html>")
  }

  def roundAt(p: Int)(n: Double): Double = {
    val s = math pow (10, p)
    (math round n * s) / s
  }
}