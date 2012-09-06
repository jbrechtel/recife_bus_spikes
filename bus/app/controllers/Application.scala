package controllers

import play.api._
import play.api.mvc._
import recifeBuses._

object Application extends Controller {

  def index = Action {
    val routes = PageScraper.parse
    Ok(views.html.index(routes))
  }
}
