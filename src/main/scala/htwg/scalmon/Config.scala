package htwg.scalmon

object UserInterface extends Enumeration {
  type UserInterface = Value
  val Textual, Graphical, Web, All = Value
}

import UserInterface._

case class Config(userInterface: UserInterface = Textual, size: Int = 1)

class Parser extends scopt.OptionParser[Config]("scalmon") {
  val listT = List("t", "textual")
  val listG = List("g", "graphical")
  val listW = List("w", "web")
  val listA = List("a", "all")

  val listValid = listT ::: listG ::: listW ::: listA

  head(BuildInfo.name, BuildInfo.version)

  opt[Int]('s', "size")
    .action { (x, c) => c.copy(size = x) }
    .text("Specify the number of animals per player.")
    .validate { x => if (x > 0) success else failure("Option --size must be > 0") }

  opt[String]('u', "ui")
    .action { (x, c) =>
      c.copy(userInterface = x match {
        case s if (listT.contains(s)) => Textual
        case s if (listG.contains(s)) => Graphical
        case s if (listW.contains(s)) => Web
        case s if (listA.contains(s)) => All
      })
    }
    .text("Specify the user interface with [" + listValid.mkString("|") + "].")
    .validate { x => if (listValid.contains(x)) success else failure("Invalid value '" + x + "' for option --ui") }

  help("help") text "Prints usage text."

  version("version") text "Show version number."
}