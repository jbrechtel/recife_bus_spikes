package recifeBuses

object RouteStopsParser {
  import org.htmlcleaner._
  import models._

  def parseStopsFromRows(rows: Array[TagNode]): List[Stop] = {
    var stops = List[Stop]()
    for(row <- rows) {
      stops = parseStopFromRow(row) :: stops
    }
    stops
  }

  def parseStopFromRow(row: TagNode): Stop = {
    var dataElements = row.getElementsByName("td", true)
    var codigo = dataElements(0).getText().toString()
    var bairro = dataElements(1).getText().toString()
    var logradouro = dataElements(2).getText().toString()
    var referencia = dataElements(3).getText().toString()
    var latandlongAnchors = dataElements(4).getElementsByName("a", true)
    var latandlong = latandlongAnchors(0).getAttributeByName("href")
    var latitude = latandlong.split("'")(1)
    var longitude = latandlong.split("'")(3)
    new Stop(codigo, bairro, logradouro, referencia, latitude, longitude)
  }
}
