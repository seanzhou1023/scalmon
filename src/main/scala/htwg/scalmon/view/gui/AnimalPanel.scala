package htwg.scalmon.view.gui

import htwg.scalmon.view.Helper._
import htwg.scalmon.model.Animal
import java.awt.Color

class AnimalPanel(val a: Animal) extends swing.BoxPanel(swing.Orientation.Vertical) {
  val imageLabel = new ImageLabel(a.image)
  val valueLabel = new Label(
    <html>
      <table>
        <tr><td>Life: </td><td>%0 / %1</td></tr>
        <tr><td>Speed:</td><td>%2</td></tr>
        <tr><td>Block:</td><td>%3</td></tr>
        <tr><td>Crit: </td><td>%4%</td></tr>
      </table>
    </html>.toString)

  val buttons = List(
    s"<html>DMG: ${a.variationBetween(a.baseAttackValue)}<br /></html>",
    s"<html>HEAL: ${a.variationBetween(a.baseAttackValue)}<br /></html>",
    s"<html><center>DMG: ${a.variationBetween(a.baseAttackValue * 2)}<br />" +
    s"SELF DMG: ${a.variationBetween(a.baseAttackValue / 2)}</center></html>").map(new swing.Button(_))

  contents += new Label("<html>%0</html>", a.name)
  contents ++= imageLabel :: valueLabel :: new swing.Separator :: buttons

  def update(activeAnimal: Animal) {
    imageLabel.enabled = a.alive

    valueLabel.setArgs(
      a.healthPoints,
      a.initHealthPoints,
      a.initSpeed,
      a.baseBlockValue,
      roundAt(2)(a.criticalChance * 100))

    val textColor = if (a.alive) Color.black else Color.gray
    val buttonsActive = activeAnimal == a
    valueLabel.foreground = textColor

    buttons.foreach(b => { b.enabled = buttonsActive; b.foreground = textColor })
  }

  update(null)
}