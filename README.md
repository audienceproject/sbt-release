# sbt-release

A simple SBT plugin for making git releases.

The release process consists of the following steps:

- Strip `-SNAPSHOT` from the version, commit and tag
- Bump version number and append `-SNAPSHOT`, commit
- Push the changes

## Usage

In your project, run

```shell
sbt release
```

and then follow the onscreen instructions.

## Installation

Add the following to `project/plugins.sbt`:

```sbt
addSbtPlugin("com.audienceproject" % "sbt-release" % "<VERSION>")
```

## Configuration

_sbt-release_ aims to have sane defaults and therefor only provides a very
limited set of configuration options, which are documented below.

You can find the default values in `ReleasePlugin.Defaults`.

### Changing message templates

_sbt-release_ uses a number of different message templates during the release
process. The templates can be changed by using a simple template string with a
single string placeholder (`%s`) for the version number:

````sbt
// Used when bumping version number and committing the changes
release / commitMsgReleaseTemplate := "🎉 Release version %s 🎉"
release / commitMsgDevCycleTemplate := "👷 🏗 Prepare for dev cycle %s"

// Used during tag creation
release / tagNameTemplate := "release/%s"
release / tagMessageTemplate := "Release %s"
````

### Changing version file name

By default, _sbt-release_ keeps track of the project version number in a file
called `version.sbt`.

This file should be considered as owned by _sbt-release_, and should **not** be
modified manually unless to bump the _major_ or _minor_ version number. The file
is overwritten during the release process.

If, for some reason, you wish to store the project version number in another
file, update `build.sbt` like so:

````sbt
release / versionSbtFile := baseDirectory.value / "my_version_file.sbt"
````
