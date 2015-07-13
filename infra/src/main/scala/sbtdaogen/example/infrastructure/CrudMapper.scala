package sbtdaogen.example.infrastructure

import skinny.orm.SkinnyCRUDMapperWithId

trait CrudMapper[Id, Entity] extends SkinnyCRUDMapperWithId[Id, Entity] {

  override def useAutoIncrementPrimaryKey: Boolean = false

  override def useExternalIdGenerator: Boolean = true

  def toNamedValues(record: Entity): Seq[(Symbol, Any)]

}
