organization := "com.audienceproject.sbt"
organizationName := "Audience Project"
name := "sbt-release"
description := "A sbt plugin for releasing a Scala library"

enablePlugins(SbtPlugin, ReleasePlugin)

ThisBuild / scalaVersion := "2.12.15"
ThisBuild / versionScheme := Some("early-semver")

scalacOptions := Seq("-Xsource:2.12", "-encoding", "UTF-8", "-deprecation", "-unchecked")

publishMavenStyle := true
pomIncludeRepository := { _ => false }
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
pomExtra := <url>https://github.com/audienceproject/sbt-release</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>https://opensource.org/licenses/MIT</url>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:audienceproject/sbt-release.git</url>
    <connection>scm:git:git//github.com/audienceproject/sbt-release.git</connection>
    <developerConnection>scm:git:ssh://github.com:audienceproject/sbt-release.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>steffen</id>
      <name>Steffen Bach</name>
      <email>steffen@audienceproject.com</email>
      <organization>AudienceProject</organization>
      <organizationUrl>https://www.audienceproject.com</organizationUrl>
    </developer>
  </developers>