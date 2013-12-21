package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    val animal = new htwg.scalmon.model.Animal("Pika")
    Ok(views.html.index("Your new application is ready. " + animal))
  }
  
}