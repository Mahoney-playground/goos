package goos

import org.intellij.lang.annotations.Language
import org.junit.platform.console.ConsoleLauncher

fun main(args: Array<String>) {

  val defaultedArgs = args
    .withDefaultSelectPackage("goos")
    .withDefaultIncludeClassName("^(Test.*|.+[.$]Test.*|.*Tests?|.*Spec)$")

  ConsoleLauncher.main(*defaultedArgs)
}

private fun Array<String>.withDefaultSelectPackage(
  pkg: String
): Array<String> = withDefaultOption("p", "select-package", pkg)

private fun Array<String>.withDefaultIncludeClassName(
  @Language("RegExp") pattern: String
): Array<String> = withDefaultOption("n", "include-classname", pattern)

private fun Array<String>.withDefaultOption(
  shortOption: String,
  longOption: String,
  value: String
): Array<String> {

  val alreadyHasOption =
    this.contains("-$shortOption") ||
      this.any { it.startsWith("--$longOption=") }

  return if (alreadyHasOption) this else this + "--$longOption=$value"
}
