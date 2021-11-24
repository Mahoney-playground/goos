package uk.org.lidalia.gradle.plugins.extractplugin.process

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import java.io.InputStream
import java.io.StringWriter

@Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
class CommandTests : StringSpec({

  "returns result with no new line" {
    "printf 'hello world'"() shouldBe "hello world"
  }

  "returns result with new line" {
    "echo 'hello world'"() shouldBe "hello world\n"
  }

  "returns piped result" {
    ("printf 'hello world'" `|` "cat")() shouldBe "hello world"
  }

  "throws exception on fail" {
    val e = shouldThrow<ProcessFailedException> {
      "printf 'pre fail' && false && printf 'post fail'"()
    }
    e.message shouldMatch
      """Failure\(command='printf 'pre fail' && false && printf 'post fail'', status=1, output='pre fail'\)""".toRegex()
  }

  "throws exception on pipe fail" {

    val e = shouldThrow<ProcessFailedException> {
//      ("exit 1" pipe "echo 'hello world'").await()
    }
    e.message shouldMatch
      """Got status 1 running java.lang.UNIXProcess@[a-f0-9]+\[exit 1]""".toRegex()
  }

  "can recreate string ending in line feed" {
    val inputWithLineFeed = "foo\nbar\n"
    inputWithLineFeed.byteInputStream().reconstituted() shouldBe inputWithLineFeed
  }

  "can recreate string ending without line feed" {

    val inputWithoutLineFeed = "foo\nbar"
    inputWithoutLineFeed.byteInputStream().reconstituted() shouldBe inputWithoutLineFeed
  }
})

fun InputStream.reconstituted(): String {
  val s: Appendable = StringWriter()
  reader().forEachCompleteLine { line ->
    s.append(line)
  }
  return s.toString()
}
