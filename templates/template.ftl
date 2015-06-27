package sbtdaogen.example

import scalikejdbc._
import skinny.orm._

case class ${name}Id(value: Long) {
    override def toString = value.toString
}

object ${name}Id {
    implicit val typeBinder: TypeBinder[${name}Id] = TypeBinder.long.map(${name}Id.apply)
}

case class ${name}(<#list columns as column><#if column.name?ends_with("Id")>${column.name}: ${name}Id<#if column_has_next>,</#if><#else><#if column.nullable>${column.name}: Option[${column.typeName}]<#if column_has_next>,</#if><#else>${column.name}: ${column.typeName}<#if column_has_next>,</#if></#if></#if></#list>)

object ${name} extends SkinnyCRUDMapperWithId[${name}Id, ${name}] with ${name}Support {

    override def defaultAlias: Alias[${name}] = createAlias("${name[0]?lower_case}")

    override def extract(rs: WrappedResultSet, s: ResultName[${name}]): ${name} = autoConstruct(rs, s)

}

