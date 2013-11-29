package htwg.scalmon.model

import htwg.scalmon.Listener

class Model(val gameSize: Int) {
  private var listeners: List[Listener] = Nil

  def addListener(ln: Listener) = listeners = ln :: listeners
  def removeListener(ln: Listener) = listeners = listeners.filterNot(_ == ln)
  def notifyListeners(info: Option[AbilityInfo] = None) = listeners.foreach(_.update(info))

  var playerA: Player = null
  var playerB: Player = null

  var state: State = Init()

  def resetAnimals = (playerA.animals ++ playerB.animals).foreach(_.reset)
}