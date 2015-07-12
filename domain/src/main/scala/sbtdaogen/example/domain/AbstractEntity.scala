package sbtdaogen.example.domain

import org.sisioh.dddbase.core.model.{Entity, Identifier}

abstract class AbstractEntity[ID <: Identifier[_]]
  extends Entity[ID] {

}
