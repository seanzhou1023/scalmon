package htwg.scalmon.view.wui

import htwg.scalmon.model._
import htwg.scalmon.controller._
import htwg.scalmon.view.Helper._

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

class ServiceActor extends Actor with ScalmonService {
  def actorRefFactory = context
  def receive = runRoute(route)
}

object ModelHack {  // TODO: make schoener
  var model: Model = null
  var controller: Controller = null
  def activeAnimal = if (model.state.isInstanceOf[Round]) model.state.asInstanceOf[Round].chooseAttackFor else null
}

trait ScalmonService extends HttpService {

  val route =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              {style}
              <body>
                <h1>Say hello to <i>scalmon web</i>!</h1>
                <form action="userAction" method="post">
                  {drawAnimals(ModelHack.model.playerA, "A")}
                  {battlefield}
                  {drawAnimals(ModelHack.model.playerB, "B")}
                  <br /><br />
                  <center>
                  <input type="submit" value="Attack!"></input>
                  </center>
                </form>
              </body>
            </html>
          }
        }
      }
    } ~
    (post | parameter('method ! "post")) {
      path("userAction") {
        formFields('ability, 'animal) { (ability, animal) =>
          val pi: Seq[String] = animal.split(",")
          val player = pi(0)
          val index = pi(1).toInt
          val targetAnimal = 
            if (player == "A")
              ModelHack.model.playerA.animals(index)
            else
              ModelHack.model.playerB.animals(index)
          val command = Ability(ability.toInt, targetAnimal)
          println(command)
          ModelHack.controller.handle(command)
          while (ModelHack.model.state.isInstanceOf[RunRound]) {
            ModelHack.controller.handle(RunStep)
          }
          redirect("/", StatusCodes.Found)
        }
      }
    }

  def drawAnimals(p: Player, AorB: String) =
    <div class="row">
      {for ((animal,i) <- p.animals.zipWithIndex) yield animalTemplate(animal, AorB + "," + i)}
    </div>

  def animalTemplate(a: Animal, idx: String) =
    <div class="block">
    {a.name}
    PICTURE
    <input type="radio" name="animal" value={idx}>{a.name}</input>
    <table>
      <tr><td>Life: </td><td>{a.healthPoints} / {a.initHealthPoints}</td></tr>
      <tr><td>Speed:</td><td>{a.initSpeed}</td></tr>
      <tr><td>Block:</td><td>{a.baseBlockValue}</td></tr>
      <tr><td>Crit: </td><td>{roundAt(2)(a.criticalChance * 100)}%</td></tr>
      <tr><td>Type: </td><td>{a.animalType}</td></tr>
    </table>
      {
        if (ModelHack.activeAnimal == a) {
          <input type="radio" name="ability" value="1">
        	DMG:{ a.variationBetween(a.baseAttackValue) }
          </input>
        } else {
          <input type="radio" disabled="disabled" name="ability" value="1">
        	DMG:{ a.variationBetween(a.baseAttackValue) }
          </input>
        }
      }
      <br/>
      {
        if (ModelHack.activeAnimal == a) {
          <input type="radio" name="ability" value="2">
        	HEAL:{ a.variationBetween(a.baseAttackValue) }
          </input>
        } else {
          <input type="radio" disabled="disabled" name="ability" value="2">
        	HEAL:{ a.variationBetween(a.baseAttackValue) }
          </input>
        }
      }
      <br/>
      {
        if (ModelHack.activeAnimal == a) {
          <input type="radio" name="ability" value="3">
        	DMG:{ a.variationBetween(a.baseAttackValue * 2) }<br/>
        	SELF DMG:{ a.variationBetween(a.baseAttackValue / 2) }
          </input>
        } else {
          <input type="radio" disabled="disabled" name="ability" value="3">
        	DMG:{ a.variationBetween(a.baseAttackValue * 2) }<br/>
        	SELF DMG:{ a.variationBetween(a.baseAttackValue / 2) }
          </input>
        }
      }
    </div>

  def battlefield =
    <div align="center"><strong>Battlefield</strong></div>

  def style =
    <style>
      .row {{
        width: 100%;
        text-align: center;
      }}
      .block {{
        width: 150px;
        display: inline-block;
        zoom: 1;
      }}
    </style>

}
