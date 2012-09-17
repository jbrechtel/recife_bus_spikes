import org.scalatest.FunSuite
import scala.collection.mutable.Stack
import recifeBuses._
import org.htmlcleaner._

class RouteStopScraperSuite extends FunSuite {
  val rootHtml = """<html><body><p>Some gibberish<p>
  <select name="SelLinhas" onchange="carreganomeitinerario()" style="width:275">
    <option value="0">--Selecione a Linha--</option>
    <option value="940"> Abreu e Lima/Olinda </option>
    <option value="105"> Aeroporto </option>
  </select>
   <script language="javascript"></script>
    <script language="javascript">
    var arrayitinerario = new Array(3);
    var tamanho = 496;
    arrayitinerario[0] = new Array(tamanho);
    arrayitinerario[1] = new Array(tamanho);
    arrayitinerario[2] = new Array(tamanho);
    arrayitinerario[0][0] = '105';
    arrayitinerario[1][0] = '34';
    arrayitinerario[2][0] = 'Principal';
  </script></body></html>"""

  test("parsing routes from javascript tag returns route with id and nomeItinerario") {
    val root = new HtmlCleaner(new CleanerProperties).clean(rootHtml)
    val routes = RoutesScraper.parseRoutesFromJavascript(root)
    val route = routes(0)
    val (id, nomeItinerario) = route
    assert(nomeItinerario === "34")
    assert(id === "105")
  }

  test("get nome itinerarios from javascript text split by ';'") {
    val lines = Seq("arrayitinerario[0][0] = '105'", "arrayitinerario[1][0] = '34'", "arrayitinerario[2][0] = 'Principal'")
    val nomeItinerarios = RoutesScraper.getNomeItinerariosFrom(lines)
    assert(nomeItinerarios === Seq("34"))
  }

  test("get route ids from javascript text split by ';'") {
    val lines = Seq("arrayitinerario[0][0] = '105'", "arrayitinerario[1][0] = '34'", "arrayitinerario[2][0] = 'Principal'")
    val routeIds = RoutesScraper.getRouteIdsFrom(lines)
    assert(routeIds === Seq("105"))
  }

  test("gets routes with name and id") {
    val selectHtml = """
  <select name="SelLinhas" onchange="carreganomeitinerario()" style="width:275">
    <option value="0">--Selecione a Linha--</option>
    <option value="940"> Abreu e Lima/Olinda </option>
    <option value="34"> Aeroporto </option>
  </select>
    """
    val root = new HtmlCleaner(new CleanerProperties).clean(selectHtml)
    val routes = RoutesScraper.parseRoutesFromSelect(root)
    val routeNames = routes.map{case (id, name) => name}
  val routeIds = routes.map{case (id, name) => id}
    assert(routeNames === Seq("Abreu e Lima/Olinda", "Aeroporto"))
    assert(routeIds === Seq("940", "34"))
  }
  
  test("get script node") {
    val scriptNode = RoutesScraper.getScriptNode(rootHtml)
    assert(scriptNode != None)
  }

  test("get only the script node with ArrayItinerario") {
    val scriptNode = RoutesScraper.getScriptNode(rootHtml).get.getText.toString
    val scriptNodeMatcher = """.*arrayitinerario.*""".r
    assert(scriptNodeMatcher.findFirstIn(scriptNode).isDefined)
  }

  test("getting list of routes from getRoutes") {
    val routes = RoutesScraper.getRoutes(rootHtml)
    assert(routes(0).externalRouteId === "105")
    assert(routes(0).nomeItinerario === "34")
    assert(routes(0).name === "Aeroporto")
  }

  test("get routes from file") {
    assert(RoutesScraper.getRoutes().length === 484)
  }
}
