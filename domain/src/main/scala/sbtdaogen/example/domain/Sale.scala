package sbtdaogen.example.domain

import org.sisioh.dddbase.core.model.Identifier

case class SaleId(value: Int) extends Identifier[Int]

case class Sale(identifier: SaleId, saleDetails: Seq[SaleDetail], versionNo: Int)
  extends AbstractEntity[SaleId]

case class SaleDetailId(value: Int) extends Identifier[Int]

case class SaleDetail(identifier: SaleDetailId, saleId: SaleId, productName: String, price: Int, versionNo: Int)


