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
        case 'q' :: Nil                                   => controller.handle(Quit)
        case 'h' :: Nil | 'h' :: 'e' :: 'l' :: 'p' :: Nil => printHelp
        case 's' :: other if (initialized)                => printAnimalState(other.filter(_ != ' '))
        case _ => input.split(',').toList.map(_.trim) match {
          case playerName :: animalNames if (animalNames.size == model.gameSize) => controller.handle(SetPlayer(playerName, animalNames))
          case _ =>
        }
      }
    }
  }

  def show {
    inputThread.start

    println("\nWelcome to " + BuildInfo.name.toUpperCase() + "!")
    update(None)
  }

  def update(info: Option[AbilityInfo]) {
    if (info != None)
      println(info)

    model.state match { // print what the user has to do
      case Init(b) => printInit(b)
      case _       => println("TUI update: " + info)
    }
  }

  def printHelp {
    println("""Commands:
q               : quits game
h               : prints this help
s               : prints players and animals
s [a|b] <number>: prints details about number's animal of player A resp. B
""")
    update(None)
  }

  def printInit(playerB: Boolean) {
    val p1 = if (playerB) "opponent's" else "your"
    val p2 = if (playerB) "his" else "your"
    val plural = if (model.gameSize > 1) "s" else ""

    println(s"Please enter ${p1} name and the name of ${p2} ${model.gameSize} animal${plural} (comma separated):")
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
    val bAV = a.baseAttackValue
    println(f"\n${a.name}\nLife:  ${a.healthPoints}%3d / ${a.initHealthPoints}\nSpeed: ${a.initSpeed}%3d\nBlock: ${a.baseBlockValue}%3d\nCrit:  ${roundAt(2)(a.criticalChance * 100)}%6.2f%%")
    println(s"Attacks:\n- DMG  ${a.variationBetween(bAV)}\n- HEAL ${a.variationBetween(bAV)}\n- DMG  ${a.variationBetween(bAV * 2)} with SELF DMG ${a.variationBetween(bAV / 2)}")
  }
}