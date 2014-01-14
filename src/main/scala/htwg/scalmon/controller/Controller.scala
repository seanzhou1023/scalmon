package htwg.scalmon.controller

import htwg.scalmon.model._
import htwg.scalmon.utils.Log
import scala.util.Random
import scala.math.random

class Controller(val model: Model, val logExceptions: Boolean = true) {

  def handle(command: Command) {
    try {
      model.synchronized {
        command match {
          case x: SetPlayer => cmdSetPlayer(x)
          case x: Ability   => cmdAbility(x)
          case RunStep      => cmdRunStep
          case Restart      => cmdRestart
          case Quit         => model.state = Exited
          case other        => throw new IllegalArgumentException("unknown command: " + other)
        }
      }
    } catch {
      case e: Exception => if (logExceptions) Log(e) else throw e
    }

    model.notifyListeners()
  }

  private def cmdSetPlayer(cmd: SetPlayer) {
    if (cmd.animalNames.size != model.gameSize)
      throw new IllegalArgumentException("Number of animal names (" + cmd.animalNames.size + ") has to match the game size (" + model.gameSize + ")")

    val animals = cmd.animalNames.map(name => new Animal(name))
    val player = new Player(cmd.playerName, animals.toArray)

    model.state match {
      case Init(false) =>
        model.playerA = player
        model.state = Init(true)
      case Init(true) =>
        model.playerB = player
        startFight
      case _ => throw new IllegalArgumentException("SetPlayer not allowed in state " + model.state)
    }
  }

  private def startFight {
    model.resetAnimals
    model.state = startRound(1)
  }

  private def startRound(number: Int) = {
    val aliveOfA = model.playerA.animalsAlive

    val aiAttacks =
      for (animal <- model.playerB.animalsAlive)
        yield random match {
        case x if x < 0.2 => (animal, Ability(2, Random.shuffle(model.playerB.animalsAlive).head)) // heal
        case x if x > 0.8 => (animal, Ability(3, Random.shuffle(aliveOfA).head)) // sacrifice attack
        case _            => (animal, Ability(1, Random.shuffle(aliveOfA).head))
      }

    Round(number, aliveOfA.head, aiAttacks.toMap)
  }

  private def cmdAbility(cmd: Ability) {
    model.state match {
      case Round(number, chooseAttackFor, attacks) =>
        val newAttacks = attacks + ((chooseAttackFor, cmd))

        model.state = model.playerA.animalsAlive
          .filterNot(newAttacks.contains(_)) match {
            case Nil => RunRound(number,
              newAttacks.toList.sortWith((a, b) => a._1.initSpeed > b._1.initSpeed))
            case list => Round(number, list.head, newAttacks)
          }

      case _ => throw new IllegalArgumentException("Attack not allowed in state " + model.state)
    }
  }

  private def cmdRunStep {
    model.state match {
      case RunRound(number, attacks) =>
        val (animal, ability) = attacks.head
        val target = ensureTargetValid(ability)
        val info = animal.ability(target)
        model.notifyListeners(Option(info))

        model.state = (model.playerA.beaten, model.playerB.beaten) match {
          case (true, true)  => GameOver(null) // both are beaten
          case (false, true) => GameOver(model.playerA)
          case (true, false) => GameOver(model.playerB)
          case (false, false) =>
            attacks.tail.filter(_._1.alive) match { // only animals which are not knocked out can attack
              case Nil  => startRound(number + 1) // next round
              case list => RunRound(number, list)
            }
        }

      case _ => throw new IllegalArgumentException("RunStep not allowed in state " + model.state)
    }
  }

  private def cmdRestart {
    model.state match {
      case GameOver(_) => startFight
      case _           => throw new IllegalArgumentException("Restart not allowed in state " + model.state)
    }
  }

  private def ensureTargetValid(ability: Ability) = ability.target.alive match {
    case true => ability
    case _ => // if the target animal is dead, randomly choose another of the same player
      val owner = if (model.playerA.animals.contains(ability.target)) model.playerA else model.playerB
      Ability(ability.skill, Random.shuffle(owner.animalsAlive).head)
  }
}