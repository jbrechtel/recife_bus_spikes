import org.scalatest.FunSuite
import scala.collection.mutable.Stack
import recifeBuses._
import org.htmlcleaner._
import models._

class StopsScraperSuite extends FunSuite {
  test("parsing stops") {
    val route = new Route("","","")
    val stops = StopsScraper.getStops(route)
    assert(stops.length > 0)
    val firstStop = stops(0)
    assert(firstStop.codigo === "180056")
    assert(firstStop.bairro === "Cabanga")
    assert(firstStop.logradouro.trim === "Avenida Engenheiro José Estelita")
    assert(firstStop.referencia === "SEGUNDA BAIA AO LADO DO CAIS.")
    assert(firstStop.latitude === "-8 4 28.53")
    assert(firstStop.longitude === "-34 53 2.76")
  }
}
