package sbtdaogen.example.domain

import org.sisioh.dddbase.core.model.Identifier

case class DeptId(value: Int) extends Identifier[Int]

case class Dept(identifier: DeptId, deptName: String, versionNo: Int)
  extends AbstractEntity[DeptId]
