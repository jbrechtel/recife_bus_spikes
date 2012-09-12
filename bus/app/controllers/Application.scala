package controllers

import play.api._
import play.api.db._
import play.api.mvc._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import recifeBuses._

object Application extends Controller {

  def index = Action {

    val routes = PageScraper.parse
    routes.foreach{ route =>
      val conn = DB.withConnection { implicit connection =>
        SQL("""INSERT INTO Routes (id, name, nomeItinerario)
          VALUES({id}, {name}, {nomeItinerario})
          """).on( 'id -> route.id, 'name -> route.name, 'nomeItinerario -> route.nomeItinerario).executeUpdate()
          }
    }


    Ok(views.html.index(routes))
  }
}
