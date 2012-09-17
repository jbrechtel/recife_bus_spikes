package models
case class Route(name: String, externalRouteId: String, nomeItinerario: String, stops: Seq[Stop]) {
  def this(name: String, externalRouteId: String, nomeItinerario: String) = this(name,externalRouteId,nomeItinerario,Nil)
}
