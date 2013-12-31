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

// TODO: Spieler und Animals anlegen Dialog

object Bypass {
  var model: Model = null
  var controller: Controller = null
  def activeAnimal =
    if (model.state.isInstanceOf[Round])
      model.state.asInstanceOf[Round].chooseAttackFor
    else
      null
  var lastInfo: Option[AbilityInfo] = None
  var battlefieldText = ""
}

trait ScalmonService extends HttpService {

  val route =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              { style }
              <body>
                <h1>Say hello to <i>scalmon web</i>!</h1>
                <form action="userAction" method="post">
                  { drawAnimals(Bypass.model.playerA, "A") }
                  { battlefield }
                  { drawAnimals(Bypass.model.playerB, "B") }
                  <br/><br/>
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
                Bypass.model.playerA.animals(index)
              else
                Bypass.model.playerB.animals(index)

            val command = Ability(ability.toInt, targetAnimal)
            Bypass.controller.handle(command)

            while (Bypass.model.state.isInstanceOf[RunRound]) {
              Bypass.controller.handle(RunStep)
            }

            redirect("/", StatusCodes.Found)
          }
        }
      }

  def drawAnimals(p: Player, AorB: String) =
    <div class="row">
      {
        for ((animal, i) <- p.animals.zipWithIndex)
          yield animalTemplate(animal, AorB + "," + i)
      }
    </div>

  def animalTemplate(a: Animal, idx: String) =
    <div class="block">
      <div style="text-align: center;">
        { a.name }<br/>
        {
          if (a.alive)
            <input type="radio" align="center" name="animal" value={ idx }/>
          else
            <input type="radio" align="center" name="animal" value={ idx } disabled="disabled"/>
        }
      </div>
      <img src={ "data:image/png;base64," + a.image.asBase64 }/>
      <table>
        <tr><td>Life: </td><td>{ a.healthPoints } / { a.initHealthPoints }</td></tr>
        <tr><td>Speed:</td><td>{ a.initSpeed }</td></tr>
        <tr><td>Block:</td><td>{ a.baseBlockValue }</td></tr>
        <tr><td>Crit: </td><td>{ roundAt(2)(a.criticalChance * 100) }%</td></tr>
        <tr><td>Type: </td><td>{ a.animalType }</td></tr>
      </table>
      {
        if (Bypass.activeAnimal == a) {
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
        if (Bypass.activeAnimal == a) {
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
        if (Bypass.activeAnimal == a) {
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
    <div align="center">
      <br/><br/>
      <strong>{ Bypass.battlefieldText }</strong>
      <br/><br/>
    </div>

  def styleUnparsed = scala.xml.Unparsed("""
     @import url(http://fonts.googleapis.com/css?family=Source+Sans+Pro:300);
	 body {
  		font-family: "Source Sans Pro", sans-serif;
        font-weight: 300;
  		text-align: center;
  		margin: 50px 0px;
  		padding: 0px;
  	  }
      .row {
        width: 100%;
        text-align: center;
      }
      .block {
  		text-align: left;
        width: 200px;
		padding: 15px;
        display: inline-block;
        zoom: 1;
      }
  """)

  def style = <style>{ styleUnparsed }</style>

}
