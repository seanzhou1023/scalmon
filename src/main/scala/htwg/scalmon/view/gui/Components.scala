package htwg.scalmon.view.gui

import htwg.scalmon.model._
import htwg.scalmon.utils.ImageWrapper

class Label(val format: String, args: Any*) extends swing.Label {
  def setArgs(args: Any*) {
    var t = format

    for (i <- 0 until args.length)
      t = t.replaceAll("%" + i, args(i).toString)

    text = t
  }

  setArgs(args: _*)
}

class ImageLabel(val imageWrapper: ImageWrapper, val instance: Animal) extends swing.Label {
  icon = new javax.swing.ImageIcon(imageWrapper.get(this))

  override def repaint {
    icon = new javax.swing.ImageIcon(imageWrapper.get())
    super.repaint
  }
}

class PlayerPanel(player: Player) extends swing.FlowPanel {
  val animalPanels: List[AnimalPanel] = if (player != null) player.animals.map(new AnimalPanel(_)).toList else Nil
  contents ++= animalPanels

  def update(active: Animal) = animalPanels.foreach(_.update(active))
}

class VSpace(height: Int = 10) extends swing.Component {
  minimumSize = new swing.Dimension(0, height)
  preferredSize = minimumSize
}

class AbilityButton(text: String, val ability: Int) extends swing.Button(text: String)

class Timer(interval: Int, repeats: Boolean, op: => Unit)
  extends javax.swing.Timer(interval,
    new javax.swing.AbstractAction() {
      def actionPerformed(e: java.awt.event.ActionEvent) = op
    }) {
  setRepeats(repeats)

  def active = isRunning

  def active_=(run: Boolean): Boolean = {
    (isRunning(), run) match {
      case (false, true) => start()
      case (true, false) => stop()
      case _             =>
    }
    isRunning()
  }
}

object Timer {
  def apply(interval: Int, repeats: Boolean = true)(op: => Unit) = new Timer(interval, repeats, op)
}