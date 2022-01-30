package uk.org.lidalia.gradle.plugins.extractplugin.filesystem

import uk.org.lidalia.gradle.plugins.extractplugin.either.Outcome
import uk.org.lidalia.gradle.plugins.extractplugin.either.failure
import uk.org.lidalia.gradle.plugins.extractplugin.either.success
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

fun Path.mkdirs(): Boolean = toFile().mkdirs()
val Path.isDirectory: Boolean get() = toFile().isDirectory
val Path.isEmpty: Boolean get() = toFile().listFiles()!!.isEmpty()

fun Path.exists(): Boolean = toFile().exists()

fun String.toPath(): Path = Paths.get(this)
fun String.toDirectory(): Outcome<NotADirectory, Directory> = Directory(Paths.get(this))

fun URL.toFile(): File = this.toURI().toFile()

fun URI.toFile(): File = File(this)

class Directory private constructor(
  private val path: Path
) : Path by path {

  companion object {
    operator fun invoke(path: Path): Outcome<NotADirectory, Directory> =
      if (path.isDirectory) Directory(path).success()
      else NotADirectory(path).failure()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    return if (other is Directory) path == other.path
    else false
  }

  override fun hashCode(): Int = path.hashCode()

  override fun toString(): String = path.toString()
}

fun Path.toDirectory(): Outcome<NotADirectory, Directory> = Directory(this)

class NotADirectory(val path: Path) : IllegalArgumentException("$path is not a directory")

fun Iterable<Path>.join(): Path? = fold(null as Path?) { acc, path -> acc?.resolve(path) ?: path }

fun Path.deleteRecursively() {
  toFile().deleteRecursively()
}
