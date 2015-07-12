
lazy val baseSettings = Seq(
  version := "1.0",
  scalaVersion := "2.11.7",
  organization := "jp.co.septeni-original",
  shellPrompt := {
    "sbt (%s)> " format projectId(_)
  },
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := {
    _ => false
  },
  resolvers ++= Seq(
    "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"
  ),
  libraryDependencies ++= Seq(
    "com.h2database" % "h2" % "1.4.+",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  ),
  credentials <<= Def.task {
    val ivyCredentials = (baseDirectory in LocalRootProject).value / ".credentials"
    val result = Credentials(ivyCredentials) :: Nil
    result
  }
)

lazy val generatorSettings = Seq(
  templateDirectory in generator := (baseDirectory in LocalRootProject).value / "templates",
  tableNameFilter in generator := { tableName: String => tableName.toUpperCase != "SCHEMA_VERSION" },
  driverClassName in generator := "org.h2.Driver",
  jdbcUrl in generator := "jdbc:h2:file:./db/development",
  jdbcUser in generator := "sa",
  jdbcPassword in generator := "",
  typeNameMapper in generator := {
    case "INTEGER" => "Int"
    case "VARCHAR" => "String"
    case "BOOLEAN" => "Boolean"
    case "DATE" | "TIMESTAMP" => "java.sql.Date"
    case "DECIMAL" => "BigDecimal"
  }
)

lazy val domain = (project in file("domain"))
  .disablePlugins(SbtDaoGeneratorPlugin)
  .settings(name := "domain")
  .settings(baseSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.sisioh" %% "scala-dddbase-core" % "0.2.7" withSources()
    )
  ).dependsOn(infra)

lazy val infra = (project in file("infra"))
  .enablePlugins(SbtDaoGeneratorPlugin)
  .settings(name := "infra")
  .settings(baseSettings: _*)
  .settings(
    tableNameFilter in generator := { tableName: String => tableName.toUpperCase != "SCHEMA_VERSION" },
    driverClassName in generator := "org.h2.Driver",
    jdbcUrl in generator := "jdbc:h2:file:./db/development",
    jdbcUser in generator := "sa",
    jdbcPassword in generator := "",
    typeNameMapper in generator := {
      case "INTEGER" => "Int"
      case "VARCHAR" => "String"
      case "BOOLEAN" => "Boolean"
      case "DATE" | "TIMESTAMP" => "java.sql.Date"
      case "DECIMAL" => "BigDecimal"
    })
  .settings(flywaySettings: _*)
  .settings(
    flywayUrl := "jdbc:h2:file:./db/development",
    flywayUser := "sa",
    libraryDependencies ++= Seq(
      "com.h2database" % "h2" % "1.4.+",
      "org.skinny-framework" %% "skinny-orm" % "1.3.19",
      "org.skinny-framework" %% "skinny-test" % "1.3.19",
      "ch.qos.logback" % "logback-classic" % "1.1.+"
    )
  )

lazy val root = (project in file(".")).disablePlugins(SbtDaoGeneratorPlugin).settings(baseSettings: _*).settings(
  name := "sbt-dao-generator-example"
).aggregate(domain, infra)

def projectId(state: State) = extracted(state).currentProject.id

def extracted(state: State) = Project extract state
