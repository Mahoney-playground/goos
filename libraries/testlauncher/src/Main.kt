package uk.org.lidalia.testlauncher

import org.intellij.lang.annotations.Language
import org.junit.platform.console.ConsoleLauncher
import uk.org.lidalia.clioptions.getOption
import uk.org.lidalia.clioptions.withDefaultOption
import uk.org.lidalia.indexhtml.addIndexFiles
import java.io.File

fun runTests(args: Array<String>): Int {
  val defaultedArgs = args
    .withDefaultSelectPackage("goos")
    .withDefaultIncludeClassName("^(Test.*|.+[.$]Test.*|.*Tests?|.*Spec)$")
    .withDefaultReportsDir("build/reports")

  val exitCode = ConsoleLauncher
    .execute(System.out, System.err, *defaultedArgs)
    .exitCode

  val reportsDir = defaultedArgs.reportsDir()!!
  reportsDir.addIndexFiles()
  return exitCode
}

private fun Array<String>.withDefaultSelectPackage(pkg: String) =
  withDefaultOption('p', "select-package", pkg)

private fun Array<String>.withDefaultIncludeClassName(@Language("RegExp") pattern: String) =
  withDefaultOption('n', "include-classname", pattern)

private const val reportsDirOption = "reports-dir"

private fun Array<String>.withDefaultReportsDir(reportsDir: String) =
  withDefaultOption(null, reportsDirOption, reportsDir)

private fun Array<String>.reportsDir() = getOption(null, reportsDirOption)?.let { File(it) }
