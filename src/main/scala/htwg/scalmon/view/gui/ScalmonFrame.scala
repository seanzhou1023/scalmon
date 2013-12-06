package htwg.scalmon.view.gui

import htwg.scalmon.BuildInfo
import htwg.scalmon.controller._
import htwg.scalmon.model._

class ScalmonFrame(val model: Model, val controller: Controller) extends swing.Frame {
  title = BuildInfo.name + " " + BuildInfo.version

  val battleField = new swing.Label("Battlefield")
  val playerA = new PlayerPanel(model.playerA)
  val playerB = new PlayerPanel(model.playerB)

  contents = new swing.BorderPanel {
    add(battleField, swing.BorderPanel.Position.Center)
    add(playerA, swing.BorderPanel.Position.North)
    add(playerB, swing.BorderPanel.Position.South)
  }

  override def closeOperation {
    controller.handle(Quit)
    super.closeOperation
  }

  def update(info: Option[AbilityInfo]) = {
    visible = true

    //battleField.update(info) // TODO GUI: battleField

    val activeAnimal = if (model.state.isInstanceOf[Round]) model.state.asInstanceOf[Round].chooseAttackFor else null
    playerA.update(activeAnimal)
    playerB.update(activeAnimal)
  }
}