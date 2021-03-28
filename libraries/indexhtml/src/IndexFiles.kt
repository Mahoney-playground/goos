package uk.org.lidalia.indexhtml

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.lang
import kotlinx.html.meta
import kotlinx.html.stream.appendHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe
import org.intellij.lang.annotations.Language
import java.io.File

fun File.addIndexFiles() = runBlocking(Dispatchers.Default) {
  this@addIndexFiles.addIndexFiles(false)
}

private suspend fun File.addIndexFiles(includeParentLink: Boolean) {
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
): String {
  val children = listFiles().orEmpty()
    .filterNot { it.isHidden }
    .directoriesFirst()
  return indexHtmlTemplate(
    this,
    includeParentLink,
    children,
  )
}

private fun indexHtmlTemplate(
  directory: File,
  includeParentLink: Boolean,
  children: List<File>
) = buildString {
  @Language("css")
  val css = """
    a {
      text-decoration: none;
    }
  """.trimIndent()
  appendLine("<!DOCTYPE html>")
  appendHTML()
    .html {
      lang = "en"
      head {
        title(directory.name)
        meta {
          charset = "utf-8"
        }
        style {
          unsafe { +css }
        }
      }
      body {
        h1 { +"Index of ${directory.name}" }
        div {
          if (includeParentLink) {
            div {
              a(href = "..") { +"⬆ Parent Directory" }
            }
          }
          children.forEach { child ->
            val icon = if (child.isDirectory) "\uD83D\uDCC1" else "\uD83D\uDCC4"
            div {
              a(href = child.name) { +"$icon ${child.name}" }
            }
          }
        }
      }
    }
}

private fun List<File>.directoriesFirst(): List<File> {
  val (directories, files) = partition { it.isDirectory }
  return directories.sorted() + files.sorted()
}

private suspend fun File.addIndexFilesToSubDirs() =
  listFiles().orEmpty()
    .filter { it.isDirectory && !it.isHidden }
    .forEachParallel { it.addIndexFiles(includeParentLink = true) }

private suspend fun <T> Iterable<T>.forEachParallel(f: suspend (T) -> Unit) {
  coroutineScope {
    forEach { t ->
      launch {
        f(t)
      }
    }
  }
}
