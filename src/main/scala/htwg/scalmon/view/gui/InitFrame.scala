package htwg.scalmon.view.gui

import htwg.scalmon.BuildInfo
import htwg.scalmon.controller._
import htwg.scalmon.model._

class InitFrame(val model: Model, val controller: Controller) extends swing.Frame {
  title = BuildInfo.name + " " + BuildInfo.version
  val textFields = for (i <- 0 to model.gameSize) yield new swing.TextField // first is for player name
  val l1 = new Label("<html><center>Please enter %0</center></html>", "your name")
  val l2 = new Label("<html>and the names of %0 animals</html>", "your")

  contents = new swing.BorderPanel {
    add(new swing.Label("<html>Welcome to " + BuildInfo.name.toUpperCase() + "!</html>"),
      swing.BorderPanel.Position.North)

    add(new swing.BoxPanel(swing.Orientation.Vertical) {
      contents += new VSpace
      contents ++= l1 +: textFields.head +: new VSpace +: l2 +: textFields.tail
      contents += new VSpace
    }, swing.BorderPanel.Position.Center)

    add(new swing.Button(swing.Action("Ok") {
      if (textFields.forall(_.text.length > 0)) {
        controller.handle(SetPlayer(textFields.head.text, textFields.tail.map(_.text).toList))

        if (model.state == Init(true)) {
          l1.setArgs("the name of your opponent")
          l2.setArgs("opponent's")
          textFields.foreach(_.text = "")
          textFields.head.requestFocus
        }
      }
    }), swing.BorderPanel.Position.South)
  }

  minimumSize = new swing.Dimension(math.max(size.width, 300), size.height + 20)
  centerOnScreen

  override def closeOperation {
    controller.handle(Quit)
    super.closeOperation
  }
}