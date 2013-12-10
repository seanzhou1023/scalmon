package htwg.scalmon.view.gui

import htwg.scalmon.BuildInfo
import htwg.scalmon.utils.Log
import htwg.scalmon.controller._
import htwg.scalmon.model._

class ScalmonFrame(val model: Model, val controller: Controller) extends swing.Frame {
  title = BuildInfo.name + " " + BuildInfo.version

  val battleField = new Battlefield
  val playerA = new PlayerPanel(model.playerA)
  val playerB = new PlayerPanel(model.playerB)
  val timer = Timer(1500) { controller.handle(RunStep) }

  contents = new swing.BorderPanel {
    add(battleField, swing.BorderPanel.Position.Center)
    add(playerA, swing.BorderPanel.Position.North)
    add(playerB, swing.BorderPanel.Position.South)
  }

  var choosedAbility: Int = 0

  def setupAbilityButtonReactions(playerPanel: PlayerPanel) =
    for (animalPanel <- playerPanel.animalPanels) {
      animalPanel.reactions += {
        case swing.event.ButtonClicked(b: AbilityButton) => {
          choosedAbility = b.ability
        }
      }
    }

  setupAbilityButtonReactions(playerA)

  def setupImageLabelReactions(playerPanel: PlayerPanel) =
    for (animalPanel <- playerPanel.animalPanels) {
      animalPanel.reactions += {
        case swing.event.MouseClicked(component: ImageLabel, _, _, _, _) => {
          if (choosedAbility != 0) {
            controller.handle(Ability(choosedAbility, component.instance))
            choosedAbility = 0
          }
        }
      }
    }

  setupImageLabelReactions(playerA)
  setupImageLabelReactions(playerB)

  override def closeOperation {
    controller.handle(Quit)
    timer.stop
    super.closeOperation
  }

  def update(info: Option[AbilityInfo]) = {
    visible = true

    timer.active = model.state.isInstanceOf[RunRound]

    battleField.update(info)

    val activeAnimal = if (model.state.isInstanceOf[Round]) model.state.asInstanceOf[Round].chooseAttackFor else null
    playerA.update(activeAnimal)
    playerB.update(activeAnimal)
  }
}