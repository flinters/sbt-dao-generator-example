package sbtdaogen.example.infrastructure

import scalikejdbc._
import skinny.orm._

case class ${name}Id(value: Int) extends TableId {
    override def toString = value.toString
}

object ${name}Id {
    implicit val typeBinder: TypeBinder[${name}Id] = TypeBinder.int.map(${name}Id.apply)
}

case class ${name}(<#list primaryKeys as primaryKey>${primaryKey.name}: ${name  }${primaryKey.camelizeName}<#if primaryKey_has_next>,</#if></#list>,<#list columns as column><#if column.name?ends_with("Id")>${column.name}: ${column.camelizeName}<#if column_has_next>,</#if><#else><#if column.nullable>${column.name}: Option[${column.typeName}]<#if column_has_next>,</#if><#else>${column.name}: ${column.typeName}<#if column_has_next>,</#if></#if></#if></#list>)
    extends Record

object ${name} extends CrudMapper[${name}Id, ${name}] with ${name}Support {

    override def defaultAlias: Alias[${name}] = createAlias("${name[0]?lower_case}")

    def idToRawValue(id: ${name}Id) = id.value

    def rawValueToId(value: Any) = ${name}Id(value.toString.toInt)

    override def extract(rs: WrappedResultSet, s: ResultName[${name}]): ${name} = autoConstruct(rs, s)

    override def toNamedValues(record: ${name}): Seq[(Symbol, Any)] = Seq(
<#list columns as column>       '${column.name} -> record.${column.name}<#if column.name?ends_with("id") || column.name?ends_with("Id")>.value</#if><#if column_has_next>,</#if>
</#list>
    )

}

