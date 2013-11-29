package htwg.scalmon

import htwg.scalmon.model.AbilityInfo

trait Listener {
  def update(info: Option[AbilityInfo])
}