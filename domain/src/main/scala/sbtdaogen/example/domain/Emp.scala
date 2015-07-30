package sbtdaogen.example.domain

import java.util.Date

import org.sisioh.dddbase.core.model.Identifier

case class EmpId(value: Int) extends Identifier[Int]

case class Emp(identifier: EmpId, deptId: DeptId, empName: String, hiredate: Option[Date], salary: Option[BigDecimal], versionNo: Int)
  extends AbstractEntity[EmpId]
