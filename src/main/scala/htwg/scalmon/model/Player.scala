package htwg.scalmon.model

import htwg.scalmon.utils.ImageLoader

class Player(val name: String, val animals: Array[Animal]) {

  val image = ImageLoader.load(name)
}