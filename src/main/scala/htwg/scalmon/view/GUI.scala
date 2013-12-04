package htwg.scalmon.view

import htwg.scalmon.BuildInfo
import htwg.scalmon.model._
import htwg.scalmon.controller._

class GUI(_model: Model, _controller: Controller) extends View(_model, _controller) {
  val initFrame = new InitFrame(model, controller)
  lazy val mainFrame = new ScalmonFrame(model, controller)

  def update(info: Option[AbilityInfo]) = {
    model.state match {
      case Init(_) => // nothing to do
      case Exited  => { initFrame.dispose; mainFrame.dispose }
      case _       => { initFrame.close; mainFrame.update(info) }
    }
  }

  def show = initFrame.visible = true
}

class Label(val format: String, args: String*) extends swing.Label {
  def setArgs(args: String*) {
    var t = format

    for (i <- 0 until args.length)
      t = t.replaceAll("%" + i, args(i))

    text = t
  }

  setArgs(args: _*)
}

class ScalmonFrame(val model: Model, val controller: Controller) extends swing.Frame {
  title = BuildInfo.name + " " + BuildInfo.version
  contents = new swing.BorderPanel {
    add(new swing.Label("Battlefield"), swing.BorderPanel.Position.Center)
    add(drawPlayers(model.playerA), swing.BorderPanel.Position.North)
    add(drawPlayers(model.playerB), swing.BorderPanel.Position.South)
  }

  override def closeOperation {
    controller.handle(Quit)
    super.closeOperation
  }

  def update(info: Option[AbilityInfo]) = {
    visible = true
    // TODO GUI: actualize text of views
  }

  def drawPlayers(player: Player) = new swing.FlowPanel {
    if (player != null)
      for (animal <- player.animals)
        contents += drawAnimal(animal)
  }

  def drawAnimal(a: Animal) = new swing.BoxPanel(swing.Orientation.Vertical) {
    contents += new swing.Label(a.name)
    contents += new swing.Label {
      icon = new javax.swing.ImageIcon(a.image, a.name)
    }
    contents += new swing.Label(
      s"Life: ${a.healthPoints}/${a.initHealthPoints}")
    contents += new swing.Label(
      s"Speed: ${a.initSpeed}")
    contents += new swing.Label(
      s"Block: ${a.baseBlockValue}")
    contents += new swing.Label(
      s"Crit: ${roundAt(2)(a.criticalChance * 100)}%")
    contents += new swing.Separator
    contents += new swing.Button(
      s"<html>DMG: ${a.variationBetween(a.baseAttackValue)}<br /></html>")
    contents += new swing.Button(
      s"<html>HEAL: ${a.variationBetween(a.baseAttackValue)}<br /></html>")
    contents += new swing.Button(
      s"<html>DMG: ${a.variationBetween(a.baseAttackValue * 2)}<br />" +
      s"SELF DMG: ${a.variationBetween(a.baseAttackValue / 2)}</html>")
  }

  def roundAt(p: Int)(n: Double): Double = {
    val s = math pow (10, p)
    (math round n * s) / s
  }
}

class InitFrame(val model: Model, val controller: Controller) extends swing.Frame {
  title = BuildInfo.name + " " + BuildInfo.version
  val textFields = for (i <- 0 to model.gameSize) yield new swing.TextField // first is for player name
  val l1 = new Label("<html><br />Please enter %0</html>", "your name")
  val l2 = new Label("<html><br />and the names of %0 animals</html>", "your")

  contents = new swing.BoxPanel(swing.Orientation.Vertical) {
    contents += new swing.Label("<html>Welcome to " + BuildInfo.name.toUpperCase() + "!</html>")
    contents ++= l1 +: textFields.head +: l2 +: textFields.tail

    contents += new swing.Button(swing.Action("Ok") {
      if (textFields.forall(_.text.length > 0)) {
        controller.handle(SetPlayer(textFields.head.text, textFields.tail.map(_.text).toList))

        if (model.state == Init(true)) {
          l1.setArgs("the name of your opponent")
          l2.setArgs("opponent's")
          textFields.foreach(_.text = "")
          textFields.head.requestFocus
        }
      }
    })
  }

  size = new swing.Dimension(math.max(size.width, 300), size.height + 40)
  centerOnScreen

  override def closeOperation {
    controller.handle(Quit)
    super.closeOperation
  }
}
