name := "sbt-dao-generator-example"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  "org.skinny-framework" %% "skinny-orm"      % "1.3.19",
  "com.h2database"       %  "h2"              % "1.4.+",
  "ch.qos.logback"       %  "logback-classic" % "1.1.+",
  "org.sisioh" %% "scala-dddbase-core" % "0.2.7" withSources()
)

seq(flywaySettings: _*)

flywayUrl := "jdbc:h2:file:./db/development"

flywayUser := "sa"

tableNameFilter in generator := { tableName: String => tableName.toUpperCase != "SCHEMA_VERSION"}

driverClassName in generator := "org.h2.Driver"

jdbcUrl in generator := "jdbc:h2:file:./db/development"

jdbcUser in generator := "sa"

jdbcPassword in generator := ""

typeNameMapper in generator := {
  case "INTEGER" => "Int"
  case "VARCHAR" => "String"
  case "BOOLEAN" => "Boolean"
  case "DATE" | "TIMESTAMP" => "java.sql.Date"
  case "DECIMAL" => "BigDecimal"
}