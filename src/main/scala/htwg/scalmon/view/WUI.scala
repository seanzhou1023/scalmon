package htwg.scalmon.view

import htwg.scalmon.model._
import htwg.scalmon.controller._
import htwg.scalmon.view.wui.MyServiceActor
import htwg.scalmon.view.wui.ModelHack

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

class WUI(_model: Model, _controller: Controller) extends View(_model, _controller) {
  implicit val system = ActorSystem("on-spray-can")
  val service = system.actorOf(Props[MyServiceActor], "demo-service")
  implicit val timeout = Timeout(5.seconds)

  ModelHack.model = model

  def update(info: Option[AbilityInfo]) = {
    model.state match {
      case Init(_) => // nothing to do
      case Exited  => { }
      case _       => { }
    }
  }

  def show = IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)

}
