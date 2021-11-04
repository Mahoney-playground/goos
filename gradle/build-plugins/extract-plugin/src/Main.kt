package uk.org.lidalia.gradle.plugins.reportaggregator

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Paths

fun main() {

  val pathToExistingRepository = Paths.get("/Users/Robert/Workspaces/goos")
  val plugin = "download-dependencies"
  val pluginToExtract = Paths.get("gradle/build-plugins/$plugin")
  val pathToNewRepository = Paths.get("/Users/Robert/Workspaces/$plugin")

  val gitFilterRepoScript = resource("git-filter-repo.py")!!.toFile()

  "git clone --no-local $pathToExistingRepository $pathToNewRepository"()
  gitFilterRepoScript(
    "--path", pluginToExtract.toString(),
    "--path", "gradle/wrapper",
    "--path", "gradlew",
    "--path", "gradlew.bat",
    "--path", ".editorconfig",
    "--path", ".gitattributes",
    "--path", ".gitignore",
    dir = pathToNewRepository
  )
  "mv $pluginToExtract/* ."(dir = pathToNewRepository)
  "rm -rf gradle/build-plugins"(dir = pathToNewRepository)
  "git add ."(dir = pathToNewRepository)
  "git commit -m 'Extracted $plugin to new repository'"(dir = pathToNewRepository)
}

private fun URL.toFile() = this.toURI().toFile()
private fun URI.toFile() = File(this)

private fun resource(resouceName: String) =
  Thread.currentThread().contextClassLoader.getResource(resouceName)
