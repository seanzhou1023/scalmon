package htwg.scalmon.model

import htwg.scalmon.Listener

class Model(val gameSize: Int) {
  private var listeners: List[Listener] = Nil

  def addListener(ln: Listener) = listeners = ln :: listeners
  def removeListener(ln: Listener) = listeners = listeners.filterNot(_ == ln)
  def notifyListeners = listeners.foreach(_.update)

  val playerA = new Player(gameSize)
  val playerB = new Player(gameSize)
}