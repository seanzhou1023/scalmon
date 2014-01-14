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
  var restartButton = false
  var inInitPhase = true
}

trait ScalmonService extends HttpService {

  val route =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            if (Bypass.inInitPhase)
              initFrame
            else {
              <html>
                <head>
                  <title>scalmon web</title>
                  <meta charset="UTF-8" />
                  { style }
                </head>
                <body>
                  <h1>Say hello to <i>scalmon web</i>!</h1>
                  <form action="userAction" method="post">
                    { drawAnimals(Bypass.model.playerA, "A") }
                    { battlefield }
                    { drawAnimals(Bypass.model.playerB, "B") }
                    <br /><br />
                    <input type="submit" name="quit" value="Quit Server" />
                  </form>
                </body>
              </html>
            }
          }
        }
      }
    } ~
      (post | parameter('method ! "post")) {
        path("userAction") {
          formFields('quit.?, 'ability.?, 'animal.?) { (quitOpt, abilityOpt, animalOpt) =>
            if (quitOpt != None) {
              Bypass.controller.handle(Quit)
            }
            else if (abilityOpt == None || animalOpt == None) {
              Bypass.controller.handle(Restart)
            }
            else {
              val ability = abilityOpt.get
              val animal = animalOpt.get
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
            }

            redirect("/", StatusCodes.Found)
          }
        }
      } ~
      (post | parameter('method ! "post")) {
        path("initAction") {
          entity(as[FormData]) { formData =>
            initPlayer("A", formData.fields.toList)
            initPlayer("B", formData.fields.toList)
            redirect("/", StatusCodes.Found)
          }
        }
      }

  def initPlayer(key: String, fields: List[Tuple2[String,String]]) = {
    val playerName = fields.find(_._1 == ("player" + key)).get._2
    val animalNames = fields.filter(_._1.contains("animal" + key)).map(_._2)
    Bypass.controller.handle(SetPlayer(playerName, animalNames)) 
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
            <input type="radio" name="animal" value={ idx }/>
          else
            <input type="radio" name="animal" value={ idx } disabled="disabled"/>
        }
      </div>
      <img src={ "data:image/png;base64," + a.image.asBase64 } alt={ a.name } />
      <table>
        <tr><td>Life: </td><td>{ a.healthPoints } / { a.initHealthPoints }</td></tr>
        <tr><td>Speed:</td><td>{ a.initSpeed }</td></tr>
        <tr><td>Block:</td><td>{ a.baseBlockValue }</td></tr>
        <tr><td>Crit: </td><td>{ roundAt(2)(a.criticalChance * 100) }%</td></tr>
        <tr><td>Type: </td><td>{ a.animalType }</td></tr>
        <tr><td>
        {
          if (Bypass.activeAnimal == a) {
            <input type="radio" name="ability" value="1" />
          } else {
            <input type="radio" disabled="disabled" name="ability" value="1" />
          }
        }
        </td><td>DMG:{ a.variationBetween(a.baseAttackValue) }</td></tr>
        <tr><td>
        {
          if (Bypass.activeAnimal == a) {
            <input type="radio" name="ability" value="2" />
          } else {
            <input type="radio" disabled="disabled" name="ability" value="2" />
          }
        }
        </td><td>HEAL:{ a.variationBetween(a.baseHealValue) }</td></tr>
        <tr><td>
        {
          if (Bypass.activeAnimal == a) {
            <input type="radio" name="ability" value="3" />
          } else {
            <input type="radio" disabled="disabled" name="ability" value="3" />
          }
        }
        </td><td>DMG:{ a.variationBetween(a.baseAttackValue * 2) }<br/>SELF DMG:{ a.variationBetween(a.baseAttackValue / 2) }</td></tr>
      </table>
    </div>

  def battlefield =
    <div>
      <br/><br/>
      <strong>{ Bypass.battlefieldText }</strong>
      <br/><br/>
      <input type="submit" value={if (Bypass.restartButton) "Restart!" else "Attack!"} />
      <br/><br/>
    </div>

  def initRows(name: String, key: String) =
    <table align="center">
      <tr><td>{name} Name: </td><td><input type="text" name={"player" + key}/></td></tr>
      <tr><td></td><td></td></tr>
      {
        for (i <- 1 to Bypass.model.gameSize)
          yield <tr><td>Animal {i}:</td><td>
                <input type="text" name={"animal" + key + i}/></td></tr>
      }
    </table>

  def initFrame =
    <html>
      { style }
      <body>
        <h1>Say hello to <i>scalmon web</i>!</h1>
        <form action="initAction" method="post">
          {initRows("Your", "A")}
          <br /><br />
          {initRows("KI", "B")}
          <input type="submit" value="Play!" />
        </form>
      </body>
    </html>

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
