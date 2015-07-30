package sbtdaogen.example.domain

import org.scalatest.{Matchers, fixture}
import scalikejdbc.DBSession
import scalikejdbc.scalatest.AutoRollback

class DeptRepositoryOnJDBCSpec
  extends fixture.FunSpec
  with AutoRollback
  with Matchers
  with DBSettings {

  override def fixture(implicit session: DBSession) {
    // do fixtures stuff
  }

  describe("Dept") {
    it("should find all entities") { implicit session =>
      implicit val ctx = EntityIOContextOnJDBC(session)
      val repository = new DeptRepositoryOnJDBC()
      val dept = Dept(DeptId(2), "KATO", 1)
      val entityAsTry = repository.store(dept).flatMap{ result =>
        repository.resolveBy(dept.identifier)
      }
      entityAsTry.get
      assert(entityAsTry.isSuccess)
      assert(entityAsTry.get.identifier == DeptId(2))
    }
  }

}
