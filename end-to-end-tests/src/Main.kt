package goos

import uk.org.lidalia.testlauncher.runTests
import java.util.logging.Level.WARNING
import java.util.logging.Logger
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  Logger.getLogger("").level = WARNING

  val exitCode = runTests(args)

  exitProcess(exitCode)
}
