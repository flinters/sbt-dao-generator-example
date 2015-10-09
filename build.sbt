import org.seasar.util.lang.StringUtil

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

lazy val domain = (project in file("domain"))
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
    driverClassName in generator := "org.h2.Driver",
    jdbcUrl in generator := "jdbc:h2:file:./db/development",
    jdbcUser in generator := "sa",
    jdbcPassword in generator := "",
    tableNameFilter in generator := { tableName: String => tableName.toUpperCase != "SCHEMA_VERSION" },
    typeNameMapper in generator := {
      case "INTEGER" => "Int"
      case "VARCHAR" => "String"
      case "BOOLEAN" => "Boolean"
      case "DATE" | "TIMESTAMP" => "java.sql.Date"
      case "DECIMAL" => "BigDecimal"
    },
    classNameMapper in generator := { tableName =>
      Seq(StringUtil.camelize(tableName), StringUtil.camelize(tableName) + "Spec")
    },
    templateNameMapper in generator := {
      case modelName if modelName.endsWith("Spec") => "template_spec.ftl"
      case _ => "template.ftl"
    },
    outputDirectoryMapper in generator := {
      case (modelName: String) if modelName.endsWith("Spec") => (sourceManaged in Test).value
      case (modelName: String) => (sourceManaged in Compile).value
    },
    libraryDependencies ++= Seq(
      "org.skinny-framework" %% "skinny-orm" % "1.3.19",
      "org.skinny-framework" %% "skinny-test" % "1.3.19",
      "ch.qos.logback" % "logback-classic" % "1.1.+",
      "org.scalatest" %% "scalatest" % "2.2.4"
    ),
    sourceGenerators in Compile <+= generateAll in generator
  )

lazy val flyway = (project in file("flyway"))
  .settings(name := "flyway")
  .settings(baseSettings: _*)
  .settings(flywaySettings: _*)
  .settings(
    flywayUrl := "jdbc:h2:file:./db/development",
    flywayUser := "sa",
    flywayPassword := ""
  )

lazy val root = (project in file("."))
  .settings(baseSettings: _*).settings(
  name := "sbt-dao-generator-example"
).aggregate(domain, infra)

def projectId(state: State) = extracted(state).currentProject.id

def extracted(state: State) = Project extract state
