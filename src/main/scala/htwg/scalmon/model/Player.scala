package htwg.scalmon.model

import htwg.scalmon.utils.ImageLoader

class Player(val name: String, val animals: Array[Animal]) {

  lazy val image = ImageLoader.get(name)

  def beaten = animals.forall(!_.alive)

  def animalsAlive = animals.toList.filter(_.alive)
}