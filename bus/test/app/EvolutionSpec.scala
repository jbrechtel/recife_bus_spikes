import org.specs2.mutable._
import play.api.db.DB
import play.api.Play.current
import play.api.test._
import play.api.test.Helpers._

import anorm._

class DBEvolutionsTest extends Specification {

  "Evolutions" should {
    "be applied without errors" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        DB.withConnection {
          implicit connection =>
            SQL("select count(1) from routes").execute()
            SQL("select count(1) from stops").execute()
        }
      }
      success
    }
  }
}
