package htwg.scalmon.view.wui

import htwg.scalmon.model._
import htwg.scalmon.view.Helper._

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)

}

object ModelHack {
  var model: Model = null
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val route =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>scalmon web</i>!</h1>
                {drawAnimals(ModelHack.model.playerA)}
                {battlefield}
                {drawAnimals(ModelHack.model.playerB)}
              </body>
            </html>
          }
        }
      }
    } ~
    (post | parameter('method ! "post")) {
      path("test") {
        complete("Post ok.")
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
}
