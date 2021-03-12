package goos.auction.xmpp

import goos.auction.api.Auction
import goos.auction.api.AuctionHouse
import goos.auction.sol.MessageListener
import goos.auction.sol.SolAuction
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class XMPPAuctionHouse(
  hostname: String,
  private val username: String,
  private val password: String
) : AuctionHouse {

  private val connection: XMPPTCPConnection = XMPPTCPConnection(
    XMPPTCPConnectionConfiguration.builder()
      .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
      .setXmppDomain(
        JidCreate.domainBareFrom(hostname)
      )
      .build()
  )

  override fun auctionFor(itemId: String): Auction {
    ensureConnected()
    return SolAuction(connection.user.toString()) { messageListener: MessageListener ->
      XMPPAuctionMessageTransport(
        connection,
        auctionId(itemId, connection.host),
        messageListener,
      )
    }
  }

  private val lock: Lock = ReentrantLock()
  private fun ensureConnected() {
    lock.withLock {
      if (!connection.isConnected) {
        connect()
      }
    }
  }

  private fun connect() {
    connection.connect()
    connection.login(
      username,
      password,
      Resourcepart.from("Auction")
    )
  }

  override fun disconnect() {
    if (connection.isConnected)
      connection.disconnect()
  }

  companion object {

    private val AUCTION_RESOURCE = Resourcepart.from("Auction")

    private fun auctionId(itemId: String, hostname: String): EntityBareJid =
      JidCreate.entityBareFrom("auction-$itemId@$hostname/$AUCTION_RESOURCE")
  }
}
