val jgitVersion = "7.7.1.202607240634-r"
libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % jgitVersion,
  "org.eclipse.jgit" % "org.eclipse.jgit.ssh.apache.agent" % jgitVersion,
  "org.eclipse.jgit" % "org.eclipse.jgit.gpg.bc" % jgitVersion, // Needed for ED25519 keys
)
