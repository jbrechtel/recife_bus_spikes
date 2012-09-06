package models
case class Route(name: String, id: String, nomeItinerario: String, stops: Seq[Stop]) {
  def this(name: String, id: String, nomeItinerario: String) = this(name,id,nomeItinerario,Nil)
}
