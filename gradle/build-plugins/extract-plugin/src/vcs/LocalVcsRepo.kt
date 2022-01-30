package uk.org.lidalia.gradle.plugins.extractplugin.vcs

import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.Directory
import java.nio.file.Path

interface LocalVcsRepo<Self : LocalVcsRepo<Self>> {

  fun extractPathToNewRepo(
    relativePathToExtract: Path,
    parentOfNewRepository: Directory,
    extraPathsToKeep: List<Path>
  ): Self
}
