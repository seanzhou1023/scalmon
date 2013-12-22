package htwg.scalmon.view.wui

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
  def receive = {
    runRoute(myRoute)
  }
}

object Single {
  var count = 1
  def animal(x: String): String = {
    count += 1
    return x + " " + count
  }
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>scalmon web</i>!</h1>
              </body>
            </html>
          }
        }
      }
    } ~
    path("animal") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Say hello to <i>scalmon web</i>!</h1>
                <p>{Single.animal("X")} welcomes you.</p>
              </body>
            </html>
          }
        }
      }
    }
}
