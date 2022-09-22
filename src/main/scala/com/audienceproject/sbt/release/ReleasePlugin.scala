package com.audienceproject.sbt.release

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{IndexDiff, Repository}
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.treewalk.FileTreeIterator
import sbt.Keys.{aggregate, baseDirectory, version}
import sbt.internal.util.ManagedLogger
import sbt.{Def, _}

import scala.Console.{BLUE, BOLD, GREEN, RESET}

object ReleasePlugin extends AutoPlugin {

  object Defaults {
    val CommitMessageReleaseTemplate = "[sbt-release] 🎉 Release version %s 🎉"
    val CommitMessageDevVersionTemplate = "[sbt-release] 🚧 Bump development version to %s 🚧"
    val TagNameTemplate = "v%s"
    val TagMessageTemplate = "Release v%s"
    val VersionFileName = "version.sbt"
  }

  override def trigger = allRequirements

  object autoImport {
    val release = taskKey[Unit]("Create and tag a release in this repository")
    val versionSbtFile = settingKey[File]("Name of .sbt file containing the version number")
    val commitMsgReleaseTemplate = settingKey[String]("Commit message template used when tagging release. Accepts as single '%s' placeholder for the version number")
    val commitMsgDevCycleTemplate = settingKey[String]("Commit message template used when starting dev cycle. Accepts as single '%s' placeholder for the version number")
    val tagNameTemplate = settingKey[String]("Template used to create the git tag name. Accepts as single '%s' placeholder for the version number")
    val tagMessageTemplate = settingKey[String]("Template used to create the git tag description. Accepts as single '%s' placeholder for the version number")
  }

  import autoImport._

  lazy val releaseTask = Def.task {
    implicit val logger: ManagedLogger = sbt.Keys.streams.value.log

    assertNoAggregation((release / aggregate).value)
    assertNoAggregation((ThisScope / release / aggregate).value)
    assertNoAggregation((ThisProject / release / aggregate).value)
    assertNoAggregation((ThisBuild / release / aggregate).value)

    val rootDir = assertRootProject((ThisBuild / baseDirectory).value, (ThisProject / baseDirectory).value)
    implicit val git: Git = Git.open(rootDir)
    implicit val versionFile: File = assertVersionFilePresence((release / versionSbtFile).value)
    val (currentVersion, releaseVersion, nextVersion) = incrementVersionNumber((ThisBuild / version).value)

    assertCleanWorkingDirectory(git.getRepository)
    printVersionSummary(currentVersion, releaseVersion, nextVersion)

    if (askToContinue(releaseVersion)) {
      bumpToVersion((release / commitMsgReleaseTemplate).value, releaseVersion)
      tagRelease((release / tagNameTemplate).value, (release / tagMessageTemplate).value, releaseVersion)

      bumpToVersion((release / commitMsgDevCycleTemplate).value, nextVersion)
      push((release / tagNameTemplate).value, releaseVersion)

      logger.info(s"${GREEN}Version $BLUE$BOLD$releaseVersion$RESET$GREEN was tagged and released")
    } else {
      logger.info("Aborting...")
    }
  }

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    release := releaseTask.value,
    release / commitMsgReleaseTemplate := Defaults.CommitMessageReleaseTemplate,
    release / commitMsgDevCycleTemplate := Defaults.CommitMessageDevVersionTemplate,
    release / tagNameTemplate := Defaults.TagNameTemplate,
    release / tagMessageTemplate := Defaults.TagMessageTemplate,
    release / versionSbtFile := baseDirectory.value / Defaults.VersionFileName,

    // Don't aggregate me, bro! Running this across sub-projects leads to weird results.
    release / aggregate := false,
    ThisScope / release / aggregate := false,
    ThisBuild / release / aggregate := false,
    ThisProject / release / aggregate := false,
  )

  private def askToContinue(version: String): Boolean =
    CommandLineUIService.confirm(s"Proceed with release of version $version?")

  private def bumpToVersion(messageTemplate: String, version: String)(implicit git: Git, versionFile: File): Unit = {
    IO.write(versionFile, s"""ThisBuild / version := "$version"\n""")
    git.add().addFilepattern(versionFile.name).call()
    git.commit().setMessage(messageTemplate.format(version)).call()
  }

  private def tagRelease(nameTemplate: String, messageTemplate: String, version: String)(implicit git: Git): Unit = {
    git.tag()
      .setAnnotated(true)
      .setName(nameTemplate.format(version))
      .setMessage(messageTemplate.format(version))
      .call()
  }

  private def push(nameTemplate: String, version: String)(implicit git: Git): Unit = {
    // We want to run `git push --follow-tags`, but JGit doesn't support that out of the box.
    // Instead, explicitly push the current branch and the newly created tag (to avoid pushing
    // any non-release tags the user might have created).

    git.push()
      .setAtomic(true)
      .setRefSpecs(
        new RefSpec(s"refs/tags/${nameTemplate.format(version)}"),
        new RefSpec(git.getRepository.getFullBranch)
      )
      .call()
  }

  private def assertRootProject(rootDir: File, projectDir: File)(implicit logger: ManagedLogger): File = {
    if (!rootDir.equals(projectDir)) {
      fail("Cannot release a sub-project!")
    }
    rootDir
  }

  private def assertNoAggregation(aggregate: Boolean)(implicit logger: ManagedLogger): Unit =
    if (aggregate) {
      fail("'release / aggregate' must be 'false'")
    }

  private def assertVersionFilePresence(file: File)(implicit logger: ManagedLogger): File = {
    if (!file.exists() || !file.isFile)
      fail(s"File '${file.name}' must be present - cannot continue")

    file
  }

  private def assertCleanWorkingDirectory(repo: Repository)(implicit logger: ManagedLogger): Unit = {
    val dirty = new IndexDiff(repo, "HEAD", new FileTreeIterator(repo)).diff()
    if (dirty)
      fail("git repository is dirty - cannot continue")
  }

  private def printVersionSummary(currentVersion: String, releaseVersion: String, nextVersion: String)(implicit logger: ManagedLogger): Unit = {
    logger.info(
      s"""${BOLD}Version summary:$RESET
         |  ${BOLD}Current version:$RESET    $currentVersion
         |  ${BOLD}Release version:$RESET    $BLUE$BOLD$releaseVersion$RESET
         |  ${BOLD}Next dev version:$RESET   $nextVersion""".stripMargin)
  }

  private def incrementVersionNumber(currentVersion: String)(implicit logger: ManagedLogger) = {
    val releaseVersion = currentVersion.stripSuffix("-SNAPSHOT")
    val nextVersion = releaseVersion.split('.') match {
      case Array(major, minor, patch) => s"$major.$minor.${patch.toInt + 1}-SNAPSHOT"
      case _ => fail(s"Unsupported version scheme: $releaseVersion")
    }
    (currentVersion, releaseVersion, nextVersion)
  }

  private def fail(msg: String)(implicit logger: ManagedLogger): String = {
    logger.error(s"!! $msg")
    throw ReleaseException(msg)
  }
}
