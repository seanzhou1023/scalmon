package htwg.scalmon.controller

import htwg.scalmon.model.Model

class Controller(val model: Model) {

  def handle(command: Command): Unit = command match {
    case SetPlayer(playerName, animalNames) => println(playerName + " - " + animalNames);
    case other => println("unknown command: " + other);
  }
}