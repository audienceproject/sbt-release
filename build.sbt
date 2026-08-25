ThisBuild / organization := "com.audienceproject"
ThisBuild / organizationName := "Audience Project"
ThisBuild / name := "sbt-release"
ThisBuild / description := "A sbt plugin for releasing a Scala library"

enablePlugins(SbtPlugin, ReleasePlugin)

ThisBuild / scalaVersion := "2.12.20"

scalacOptions := Seq("-Xsource:2.12", "-encoding", "UTF-8", "-deprecation", "-unchecked")
