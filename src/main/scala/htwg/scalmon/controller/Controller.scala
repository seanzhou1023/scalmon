package htwg.scalmon.controller

import htwg.scalmon.model._

class Controller(val model: Model) {

  def handle(command: Command) {
    command match {
      case x: SetPlayer => cmdSetPlayer(x)
      case x: Ability => cmdAbility(x)
      case RunStep => cmdRunStep
      case other => println("unknown command: " + other)
    }

    model.notifyListeners
  }

  private def cmdSetPlayer(cmd: SetPlayer) {
    if (cmd.animalNames.size != model.gameSize) return

    val animals = cmd.animalNames.map(name => new Animal(name))
    val player = new Player(cmd.playerName, animals.toArray)

    model.state match {
      case Init(false) =>
        model.playerA = player
        model.state = Init(true)
      case Init(true) =>
        model.playerB = player
        startFight
      case _ => println("SetPlayer not allowed in state " + model.state)
    }
  }

  private def startFight {
    model.resetAnimals
    startRound(0)
  }

  private def startRound(number: Int) {
    val firstAliveOfA = model.playerA.animals.find(_.alive).get

    val aiAttacks =
      for (animal <- model.playerB.animals.filter(_.alive))
        yield (animal, Ability(1, firstAliveOfA))

    model.state = Round(number, firstAliveOfA, aiAttacks.toMap)
  }

  private def cmdAbility(cmd: Ability) {
    model.state match {
      case Round(number, chooseAttackFor, attacks) =>
        val newAttacks = attacks + ((chooseAttackFor, cmd))

        model.state = model.playerA.animals
          .filterNot(newAttacks.contains(_))
          .find(_.alive) match {
            case Some(next: Animal) => Round(number, next, newAttacks)
            case None => RunRound(number,
              newAttacks.toList.sortWith((a, b) => a._1.initSpeed > b._1.initSpeed))
          }

      case _ => println("Attack not allowed in state " + model.state)
    }
  }

  private def cmdRunStep {
    model.state match {
      case RunRound(number, attacks) =>
        val (animal, ability) = attacks.head
        animal.ability(ability)

        // TODO: test if one player has won => other state !

        attacks.tail match {
          case Nil => startRound(number + 1)
          case list => model.state = RunRound(number, list.filter(_._1.alive))
        }

      case _ => println("RunStep not allowed in state " + model.state)
    }
  }
}