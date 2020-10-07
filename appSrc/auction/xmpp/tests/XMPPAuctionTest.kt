package goos.auction.xmpp

import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.Logger
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.perSpec
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths
import kotlin.time.ExperimentalTime

@ExperimentalTime
class XMPPAuctionTest : StringSpec({

  tags(Docker)

  rootLogger.level = INFO

  @Suppress("DEPRECATION") // This is just a way to run on an ad hoc basis
  val dockerContainer = FixedHostPortGenericContainer<Nothing>(
    ImageFromDockerfile()
      .withFileFromPath(".", Paths.get(".").resolve("../../../docker-openfire"))
      .get()
  ).apply {
    withFixedExposedPort(5222, 5222)
    withFixedExposedPort(9090, 9090)
    withCreateContainerCmdModifier { it.withHostName("auctionhost.internal") }
    waitingFor(Wait.forHealthcheck())
  }

  listener(dockerContainer.perSpec())

  include(xmppAuctionApiTests())
}) {
  override fun isolationMode() = InstancePerTest
}

private val rootLogger = LoggerFactory.getLogger(ROOT_LOGGER_NAME) as Logger
