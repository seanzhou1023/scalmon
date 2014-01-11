package htwg.scalmon.view

import htwg.scalmon.model._
import htwg.scalmon.controller._
import htwg.scalmon.view.wui._

import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

class WUI(_model: Model, _controller: Controller) extends View(_model, _controller) {
  implicit val system = ActorSystem("on-spray-can")
  val service = system.actorOf(Props[ServiceActor], "demo-service")
  implicit val timeout = Timeout(5.seconds)

  Bypass.model = model
  Bypass.controller = controller

  def update(info: Option[AbilityInfo]) = {
    model.state match {
      case Init(_) => // nothing to do
      case Exited  => system.shutdown
      case _ => {
        Bypass.battlefieldText = info.getOrElse(Bypass.lastInfo.getOrElse("Battlefield")).toString
        Bypass.lastInfo = info
      }
    }
  }

  def show = IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}