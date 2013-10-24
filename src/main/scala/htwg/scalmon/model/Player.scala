package htwg.scalmon.model

class Player(val gameSize: Int) {
  var name = ""

  val animals = new Array[Animal](gameSize)
}