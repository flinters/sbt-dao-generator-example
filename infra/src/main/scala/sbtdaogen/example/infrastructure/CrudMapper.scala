package sbtdaogen.example.infrastructure

import skinny.orm.SkinnyCRUDMapperWithId

trait CrudMapper[Id, Entity] extends SkinnyCRUDMapperWithId[Id, Entity] {

  def toNamedValues(record: Entity): Seq[(Symbol, Any)]

}
