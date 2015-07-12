package sbtdaogen.example.domain

import org.scalatest.{Matchers, fixture}
import scalikejdbc.scalatest.AutoRollback

class EmpRepositoryOnJDBCSpec
  extends fixture.FunSpec
  with AutoRollback
  with Matchers
  with DBSettings {

  describe("Emp") {
    it("should find all entities") { implicit session =>
      implicit val ctx = EntityIOContextOnJDBC(session)
      val deptRepository = new DeptRepositoryOnJDBC()
      val empRepository = new EmpRepositoryOnJDBC()
      val dept = Dept(DeptId(1), "KATO", 1)
      val emp = Emp(EmpId(1), dept.identifier, "KATO", None, None, 1)
      val entityAsTry = deptRepository.store(dept).flatMap { result =>
        deptRepository.resolveBy(dept.identifier).flatMap { _ =>
          empRepository.store(emp).flatMap { _ =>
            empRepository.resolveBy(emp.identifier)
          }
        }
      }
      entityAsTry.get
      assert(entityAsTry.isSuccess)
      assert(entityAsTry.get.identifier == EmpId(1))
    }
  }
}
