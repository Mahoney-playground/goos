package uk.org.lidalia.indexhtml

import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.meta
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe
import java.io.File

fun File.addIndexFiles(includeParentLink: Boolean = false) {
  if (!containsIndexFile()) {
    addIndexFile(includeParentLink)
    addIndexFilesToSubDirs()
  }
}

private fun File.containsIndexFile(): Boolean = listFiles().orEmpty().any { it.isIndexFile() }

private val indexFileRegex = "^(?i)index\\.html?$".toRegex()

private fun File.isIndexFile(): Boolean = name.matches(indexFileRegex)

private fun File.addIndexFile(includeParentLink: Boolean) =
  resolve("index.html")
    .writeText(indexFileHtml(includeParentLink))

private fun File.indexFileHtml(
  includeParentLink: Boolean
): String = createHTML()
  .html {
    head {
      title(name)
      meta {
        charset = "utf-8"
      }
      style {
        unsafe {
          +"""
          a {
            text-decoration: none;
          }
          """
        }
      }
    }
    body {
      h1 { +"Index of $name" }
      div {
        if (includeParentLink) {
          div {
            a(href = "..") { +"⬆ Parent Directory" }
          }
        }
        val children = listFiles().orEmpty()
          .filterNot { it.isHidden }
          .directoriesFirst()
        children.forEach { child ->
          val icon = if (child.isDirectory) "\uD83D\uDCC1" else "\uD83D\uDCC4"
          div {
            a(href = child.name) { +"$icon ${child.name}" }
          }
        }
      }
    }
  }
  .toString()

private fun List<File>.directoriesFirst(): List<File> {
  val (directories, files) = partition { it.isDirectory }
  return directories.sorted() + files.sorted()
}

private fun File.addIndexFilesToSubDirs() =
  listFiles().orEmpty()
    .filter { it.isDirectory && !it.isHidden }
    .parallelStream()
    .forEach { it.addIndexFiles(includeParentLink = true) }
