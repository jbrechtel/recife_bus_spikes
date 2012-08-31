package recifeBuses

import scala.util.matching.Regex
import java.io.File
import java.io.FileInputStream
import java.net._
import org.htmlcleaner._

object RoutesScraper {
  def getRoutes() = {
    val selectNode = getSelectNode()
    parseRoutesFromSelect(selectNode)
  }

  def getRoutes(text: String) = {
    val selectNode = getSelectNode(text)
    parseRoutesFromSelect(selectNode)
  }

  def getSelectNode(): TagNode = {
    val url = new URL("http://200.238.84.28/site/consulta/itinerarios_parada_linhas.asp")
    val input = new FileInputStream("sample_pages/main.html")
    val text = io.Source.fromInputStream(input).mkString
    getSelectNode(text)
  }

  def getSelectNode(text: String): TagNode = {
    var props = new CleanerProperties
    var cleaner = new HtmlCleaner(props)
    var node = cleaner.clean(text)
    var selectNodes = node.getElementsByAttValue("name", "SelLinhas", true, true)
    selectNodes(0)
  }

  def parseRoutesFromSelect(selectNode: TagNode ): Seq[Route] = {
    val options = selectNode.getElementsByName("option", true)

    options.filter { option =>
      option.getAttributeByName("value") != "0"
    }.map { option =>
      val routeName = option.getText().toString().trim()
      val routeId = option.getAttributeByName("value")
      new Route(routeName, routeId, "")
    }
  }

  def parseRoutesFromJavascript(root: TagNode): Seq[Route] = {
    val lines = root.getText.toString.split(";").map(_.trim)

    val nomeItinerarios = getNomeItinerariosFrom(lines)
    val routeIds = getRouteIdsFrom(lines)

    routeIds.zip(nomeItinerarios).map { idAndNome =>
      new Route("", idAndNome._2, idAndNome._1)
    }
  }

  def getNomeItinerariosFrom(lines: Seq[String]): Seq[String] = {
    val ItinerarioPattern = """.*arrayitinerario\[0\]\[0\] = \'(.+)\'.*""".r
    val nomeItinerarios = lines.flatMap { line =>
      line match {
        case ItinerarioPattern(num) => Some(num)
        case _ => None
      }
    }
    return nomeItinerarios
  }

  def getRouteIdsFrom(lines: Seq[String]): Seq[String] = {
    val RouteIdPattern = """.*arrayitinerario\[1\]\[0\] = \'(.+)\'.*""".r
    val routeIds = lines.flatMap(_ match {
      case RouteIdPattern(num) => Some(num)
      case _ => None
    }).toSeq
    return routeIds
  }
}
