package recifeBuses

object PageScraper {

  def main(args: Array[String]) {
    var routes = RoutesScraper.getRoutes();
    println("Found " + routes.length + " routes")
    var stops = findStopsForRoutes(routes)
      println("Found " + stops.length + " stops")
  }

  def findStopsForRoutes(routes: Seq[Route]): Seq[Seq[Stop]] = {
    routes.map(StopsScraper.getStops(_))
  }
}
