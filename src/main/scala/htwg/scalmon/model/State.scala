package htwg.scalmon.model

import htwg.scalmon.controller.Ability

abstract class State

case class Init(val secondPlayer: Boolean = false) extends State

case class Round(val number: Int, val chooseAttackFor: Animal, val attacks: Map[Animal, Ability]) extends State

case class RunRound(val number: Int, val attacks: List[(Animal, Ability)]) extends State

case class GameOver(val winner: Player) extends State

case object Exited extends State

abstract class AbilityInfo

case class AttackInfo(val attacker: Animal, val victim: Animal, val damage: Int) extends AbilityInfo

case class HealInfo(val healer: Animal, val cured: Animal, val healthPoints: Int) extends AbilityInfo