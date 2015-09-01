package sbtdaogen.example.domain

import org.scalatest.{Matchers, fixture}
import scalikejdbc.scalatest.AutoRollback

class SaleRepositoryOnJDBCSpec
  extends fixture.FunSpec
  with AutoRollback
  with Matchers
  with DBSettings {

  describe("Sale") {
    it("should find all entities") { implicit session =>
      implicit val ctx = EntityIOContextOnJDBC(session)
      val saleRepository = new SaleRepositoryOnJDBC()
      val saleId = SaleId(1)
      val sale = Sale(
        identifier = saleId,
        saleDetails = Seq(
          SaleDetail(
            identifier = SaleDetailId(1),
            saleId = saleId,
            productName = "ABC",
            price = 101,
            versionNo = 0
          ),
          SaleDetail(
            identifier = SaleDetailId(2),
            saleId = saleId,
            productName = "DEF",
            price = 102,
            versionNo = 0
          )
        ),
        versionNo = 0
      )
      val entityAsTry = saleRepository.store(sale).flatMap { result =>
        saleRepository.resolveBy(sale.identifier).flatMap { result =>
          val newSale = result.copy(
            saleDetails = Seq(
              SaleDetail(
                identifier = SaleDetailId(1),
                saleId = saleId,
                productName = "ABC",
                price = 101,
                versionNo = 0
              )
            )
          )
          saleRepository.store(newSale).map(_.entity)
        }
      }
      entityAsTry.get
      assert(entityAsTry.isSuccess)
      assert(entityAsTry.get.identifier == SaleId(1))
    }
  }
}
