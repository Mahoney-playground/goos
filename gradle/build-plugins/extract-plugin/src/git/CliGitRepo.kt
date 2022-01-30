package uk.org.lidalia.gradle.plugins.extractplugin.git

import uk.org.lidalia.gradle.plugins.extractplugin.either.orThrow
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.Directory
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.deleteRecursively
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.exists
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.isEmpty
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.join
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.mkdirs
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.toDirectory
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.toFile
import uk.org.lidalia.gradle.plugins.extractplugin.filesystem.toPath
import uk.org.lidalia.gradle.plugins.extractplugin.process.invoke
import uk.org.lidalia.gradle.plugins.extractplugin.resources.resource
import java.nio.file.Path

class CliGitRepo(
  private val path: Directory
) : GitRepo {
  override fun extractGradlePluginToNewGitRepo(
    parentOfNewRepository: Directory,
    relativePathToPlugin: Path
  ): GitRepo {
    parentOfNewRepository.mkdirs()

    val gradlePathsToKeep = listOf(
      "gradle/wrapper",
      "gradlew",
      "gradlew.bat",
    ).map(String::toPath)

    val devProjectPathsToKeep = listOf(
      ".editorconfig",
    ).map(String::toPath)

    val jvmProjectPathsToKeep = listOf(
      ".sdkmanrc"
    ).map(String::toPath)

    val dockerProjectPathsToKeep = listOf(
      ".dockerignore"
    ).map(String::toPath)

    val extraPathsToKeep = gradlePathsToKeep +
      devProjectPathsToKeep +
      jvmProjectPathsToKeep +
      dockerProjectPathsToKeep

    return extractPathToNewRepo(relativePathToPlugin, parentOfNewRepository, extraPathsToKeep)
  }

  override fun extractPathToNewRepo(
    relativePathToExtract: Path,
    parentOfNewRepository: Directory,
    extraPathsToKeep: List<Path>
  ): GitRepo {
    val absolutePathToExtract = path.resolve(relativePathToExtract)
    assert(absolutePathToExtract.exists()) { "$absolutePathToExtract must exist" }
    val dirToExtract = absolutePathToExtract.fileName
    val newRepoDir = newRepoDir(parentOfNewRepository, dirToExtract)

    val gitFilterRepoScript = resource("git-filter-repo.py")!!.toFile()

    val pathsToKeep = listOf(
      relativePathToExtract,
      ".gitattributes".toPath(),
      ".gitignore".toPath(),
    ) + extraPathsToKeep

    "git clone --no-local ${this.path} $newRepoDir"()
    gitFilterRepoScript.invoke(
      dir = newRepoDir,
      args = pathsToKeep.flatMap { path -> listOf("--path", path.toString()) }.toTypedArray(),
    )
    "mv $relativePathToExtract/* ."(dir = newRepoDir)
    dirToDelete(pathsToKeep, relativePathToExtract)?.deleteRecursively()
    "git add ."(dir = newRepoDir)
    "git commit -m 'Extracted $relativePathToExtract to new repository'"(dir = newRepoDir)
    return CliGitRepo(newRepoDir)
  }

  private fun newRepoDir(
    parentOfNewRepository: Directory,
    dirToExtract: Path
  ): Directory {
    val pathToNewRepository = parentOfNewRepository.resolve(dirToExtract)

    return if (pathToNewRepository.exists()) {
      pathToNewRepository.toDirectory().orThrow().apply {
        assert(isEmpty) { "$pathToNewRepository is not empty, cannot safely proceed" }
      }
    } else {
      pathToNewRepository.mkdirs()
      pathToNewRepository.toDirectory().orThrow()
    }
  }

  override fun publishTo(remote: GitRemote) {
    TODO("Not yet implemented")
  }

  companion object {
    fun dirToDelete(
      pathsToKeep: List<Path>,
      relativePathToExtract: Path
    ): Path? {
      val pathToExtractSegments = relativePathToExtract.toList()
      val longestCommonPathToKeep = pathsToKeep
        .map {
          val segments = it.toList()
          segments
            .take(pathToExtractSegments.count())
            .withIndex()
            .takeWhile { (i, segment) ->
              pathToExtractSegments[i] == segment
            }
            .withoutIndex()
        }
        .maxByOrNull { it.count() }
      return if (longestCommonPathToKeep != null)
        if (pathToExtractSegments.count() > longestCommonPathToKeep.count()) {
          pathToExtractSegments.take(longestCommonPathToKeep.count() + 1).join()
        } else {
          null
        }
      else pathToExtractSegments.first()
    }
  }
}

fun <T> Iterable<IndexedValue<T>>.withoutIndex(): List<T> = map(IndexedValue<T>::value)
