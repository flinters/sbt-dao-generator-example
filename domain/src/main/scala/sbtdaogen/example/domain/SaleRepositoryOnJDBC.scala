package sbtdaogen.example.domain

import org.sisioh.dddbase.core.lifecycle.EntityNotFoundException
import org.sisioh.dddbase.core.lifecycle.sync.{SyncRepository, SyncResultWithEntity}
import scalikejdbc._

import scala.util.{Failure, Try}

case class SaleRepositoryOnJDBC() extends SyncRepository[SaleId, Sale] {

  override type This = this.type

  private val saleMapper = sbtdaogen.example.infrastructure.Sale

  private val saleDetailMapper = sbtdaogen.example.infrastructure.SaleDetail

  private def getEntityIOContext(ctx: Ctx) = {
    ctx match {
      case EntityIOContextOnJDBC(session) => session
      case _ => throw new IllegalArgumentException
    }
  }

  private def convertToSaleId(identifier: SaleId): sbtdaogen.example.infrastructure.SaleId =
    sbtdaogen.example.infrastructure.SaleId(identifier.value)

  private def convertToSale(record: sbtdaogen.example.infrastructure.Sale, sales: Seq[sbtdaogen.example.infrastructure.SaleDetail]): Sale =
    Sale(
      identifier = SaleId(record.id.value),
      saleDetails = sales.map(e => convertToSaleDetail(record.id, e)),
      versionNo = record.versionNo
    )

  private def convertToSaleDetail(saleId: sbtdaogen.example.infrastructure.SaleId, record: sbtdaogen.example.infrastructure.SaleDetail): SaleDetail =
    SaleDetail(
      identifier = SaleDetailId(record.id.value),
      saleId = SaleId(saleId.value),
      productName = record.productName,
      price = record.price,
      versionNo = record.versionNo
    )

  private def convertToSaleRecord(entity: Sale): sbtdaogen.example.infrastructure.Sale =
    sbtdaogen.example.infrastructure.Sale(
      id = sbtdaogen.example.infrastructure.SaleId(entity.identifier.value),
      versionNo = entity.versionNo
    )

  private def convertToSaleDetailRecord(saleId: SaleId, entity: SaleDetail): sbtdaogen.example.infrastructure.SaleDetail =
    sbtdaogen.example.infrastructure.SaleDetail(
      id = sbtdaogen.example.infrastructure.SaleDetailId(entity.identifier.value),
      saleId = sbtdaogen.example.infrastructure.SaleId(saleId.value),
      productName = entity.productName,
      price = entity.price,
      versionNo = entity.versionNo
    )

  override def store(entity: Sale)(implicit ctx: Ctx): Try[SyncResultWithEntity[This, SaleId, Sale]] = {
    implicit val dbSession = getEntityIOContext(ctx)
    try {
      val result = saleMapper.updateById(convertToSaleId(entity.identifier)).withAttributes(saleMapper.toNamedValues(convertToSaleRecord(entity)): _*)
      if (result == 0) {
        saleMapper.createWithAttributes(saleMapper.toNamedValues(convertToSaleRecord(entity)): _*)
      }
      entity.saleDetails.foreach{ detail =>
        val result = saleDetailMapper.updateById(sbtdaogen.example.infrastructure.SaleDetailId(detail.identifier.value))
          .withAttributes(saleDetailMapper.toNamedValues(convertToSaleDetailRecord(SaleId(entity.identifier.value), detail)): _*)
        if (result == 0) {
          saleDetailMapper.createWithAttributes(saleDetailMapper.toNamedValues(convertToSaleDetailRecord(SaleId(entity.identifier.value), detail)): _*)
        }
      }
      resolveBy(entity.identifier).map { entity =>
        SyncResultWithEntity(this.asInstanceOf[This], entity)
      }
    } catch {
      case ex: Exception =>
        Failure(ex)
    }
  }

  override def deleteBy(identifier: SaleId)(implicit ctx: Ctx): Try[SyncResultWithEntity[This, SaleId, Sale]] = {
    val alias = saleDetailMapper.defaultAlias
    resolveBy(identifier).map { entity =>
      saleMapper.deleteById(convertToSaleId(entity.identifier))
      saleDetailMapper.deleteBy(sqls.eq(alias.saleId, convertToSaleId(identifier)))
      SyncResultWithEntity(this.asInstanceOf[This], entity)
    }
  }

  override def existBy(identifier: SaleId)(implicit ctx: Ctx): Try[Boolean] = {
    implicit val dbSession = getEntityIOContext(ctx)
    resolveBy(identifier).map(_ => true).recover {
      case ex: EntityNotFoundException => false
    }
  }

  override def resolveBy(identifier: SaleId)(implicit ctx: Ctx): Try[Sale] = Try {
    implicit val dbSession = getEntityIOContext(ctx)
    val sale = saleMapper.findById(convertToSaleId(identifier)).getOrElse(throw new EntityNotFoundException(Some(s"identifier = $identifier")))
    val alias = saleDetailMapper.defaultAlias
    val saleDetails = saleDetailMapper.findAllBy(sqls.eq(alias.saleId, identifier.value))
    convertToSale(sale, saleDetails)
  }

}
