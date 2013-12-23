package htwg.scalmon.view.wui

import htwg.scalmon.model._
import htwg.scalmon.view.Helper._

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

object ModelHack {
  var model: Model = null
}

trait MyService extends HttpService {

  val route =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Say hello to <i>scalmon web</i>!</h1>
                {drawAnimals(ModelHack.model.playerA)}
                {battlefield}
                {drawAnimals(ModelHack.model.playerB)}
                {formField}
              </body>
            </html>
          }
        }
      }
    } ~
    (post | parameter('method ! "post")) {
      path("test") {
        formFields('name, 'send) { (name, send) =>
          complete{"Post ok " + name + send}
        }
      }
    }

  def drawAnimals(p: Player) =
    <div>
      {p.animals.map(animalTemplate)}
    </div>

  def animalTemplate(a: Animal) =
    <div>
    {a.name}
    PICTURE
    <table>
      <tr><td>Life: </td><td>{a.healthPoints} / {a.initHealthPoints}</td></tr>
      <tr><td>Speed:</td><td>{a.initSpeed}</td></tr>
      <tr><td>Block:</td><td>{a.baseBlockValue}</td></tr>
      <tr><td>Crit: </td><td>{roundAt(2)(a.criticalChance * 100)}%</td></tr>
      <tr><td>Type: </td><td>{a.animalType}</td></tr>
    </table>
    DMG:{ a.variationBetween(a.baseAttackValue) }<br/>
    HEAL:{ a.variationBetween(a.baseAttackValue) }<br/>
    DMG:{ a.variationBetween(a.baseAttackValue * 2) }<br/>
    SELF DMG:{ a.variationBetween(a.baseAttackValue / 2) }
    </div>

  def battlefield =
    <div><strong>Battlefield</strong></div>

  def formField =
    <form action="test" method="post">
      Name: <input type="text" name="name"></input>
      <input type="submit" name="send" value="Send"></input>
    </form>
}
