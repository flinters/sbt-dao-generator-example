package sbtdaogen.example

import skinny.orm.SkinnyCRUDMapper

trait EmpSupport {
  this: SkinnyCRUDMapper[Emp] =>

}