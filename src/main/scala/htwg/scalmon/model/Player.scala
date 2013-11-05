package htwg.scalmon.model

import htwg.scalmon.utils.ImageLoader

class Player(val name: String, val animals: Array[Animal]) {

  lazy val image = ImageLoader.load(name)
}