package htwg.scalmon.view

import htwg.scalmon.BuildInfo
import htwg.scalmon.model._
import htwg.scalmon.controller.Controller

class GUI(_model: Model, _controller: Controller) extends View(_model, _controller) {
  var frame: swing.Frame = new InitFrame

  def update(info: Option[AbilityInfo]) = println("GUI update: " + info)
  def update(players: (Player, Player)) = {
    val oldframe = frame
    frame = new ScalmonFrame(players)
    show
    hide(oldframe)
  }

  def show = {
    frame.visible = true
  }

  def hide(context: swing.Frame) = {
    context.dispose
    context.close
  }

}

class ScalmonFrame(players: (Player, Player)) extends swing.Frame {
  title = BuildInfo.name + " " + BuildInfo.version
  contents = new swing.BorderPanel {
    add(new swing.Label("Battlefield"), swing.BorderPanel.Position.Center)
    add(drawPlayers(players._1), swing.BorderPanel.Position.North)
    add(drawPlayers(players._2), swing.BorderPanel.Position.South)
  }

  def drawPlayers(player: Player) = new swing.FlowPanel {
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
      s"SELF DMG:${a.variationBetween(a.baseAttackValue / 2)}</html>")
  }

  def roundAt(p: Int)(n: Double): Double = {
    val s = math pow (10, p)
    (math round n * s) / s
  }
}

class InitFrame extends swing.Frame {
  title = BuildInfo.name + " " + BuildInfo.version
  contents = new swing.Label("Welcome to SCALMON!\nPlease wait...")
}
