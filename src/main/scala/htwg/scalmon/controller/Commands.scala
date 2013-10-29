package htwg.scalmon.controller

abstract class Command

case class SetPlayer(val playerName: String, val animalNames: List[String]) extends Command