package controllers

import play.api._
import play.api.db._
import play.api.mvc._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import recifeBuses._
import models._

object Application extends Controller {

  def index = Action {

//  val routes = PageScraper.parse
//  routes.foreach{ route =>
//    val conn = DB.withConnection { implicit connection =>
//      SQL("""INSERT INTO Routes (externalRouteId, name, nomeItinerario)
//        VALUES({externalRouteId}, {name}, {nomeItinerario})
//        """).on( 'externalRouteId -> route.externalRouteId, 'name -> route.name, 'nomeItinerario -> route.nomeItinerario).executeUpdate()
//        }
//  }

    val routes = DB.withConnection { implicit connection =>
        val routesSelect = SQL("SELECT * FROM Routes")
        routesSelect().map( row =>
          new Route(row[String]("name"), row[Int]("externalRouteId").toString, row[String]("nomeItinerario"))
        ).toList
    }

    Ok(views.html.index(routes))
  }
}
