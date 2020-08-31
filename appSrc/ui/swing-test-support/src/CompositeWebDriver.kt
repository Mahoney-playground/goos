package goos.uitestsupport

import org.openqa.selenium.Platform
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.HasInputDevices
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL

interface CompositeWebDriver : WebDriver, HasInputDevices

class CompositeRemoteWebDriver(
  delegate: RemoteWebDriver
) : CompositeWebDriver,
  WebDriver by delegate,
  HasInputDevices by delegate {

  constructor(url: URL = URL("http://app.internal:1234")) : this(
    RemoteWebDriver(url, DesiredCapabilities("java", "1.0", Platform.ANY))
  )
}
