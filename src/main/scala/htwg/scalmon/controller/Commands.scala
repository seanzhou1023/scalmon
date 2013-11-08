package htwg.scalmon.controller

import htwg.scalmon.model.Animal

abstract class Command

case class SetPlayer(val playerName: String, val animalNames: List[String]) extends Command

case class Ability(val skill: Int, val target: Animal) extends Command

case object RunStep extends Command