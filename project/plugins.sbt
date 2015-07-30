logLevel := Level.Warn

resolvers ++= Seq(
  Classpaths.typesafeReleases,
  Classpaths.typesafeSnapshots,
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/",
  "Flyway" at "http://flywaydb.org/repo"
)

libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.+"
)

addSbtPlugin("org.flywaydb" % "flyway-sbt" % "3.2.1")

addSbtPlugin("jp.co.septeni-original" % "sbt-dao-generator" % "1.0.0-SNAPSHOT")