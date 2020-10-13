package goos.ui.api

import io.kotest.core.factory.TestFactory
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe

fun uiApiTests(
  ui: UI,
  uiDriver: UiDriver
): TestFactory = stringSpec {

  "a possible test" {

    true shouldBe true
  }
}
