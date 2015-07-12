package sbtdaogen.example.domain

import org.sisioh.dddbase.core.lifecycle.EntityNotFoundException
import org.sisioh.dddbase.core.lifecycle.sync.{SyncRepository, SyncResultWithEntity}
import org.sisioh.dddbase.core.model.Identifier
import sbtdaogen.example.infrastructure.{CrudMapper, Record, TableId}

import scala.reflect.runtime.{universe => ru}
import scala.util.{Failure, Success, Try}

abstract class AbstractRepositoryOnJDBC
[ID <: Identifier[_], E <: AbstractEntity[ID], TID <: TableId, R <: Record]
  extends SyncRepository[ID, E] {

  override type This = this.type

  protected val mapper: CrudMapper[TID, R]

  private def getEntityIOContext(ctx: Ctx) = {
    ctx match {
      case EntityIOContextOnJDBC(session) => session
      case _ => throw new IllegalArgumentException
    }
  }

  def getType[A : ru.TypeTag](clazz: Class[A]): ru.Type =
    ru.typeTag[A].tpe

  protected def convertToId(identifier: ID): TID

  protected def convertToEntity(r: R): E

  protected def convertToRecord(entity: E): R

  override def existBy(identifier: ID)(implicit ctx: Ctx): Try[Boolean] = {
    implicit val dbSession = getEntityIOContext(ctx)
    resolveBy(identifier).map(_ => true).recover {
      case ex: EntityNotFoundException => false
    }
  }

  override def resolveBy(identifier: ID)(implicit ctx: Ctx): Try[E] = {
    implicit val dbSession = getEntityIOContext(ctx)
    mapper.findById(convertToId(identifier))
      .map(record => Success(convertToEntity(record)))
      .getOrElse(Failure(new EntityNotFoundException(Some(s"identifier = $identifier"))))
  }

  override def store(entity: E)(implicit ctx: Ctx): Try[SyncResultWithEntity[This, ID, E]] = {
    implicit val dbSession = getEntityIOContext(ctx)
    try {
      val result = mapper.updateById(convertToId(entity.identifier)).withAttributes(mapper.toNamedValues(convertToRecord(entity)): _*)
      if (result == 0) {
        mapper.createWithAttributes(mapper.toNamedValues(convertToRecord(entity)): _*)
      }
      resolveBy(entity.identifier).map { entity =>
        SyncResultWithEntity(this.asInstanceOf[This], entity)
      }
    } catch {
      case ex: Exception =>
        Failure(ex)
    }

  }

  override def deleteBy(identifier: ID)(implicit ctx: Ctx): Try[SyncResultWithEntity[This, ID, E]] = {
    resolveBy(identifier).map{ entity =>
      mapper.deleteById(convertToId(entity.identifier))
      SyncResultWithEntity(this.asInstanceOf[This], entity)
    }
  }

}
