package recifeBuses

import models._

object PageScraper {

  def parse = {
    val routes = RoutesScraper.getRoutes();
    findStopsForRoutes(routes)
  }

  def findStopsForRoutes(routes: Seq[Route]): Seq[Route] = {
    routes.par.map(route => route.copy(stops = StopsScraper.getStops(route))).seq
  }

}
