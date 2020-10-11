package goos.ui.stub

import goos.ui.api.uiApiTests
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StubUiTest : StringSpec({
  include(uiApiTests(StubUiDriver()))
}) {
  override fun isolationMode() = InstancePerTest
}
