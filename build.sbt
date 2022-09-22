organization := "com.audienceproject.sbt"
organizationName := "Audience Project"
name := "sbt-release"
description := "A sbt plugin for releasing a Scala library"

enablePlugins(SbtPlugin)

ThisBuild / scalaVersion := "2.12.15"

publishMavenStyle := true

scalacOptions := Seq("-Xsource:2.12", "-encoding", "UTF-8", "-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % "5.13.1.202206130422-r",
)

resolvers in Global ++= Seq(
  "AudienceReport Snapshots" at "s3://s3-us-east-1.amazonaws.com/maven.audiencereport.com/snapshots",
  "AudienceReport Releases" at "s3://s3-us-east-1.amazonaws.com/maven.audiencereport.com/releases",
)
