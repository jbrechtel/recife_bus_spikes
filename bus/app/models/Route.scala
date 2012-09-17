package models

import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class Route(name: String, externalRouteId: String, nomeItinerario: String, stops: Seq[Stop]) {
  def this(name: String, externalRouteId: String, nomeItinerario: String) = this(name,externalRouteId,nomeItinerario,Nil)
}

object Route {
  def insert(route: Route) = {
    DB.withConnection { implicit c =>
      SQL("""
        INSERT INTO Routes (externalRouteId, nomeItinerario, name)
        VALUES ({externalRouteId}, {nomeItinerario}, {name})
      """).on(
          'externalRouteId -> route.externalRouteId,
          'nomeItinerario  -> route.nomeItinerario,
          'name            -> route.name
        ).executeUpdate()
    }
  }

  def find(externalRouteId: String = "", nomeItinerario: String = ""): Option[Route] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM Routes").on().as(Route.simple.singleOpt)
    }
  }

  def simple = {
    get[String]("externalRouteId") ~
    get[String]("nomeItinerario") ~
    get[String]("name") map {
      case externalRouteId~nomeItinerario~name => new Route(name,externalRouteId,nomeItinerario)
    }
  }
}
