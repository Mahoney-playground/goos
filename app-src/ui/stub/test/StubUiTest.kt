package goos.ui.stub

import goos.ui.api.uiApiTests
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class StubUiTest : StringSpec({
  val ui = StubUi()
  include(uiApiTests(ui, StubUiDriver(ui)))
},) {
  override fun isolationMode() = InstancePerTest
}
