package recifeBuses

object StopsScraper {
  import java.io.File
  import java.io.FileInputStream
  import org.htmlcleaner._
  import recifeBuses._

  def getStops(route: Route): List[Stop] = {
    var node = getRootTagNode()
    var rows = node.getElementsByName("tr", true)
    rows = rows.filter(r => isRouteStopRow(r))
    RouteStopsParser.parseStopsFromRows(rows)
  }
  
  def getRootTagNode(): TagNode = {
    var props = new CleanerProperties
    var cleaner = new HtmlCleaner(props)
    val file = new File("sample_pages/route.html")
    cleaner.clean(new FileInputStream(file))
  }

  def isRouteStopRow(html: TagNode): Boolean = {
    if(html.getText().indexOf("Visualizar") > 0)
      if(html.getElementsByName("table", true).length == 0)
        return true
    false
  }

}  

