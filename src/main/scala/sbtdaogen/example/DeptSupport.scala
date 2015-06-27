package sbtdaogen.example

import skinny.orm.SkinnyCRUDMapper

trait DeptSupport {
  this: SkinnyCRUDMapper[Dept] =>

}
