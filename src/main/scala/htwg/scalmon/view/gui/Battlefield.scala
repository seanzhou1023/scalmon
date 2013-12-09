package htwg.scalmon.view.gui

import htwg.scalmon.model.AbilityInfo

class Battlefield extends swing.Label {

  var lastInfo: Option[AbilityInfo] = None

  def update(info: Option[AbilityInfo]) {
    text = info.getOrElse(lastInfo.getOrElse("Battlefield")).toString()
    lastInfo = info
  }

  preferredSize = new swing.Dimension(0, 50)
}