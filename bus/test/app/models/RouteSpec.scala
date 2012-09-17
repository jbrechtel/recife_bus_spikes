import org.specs2.mutable._
import play.api.db.DB
import play.api.Play.current
import play.api.test._
import play.api.test.Helpers._

import anorm._

import models._

class RouteSpec extends Specification {

  "Routes" should {
    "be inserted and selected" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val route = new Route("my route", "111", "333")
        Route.insert(route)
        val createdRoute = Route.find(externalRouteId = "111", nomeItinerario = "333")
        createdRoute must beSome(route)
      }
    }
  }
}

