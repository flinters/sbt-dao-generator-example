package sbtdaogen.example.domain

import java.util.Calendar

import sbtdaogen.example.infrastructure.CrudMapper

case class EmpRepositoryOnJDBC() extends AbstractRepositoryOnJDBC[EmpId, Emp, sbtdaogen.example.infrastructure.EmpId, sbtdaogen.example.infrastructure.Emp] {

  override protected val mapper: CrudMapper[sbtdaogen.example.infrastructure.EmpId, sbtdaogen.example.infrastructure.Emp] =
    sbtdaogen.example.infrastructure.Emp

  override protected def convertToId(identifier: EmpId): sbtdaogen.example.infrastructure.EmpId =
    sbtdaogen.example.infrastructure.EmpId(identifier.value)

  override protected def convertToEntity(record: sbtdaogen.example.infrastructure.Emp): Emp = {
    Emp(
      identifier = EmpId(record.id.value),
      deptId = DeptId(record.deptId.value),
      empName = record.empName,
      hiredate = record.hiredate,
      salary = record.salary,
      versionNo = record.versionNo
    )
  }

  override protected def convertToRecord(entity: Emp): sbtdaogen.example.infrastructure.Emp = {
    sbtdaogen.example.infrastructure.Emp(
      id = convertToId(entity.identifier),
      deptId = sbtdaogen.example.infrastructure.DeptId(entity.deptId.value),
      empName = entity.empName,
      hiredate = entity.hiredate.map{ e =>
        val cal = Calendar.getInstance()
        cal.setTime(e)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        new java.sql.Date(cal.getTimeInMillis)
      },
      salary = entity.salary,
      versionNo = entity.versionNo
    )
  }

}
