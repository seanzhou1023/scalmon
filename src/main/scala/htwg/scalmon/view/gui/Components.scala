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

class AbilityButton(text: String) extends swing.Button(text: String) {
  var abilityNr = 0
  def ability: Int = abilityNr
  def ability_=(nr: Int) = abilityNr = nr
}