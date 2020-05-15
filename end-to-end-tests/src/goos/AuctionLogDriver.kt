package goos.goos

import io.kotest.inspectors.forOne
import java.io.File

class AuctionLogDriver {

  private val logFile = File(LOG_FILE_NAME)

  fun hasEntry(matcher: (String) -> Unit) {
    logFile.readLines().forOne(matcher)
  }

  fun reset() {

  }

  companion object {
    const val LOG_FILE_NAME = "auction-sniper.log"
  }
}
