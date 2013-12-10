package htwg.scalmon.view

import htwg.scalmon.BuildInfo
import htwg.scalmon.model._
import htwg.scalmon.controller._
import htwg.scalmon.utils.Log
import Helper._

class TUI(_model: Model, _controller: Controller) extends View(_model, _controller) {

  val inputThread = new Thread(new Runnable {
    override def run() {
      try {
        readInput
      } catch {
        case ex: Exception => Log(ex)
      }
    }
  })

  def readInput {
    while (model.state != Exited) {
      val input = readLine
      val initialized = !model.state.isInstanceOf[Init]

      input.toList match {
        case 'q' :: Nil =>
          controller.handle(Quit)
        case 'r' :: Nil =>
          if (model.state.isInstanceOf[GameOver]) controller.handle(Restart)
        case Nil =>
          if (model.state.isInstanceOf[RunRound]) controller.handle(RunStep)
        case 'h' :: Nil | 'h' :: 'e' :: 'l' :: 'p' :: Nil =>
          printHelp
        case 's' :: other if (initialized) =>
          printAnimalState(other.filter(_ != ' '))
        case i :: ' ' :: p :: ' ' :: t if (i >= '1' && i <= '3' && List('a', 'b').contains(p.toLower)) =>
          parseAbility(i, p.toLower, t)
        case _ =>
          input.split(',').toList.map(_.trim) match {
            case playerName :: animalNames if (animalNames.size == model.gameSize) => controller.handle(SetPlayer(playerName, animalNames))
            case _ => println("wrong input: '" + input + "'"); update(None)
          }
      }
    }
  }

  def parseAbility(skill: Char, player: Char, target: List[Char]) {
    val tIdx = parseInt(target.mkString(""))
    val animals = (if (player == 'a') model.playerA else model.playerB).animals

    if (tIdx > 0 && tIdx <= model.gameSize)
      controller.handle(Ability(skill - '0', animals(tIdx - 1)))
    else
      update(None)
  }

  def show {
    inputThread.start

    println("\nWelcome to " + BuildInfo.name.toUpperCase() + "!")
    update(None)
  }

  def update(info: Option[AbilityInfo]) {
    if (info != None) {
      printAbilityInfo(info.get)
      return
    }

    model.state match { // print what the user has to do
      case Init(b)             => printInit(b)
      case Round(_, animal, _) => printChooseAbility(animal)
      case RunRound(_, _)      => println("Press enter to continue...")
      case GameOver(winner) =>
        println("\n\tGame over!\n\tWinner: " +
          (if (winner != null) winner.name else "None") +
          "!\n\tPress 'r' to restart game!")
      case x => println(x)
    }
  }

  def abilityList(a: Animal) = {
    val bAV = a.baseAttackValue
    List(
      s"DMG  ${a.variationBetween(bAV)}",
      s"HEAL ${a.variationBetween(bAV)}",
      s"DMG  ${a.variationBetween(bAV * 2)} with SELF DMG ${a.variationBetween(bAV / 2)}")
  }

  def printHelp {
    println("""
Commands:
<anr> [a|b] <tnr>: choose an ability for an animal
                   <anr> specifies the skill to use
                   [a|b] specifies the player of the target
                   <tnr> specifies the number's animal of the player

[enter]          : runs the next ability
h                : prints this help
q                : quits game
r                : restarts the game
s                : prints players and animals
s [a|b] <number> : prints details about number's animal of player A resp. B
""")
    update(None)
  }

  def printInit(playerB: Boolean) {
    val p1 = if (playerB) "opponent's" else "your"
    val p2 = if (playerB) "his" else "your"
    val plural = if (model.gameSize > 1) "s" else ""

    println(s"Please enter ${p1} name and the name of ${p2} ${model.gameSize} animal${plural} (comma separated):")
  }

  def printChooseAbility(a: Animal) {
    println(s"Choose the ability for ${a.name} (<ability-number> [a|b] <target-animal-number>):")
    val ab = abilityList(a)

    for (i <- 1 to ab.size)
      println(i + ": " + ab(i - 1))
  }

  def printAbilityInfo(info: AbilityInfo) = info match {
    case a: AttackInfo => println(s"${a.attacker.name} attacks ${a.victim.name} with a damage of ${a.damage}.")
    case h: HealInfo   => println(s"${h.healer.name} healed ${h.cured.name} with ${h.healthPoints} health points.")
  }

  def printAnimalState(list: List[Char]) {
    if (list.length == 0) {
      println("\nPlayers and animals:")

      for (p <- List(model.playerA, model.playerB).filter(_ != null)) {
        println("- " + p.name)
        p.animals.foreach(a => println(f"\t- ${a.name}%-20s (${a.healthPoints}%3d/${a.initHealthPoints}%3d)"))
      }
    } else {
      val i = parseInt(list.tail.mkString(""))
      var player = list.head.toUpper match {
        case 'A' => model.playerA
        case 'B' => model.playerB
        case _   => null
      }

      if (player != null && i > 0 && i <= model.gameSize)
        printAnimal(player.animals(i - 1))
    }

    println
    update(None)
  }

  def printAnimal(a: Animal) {
    println(f"\n${a.name}\nLife:  ${a.healthPoints}%3d / ${a.initHealthPoints}\nSpeed: ${a.initSpeed}%3d\nBlock: ${a.baseBlockValue}%3d\nCrit:  ${roundAt(2)(a.criticalChance * 100)}%6.2f%%")
    abilityList(a).foreach(ab => println("- " + ab))
  }
}