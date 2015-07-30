package sbtdaogen.example.domain

import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityIOContext
import scalikejdbc.DBSession

case class EntityIOContextOnJDBC(session: DBSession)
  extends SyncEntityIOContext
