package goos.auction.xmpp

import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.Logger
import goos.testcontainers.openfireTestContainer
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.perSpec
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.time.ExperimentalTime

@ExperimentalTime
class XMPPAuctionTest : StringSpec({

  tags(Docker)

  rootLogger.level = INFO

  listener(openfireTestContainer(Paths.get("../../../docker-openfire")).perSpec())

  include(xmppAuctionApiTests())
}) {
  override fun isolationMode() = InstancePerTest
}

private val rootLogger = LoggerFactory.getLogger(ROOT_LOGGER_NAME) as Logger
