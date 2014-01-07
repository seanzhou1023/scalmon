package htwg.scalmon.view.gui

import htwg.scalmon.controller._
import htwg.scalmon.model._

class Battlefield(val model: Model, val controller: Controller) extends swing.BorderPanel {

  val label = new swing.Label
  val button = new swing.Button("Start a new match!")
  add(label, swing.BorderPanel.Position.Center)
  add(button, swing.BorderPanel.Position.South)
  listenTo(button)

  reactions += {
    case swing.event.ButtonClicked(b: swing.Button) => {
      controller.handle(Restart)
    }
  }

  var lastInfo: Option[AbilityInfo] = None

  def update(info: Option[AbilityInfo]) {
    var showInfo = info.orElse(lastInfo)
    lastInfo = info

    label.text = model.state.isInstanceOf[GameOver] match {
      case true =>
        button.visible = true
        val winner = model.state.asInstanceOf[GameOver].winner
        "Game over! The winner is " + (if (winner != null) winner.name else "None") + "!"
      case false =>
        button.visible = false
        showInfo.getOrElse(null) match {
          case a: AttackInfo => s"${a.attacker.name} attacks ${a.victim.name} with a damage of ${a.damage}."
          case h: HealInfo   => s"${h.healer.name} healed ${h.cured.name} with ${h.healthPoints} health points."
          case _             => ""
        }
    }
  }

  preferredSize = new swing.Dimension(0, 80)
}