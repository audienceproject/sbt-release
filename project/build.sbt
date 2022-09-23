// Slight hack that makes ReleasePlugin available in the main build.sbt without depending
// on a previously released version, thus avoiding the bootstrap problem.
// One slight hiccup is that `libraryDependencies` must be available here, which is solved
// by moving them to a dedicated file and creating symlink to it from this folder.
Compile / unmanagedSourceDirectories += {
  baseDirectory.value.getParentFile / "src" / "main" / "scala"
}
