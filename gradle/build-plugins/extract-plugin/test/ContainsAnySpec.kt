package uk.org.lidalia.gradle.plugins.extractplugin

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import uk.org.lidalia.gradle.plugins.extractplugin.stringfunctions.containsAny

class ContainsAnySpec : StringSpec({

  listOf(
    " "           to true,
    "a"           to true,
    "hello world" to true,
    "whatever"    to true,
    ""            to false,
    "hello"       to false,
  ).forEach { (input, expected) ->
    """"$input".containsAny('a', ' ') shouldBe $expected""" {
      input.containsAny('a', ' ') shouldBe expected
    }

    """"$input".containsAny(setOf('a', ' ')) shouldBe $expected""" {
      input.containsAny(setOf('a', ' ')) shouldBe expected
    }
  }

  listOf(
    " ",
    "a",
    "hello world",
    "whatever",
    "",
    "hello",
  ).forEach { input ->
    """"$input".containsAny() shouldBe false""" {
      input.containsAny() shouldBe false
    }

    """"$input".containsAny(emptySet()) shouldBe false""" {
      input.containsAny(emptySet()) shouldBe false
    }
  }
})
