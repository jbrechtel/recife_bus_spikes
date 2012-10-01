package recifeBuses

object StopsScraper {
  import java.io.File
  import java.io.FileInputStream
  import org.htmlcleaner._
  import recifeBuses._
  import models._

  // An example url for stops.  The linha and nomeItinerario are available on the Route object
  // http://200.238.84.28/site/consulta/itinerarios_parada_linhas.asp?linha=36&nomeitinerario=5673

  def getStops(route: Route): List[Stop] = {
    val rows = getRootTagNode(route).getElementsByName("tr", true)
    RouteStopsParser.parseStopsFromRows(rows.filter(r => isRouteStopRow(r)))
  }

  def getRootTagNode(route: Route): TagNode = {
    var props = new CleanerProperties
    var cleaner = new HtmlCleaner(props)
    val file = new File("app/data/sample_pages/route.html")
    cleaner.clean(new FileInputStream(file))
  }

  def isRouteStopRow(html: TagNode): Boolean = {
    if(html.getText().indexOf("Visualizar") > 0)
      if(html.getElementsByName("table", true).length == 0)
        return true
    false
  }
}

