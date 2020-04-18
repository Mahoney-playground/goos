package goos

import org.intellij.lang.annotations.Language
import org.junit.platform.console.ConsoleLauncher
import kotlin.system.exitProcess
import uk.org.lidalia.indexhtml.addIndexFiles
import uk.org.lidalia.clioptions.withDefaultOption
import uk.org.lidalia.clioptions.getOption
import java.io.File

fun main(args: Array<String>) {

  val defaultedArgs = args
    .withDefaultSelectPackage("goos")
    .withDefaultIncludeClassName("^(Test.*|.+[.$]Test.*|.*Tests?|.*Spec)$")
    .withDefaultReportsDir("build/reports")

  val exitCode = ConsoleLauncher
    .execute(System.out, System.err, *defaultedArgs)
    .exitCode

  val reportsDir = defaultedArgs.reportsDir()!!
  reportsDir.addIndexFiles()

  exitProcess(exitCode)
}

private fun Array<String>.withDefaultSelectPackage(pkg: String) =
  withDefaultOption('p', "select-package", pkg)

private fun Array<String>.withDefaultIncludeClassName(@Language("RegExp") pattern: String) =
  withDefaultOption('n', "include-classname", pattern)

private const val reportsDir = "reports-dir"

private fun Array<String>.withDefaultReportsDir(reportsDir: String) =
  withDefaultOption(null, reportsDir, reportsDir)

private fun Array<String>.reportsDir() = getOption(null, reportsDir)?.let { File(it) }
