package uk.org.lidalia.gradle.plugins.reportaggregator

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
// import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
// import uk.org.lidalia.gradle.plugins.reportaggregator.StartedProcess.Companion.execute
// import uk.org.lidalia.gradle.plugins.reportaggregator.StartedProcess.Companion.pipe

@Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
class ProcessWithCommandTests : StringSpec({

  "returns result" {
//    "echo 'hello world'".execute().awaitSuccess() shouldBe "hello world\n"
  }

  "returns piped result" {
//    ("echo 'hello world'" pipe "cat").awaitResult() shouldBe "hello world\n"
  }

  "throws exception on fail".config(enabled = false) {
    val e = shouldThrow<CommandFailedException> {
//      "exit 1".execute().await()
    }
    e.message shouldMatch
      """Got status 1 running java.lang.UNIXProcess@[a-f0-9]+\[exit 1]""".toRegex()
  }

  "throws exception on pipe fail".config(enabled = false) {

    val e = shouldThrow<CommandFailedException> {
//      ("exit 1" pipe "echo 'hello world'").await()
    }
    e.message shouldMatch
      """Got status 1 running java.lang.UNIXProcess@[a-f0-9]+\[exit 1]""".toRegex()
  }
})
