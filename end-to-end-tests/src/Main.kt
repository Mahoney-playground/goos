package goos

import org.intellij.lang.annotations.Language
import org.junit.platform.console.ConsoleLauncher

fun main(args: Array<String>) {

  val defaultedArgs = args
    .withDefaultSelectPackage("goos")
    .withDefaultIncludeClassName("^(Test.*|.+[.$]Test.*|.*Tests?|.*Spec)$")
    .withDefaultReportsDir("build/reports")

  ConsoleLauncher.main(*defaultedArgs)
}

private fun Array<String>.withDefaultSelectPackage(pkg: String) =
  withDefaultOption('p', "select-package", pkg)

private fun Array<String>.withDefaultIncludeClassName(@Language("RegExp") pattern: String) =
  withDefaultOption('n', "include-classname", pattern)

private fun Array<String>.withDefaultReportsDir(reportsDir: String) =
  withDefaultOption(null, "reports-dir", reportsDir)

private fun Array<String>.withDefaultOption(
  shortOption: Char?,
  longOption: String,
  value: String
): Array<String> {

  val alreadyHasOption =
    (shortOption != null && this.contains("-$shortOption")) ||
      this.any { it.startsWith("--$longOption=") }

  return if (alreadyHasOption) this else this + "--$longOption=$value"
}
