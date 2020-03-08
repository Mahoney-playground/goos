package uk.org.lidalia.gradle.plugins.reporting

import org.jsoup.Jsoup
import io.kotest.core.spec.style.StringSpec
// import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files

@Suppress("unused")
class BuildDashboardPluginTest : StringSpec({

  "works" {

    val testProjectDir = tempDir("BuildDashboardPluginTest")

    testProjectDir.settingsGradle("""
      include(
        ":child2"
      )
      include(":grandchild")
      project(":grandchild").projectDir = file("child1/grandchild")
    """)

    testProjectDir.buildGradle("""
      plugins {
        base
        id("alt-build-dashboard")
        id("project-report")
      }
    """)

    testProjectDir
      .newDirectory("child1")
      .newDirectory("grandchild")
      .buildGradle("""
        plugins {
          base
          id("project-report")
        }
      """)

    testProjectDir
      .newDirectory("child2")
      .buildGradle("""
        plugins {
          base
          id("project-report")
        }
      """)

    GradleRunner.create()
      .withProjectDir(testProjectDir)
      .withArguments("projectReport", "buildADashboard")
      .withPluginClasspath()
      .forwardOutput()
      .build()

//    testProjectDir
//      .resolve("build/reports/child1/grandchild/a_report.txt")
//      .readText() shouldBe ""
//
//    testProjectDir
//      .resolve("build/reports/child2/a_report.txt")
//      .readText() shouldBe ""
//
//    testProjectDir.resolve("build/reports/index.html")
//      .readHtml()
  }
})

private fun File.newFile(name: String, content: String): File =
  resolve(name).apply { writeText(content) }

private fun File.newDirectory(name: String): File =
  resolve(name).apply { mkdir() }

private fun File.settingsGradle(content: String): File =
  newFile("settings.gradle.kts", content.trimIndent())

private fun File.buildGradle(content: String): File =
  newFile("build.gradle.kts", content.trimIndent())

private fun File.readHtml() = Jsoup.parse(this, "UTF-8", "http://example.com/")

private fun StringSpec.tempDir(prefix: String): File {
  val testProjectDir = Files.createTempDirectory(prefix).toFile()
  println("***** $testProjectDir ******")
  afterSpec {
//    testProjectDir.deleteRecursively()
  }
  return testProjectDir
}
