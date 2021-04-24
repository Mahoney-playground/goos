package goos

import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.Logger
import goos.auction.api.toAuctionId
import goos.auction.xmpp.XmppAuctionDriver
import goos.testcontainers.openfireTestContainer
import goos.ui.swing.AuctionSniperDriver
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.perSpec
import net.sourceforge.marathon.javadriver.JavaDriver
import org.slf4j.Logger.ROOT_LOGGER_NAME
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class AuctionSniperEndToEndContainerTest : StringSpec({

  tags(Docker)

  rootLogger.level = INFO

  listener(openfireTestContainer(Paths.get("../docker-openfire")).perSpec())

  include(
    auctionSniperEndToEndTest(
      XmppAuctionDriver("item-54321".toAuctionId()),
      XmppAuctionDriver("item-65432".toAuctionId()),
      ApplicationRunner(AuctionSniperDriver(JavaDriver()))
    )
  )

  beforeSpec {
    Thread {
      goos.app.main("auctionhost.internal", "sniper", "sniper")
    }.start()
  }
})

private val rootLogger = LoggerFactory.getLogger(ROOT_LOGGER_NAME) as Logger
