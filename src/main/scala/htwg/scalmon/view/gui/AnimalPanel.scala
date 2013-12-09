package htwg.scalmon.view.gui

import htwg.scalmon.view.Helper._
import htwg.scalmon.model.Animal
import java.awt.Color

class AnimalPanel(val a: Animal)
  extends swing.BoxPanel(swing.Orientation.Vertical) {

  val imageLabel = new ImageLabel(a.image, a)
  val valueLabel = new Label(
    <html>
      <table>
        <tr><td>Life: </td><td>%0 / %1</td></tr>
        <tr><td>Speed:</td><td>%2</td></tr>
        <tr><td>Block:</td><td>%3</td></tr>
        <tr><td>Crit: </td><td>%4%</td></tr>
		<tr><td>Crit: </td><td>%5</td></tr>
      </table>
    </html>.toString)

  val b1_text =
    <html>
      DMG:{ a.variationBetween(a.baseAttackValue) }<br/>
    </html>.toString

  val b2_text =
    <html>
      HEAL:{ a.variationBetween(a.baseAttackValue) }<br/>
    </html>.toString

  val b3_text =
    <html>
      <center>
        DMG:{ a.variationBetween(a.baseAttackValue * 2) }<br/>
        SELF DMG:{ a.variationBetween(a.baseAttackValue / 2) }
      </center>
    </html>.toString

  val buttons = List(
    new AbilityButton(b1_text) { ability = 1 },
    new AbilityButton(b2_text) { ability = 2 },
    new AbilityButton(b3_text) { ability = 3 })

  contents += new Label("<html>%0</html>", a.name)
  contents ++= imageLabel :: valueLabel :: new swing.Separator :: buttons

  for (button <- buttons) this.listenTo(button)
  
  this.listenTo(imageLabel.mouse.clicks)

  def roundAt(p: Int)(n: Double): Double = {
    val s = math pow (10, p)
    (math round n * s) / s
  }

  def update(activeAnimal: Animal) {
    imageLabel.enabled = a.alive

    valueLabel.setArgs(
      a.healthPoints,
      a.initHealthPoints,
      a.initSpeed,
      a.baseBlockValue,
      roundAt(2)(a.criticalChance * 100),
      a.animalType)

    val textColor = if (a.alive) Color.black else Color.gray
    val buttonsActive = activeAnimal == a
    valueLabel.foreground = textColor

    buttons.foreach(b => { b.enabled = buttonsActive; b.foreground = textColor })
  }

  update(null)
}
