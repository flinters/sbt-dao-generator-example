package sbtdaogen.example.domain

import sbtdaogen.example.infrastructure.CrudMapper

case class DeptRepositoryOnJDBC() extends AbstractRepositoryOnJDBC[DeptId, Dept, sbtdaogen.example.infrastructure.DeptId, sbtdaogen.example.infrastructure.Dept] {

  override protected val mapper: CrudMapper[sbtdaogen.example.infrastructure.DeptId, sbtdaogen.example.infrastructure.Dept] =
    sbtdaogen.example.infrastructure.Dept

  override protected def convertToId(identifier: DeptId): sbtdaogen.example.infrastructure.DeptId =
    sbtdaogen.example.infrastructure.DeptId(identifier.value)

  override protected def convertToEntity(record: sbtdaogen.example.infrastructure.Dept): Dept = {
    Dept(
      identifier = DeptId(record.id.value),
      deptName = record.deptName,
      versionNo = record.versionNo
    )
  }

  override protected def convertToRecord(entity: Dept): sbtdaogen.example.infrastructure.Dept = {
    sbtdaogen.example.infrastructure.Dept(
      id = convertToId(entity.identifier),
      deptName = entity.deptName,
      versionNo = entity.versionNo
    )
  }

}
