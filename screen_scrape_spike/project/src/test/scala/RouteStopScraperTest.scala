import org.scalatest.FunSuite
import scala.collection.mutable.Stack
import recifeBuses._
import org.htmlcleaner._

class RouteStopScraperSuite extends FunSuite {

  test("parsing routes from javascript tag returns route with id and nomeItinerario") {
    val rootHtml = """<script language="javascript">
    var arrayitinerario = new Array(3);
    var tamanho = 496;
    arrayitinerario[0] = new Array(tamanho);
    arrayitinerario[1] = new Array(tamanho);
    arrayitinerario[2] = new Array(tamanho);
    arrayitinerario[0][0] = '105';
    arrayitinerario[1][0] = '34';
    arrayitinerario[2][0] = 'Principal';
  </script>"""
    val root = new HtmlCleaner(new CleanerProperties).clean(rootHtml)
    val routes = RoutesScraper.parseRoutesFromJavascript(root)
    val route = routes(0)
    assert(route.nomeItinerario === "34")
    assert(route.id === "105")
  }

  test("get nome itinerarios from javascript text split by ';'") {
    val lines = Seq("arrayitinerario[0][0] = '105'", "arrayitinerario[1][0] = '34'", "arrayitinerario[2][0] = 'Principal'")
    val nomeItinerarios = RoutesScraper.getNomeItinerariosFrom(lines)
    assert(nomeItinerarios === Seq("105"))
  }

  test("get route ids from javascript text split by ';'") {
    val lines = Seq("arrayitinerario[0][0] = '105'", "arrayitinerario[1][0] = '34'", "arrayitinerario[2][0] = 'Principal'")
    val routeIds = RoutesScraper.getRouteIdsFrom(lines)
    assert(routeIds === Seq("34"))
  }

  test("gets routes with name and id") {
    val selectHtml = """
  <select name="SelLinhas" onchange="carreganomeitinerario()" style="width:275">
    <option value="0">--Selecione a Linha--</option>
    <option value="940"> Abreu e Lima/Olinda </option>
    <option value="33"> Aeroporto </option>
  </select>
    """
    val root = new HtmlCleaner(new CleanerProperties).clean(selectHtml)
    val routes = RoutesScraper.parseRoutesFromSelect(root)
    val routeNames = routes.map(_.name)
    val routeIds = routes.map(_.id)
    assert(routeNames === Seq("Abreu e Lima/Olinda", "Aeroporto"))
    assert(routeIds === Seq("940", "33"))
  }
}
