package htwg.scalmon.view.gui

import htwg.scalmon.BuildInfo
import htwg.scalmon.utils.Log
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
  
  var choosedAbility: Int = 0
  
  for (animalPanel <- playerA.animalPanels) {
    animalPanel.reactions += {
      case swing.event.ButtonClicked(b: AbilityButton) => {
        choosedAbility = b.ability
        Log(s"GUI: Click AbilityButton: ${b.ability}.")
      }
    }
  }

  for (animalPanel <- playerB.animalPanels) {
    animalPanel.reactions += {
      case swing.event.MouseClicked(component: ImageLabel, _, _, _, _) => {
        if (choosedAbility != 0) {
          Log(s"GUI: MouseClicked ImageLabel: ${component.instance} with Ability ${choosedAbility}.")
          controller.handle(Ability(choosedAbility, component.instance))
          while (model.state.isInstanceOf[RunRound]) {
            controller.handle(RunStep)
          }
          choosedAbility = 0
        } else {
          Log("GUI: MouseClicked: but no choosedAbility.")
        }
      }
    }
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