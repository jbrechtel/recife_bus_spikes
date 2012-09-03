package recifeBuses

import scala.util.matching.Regex
import java.io.File
import java.io.FileInputStream
import java.net._
import org.htmlcleaner._

object RoutesScraper {
  def getRoutes(): Seq[Route] = {
    val selectNode = getSelectNode()
    parseRoutesFromSelect(selectNode)
    Seq(new Route("","",""))
  }

  def getRoutes(text: String): Seq[Route] = {
    val selectNode = getSelectNode(text)
    val routeIdNameTuple = parseRoutesFromSelect(selectNode)
    val scriptNode = getScriptNode(text)

    val routeIdNomeItinerarioTuple = scriptNode match {
      case Some(node) => parseRoutesFromJavascript(node)
      case None       => Nil
    }

    routeIdNomeItinerarioTuple.flatMap({case (id, nomeItinerario) =>
      routeIdNameTuple.find(_._1 == id).map { case (_, name) =>
        new Route(name, id, nomeItinerario)
      }
    })
  }

  def getSelectNode(): TagNode = {
    val url = new URL("http://200.238.84.28/site/consulta/itinerarios_parada_linhas.asp")
    val input = new FileInputStream("sample_pages/main.html")
    val text = io.Source.fromInputStream(input).mkString
    getSelectNode(text)
  }

  def getSelectNode(text: String): TagNode = {
    val props = new CleanerProperties
    val cleaner = new HtmlCleaner(props)
    val node = cleaner.clean(text)
    val selectNodes = node.getElementsByAttValue("name", "SelLinhas", true, true)
    selectNodes(0)
  }

  def getScriptNode(text: String): Option[TagNode] = {
    val props = new CleanerProperties
    val cleaner = new HtmlCleaner(props)
    val node = cleaner.clean(text)
    val scriptNodeMatcher = """.*arrayitinerario = new Array.*""".r
    val scriptNodes = node.getElementsByName("script", true).filter({ node =>
    scriptNodeMatcher.findFirstIn(node.getText.toString).isDefined
  })
    scriptNodes.headOption
  }

  def parseRoutesFromSelect(selectNode: TagNode ): Seq[(String, String)] = {
    val options = selectNode.getElementsByName("option", true)

    options.filter { option =>
      option.getAttributeByName("value") != "0"
    }.map { option =>
      val routeName = option.getText().toString().trim()
      val routeId = option.getAttributeByName("value")
      (routeId, routeName)
    }
  }

  def parseRoutesFromJavascript(root: TagNode): Seq[(String, String)] = {
    val lines = root.getText.toString.split(";").map(_.trim)

    val nomeItinerarios = getNomeItinerariosFrom(lines)
    val routeIds = getRouteIdsFrom(lines)

    routeIds.zip(nomeItinerarios)
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
