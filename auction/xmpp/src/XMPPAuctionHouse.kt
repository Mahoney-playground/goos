package goos.auction.xmpp

import goos.auction.api.Auction
import goos.auction.api.AuctionHouse
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

class XMPPAuctionHouse private constructor(
  private val connection: XMPPTCPConnection
) : AuctionHouse {

  override fun auctionFor(itemId: String): Auction = XMPPAuction(connection, itemId)

  override fun disconnect() = connection.disconnect()

  companion object {
    fun connect(
      hostname: String,
      username: String,
      password: String
    ): XMPPAuctionHouse {
      val connection = XMPPTCPConnection(
        XMPPTCPConnectionConfiguration.builder()
          .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
          .setXmppDomain(
            JidCreate.domainBareFrom(hostname)
          )
          .build()
      )
      connection.connect()
      connection.login(
        username,
        password,
        Resourcepart.from("Auction")
      )
      return XMPPAuctionHouse(connection)
    }
  }
}
