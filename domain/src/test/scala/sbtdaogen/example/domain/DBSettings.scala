package sbtdaogen.example.domain

import skinny.{DBSettings => SDBSettings}

trait DBSettings {

  SDBSettings.initialize()

}
