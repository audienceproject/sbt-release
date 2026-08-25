ThisBuild / organizationName := "AudienceProject"
ThisBuild / organizationHomepage := Some(url("https://audienceproject.com//"))
ThisBuild / homepage := Some(url("https://github.com/audienceproject/crossbow"))

ThisBuild / Test / publishArtifact := false

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/audienceproject/sbt-release"),
    "scm:git@github.com/audienceproject/sbt-release.git",
    Option("scm:git:ssh://github.com:audienceproject/sbt-release.git")
  )
)
ThisBuild / developers := List(
  Developer(
    id = "SteffenBach",
    name = "Steffen Bach",
    email = "steffen@audienceproject.com",
    url = url("https://www.audienceproject.com"),
  ),
  Developer(
    id = "jacobfi",
    name = "Jacob Fischer",
    email = "jacob.fischer@audienceproject.com",
    url = url("https://www.audienceproject.com"),
  )
)

ThisBuild / licenses := List(
  "MIT" -> url("https://mit-license.org/")
)

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true

// new setting for the Central Portal
ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}