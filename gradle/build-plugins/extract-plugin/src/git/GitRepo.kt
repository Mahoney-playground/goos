package uk.org.lidalia.gradle.plugins.extractplugin.git

import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.Directory
import uk.org.lidalia.gradle.plugins.extractplugin.vcs.LocalVcsRepo
import java.nio.file.Path

interface GitRepo : LocalVcsRepo<GitRepo> {

  fun extractGradlePluginToNewGitRepo(
    parentOfNewRepository: Directory,
    relativePathToPlugin: Path
  ): GitRepo

  fun publishTo(remote: GitRemote)

  override fun extractPathToNewRepo(
    relativePathToExtract: Path,
    parentOfNewRepository: Directory,
    extraPathsToKeep: List<Path>
  ): GitRepo
}
