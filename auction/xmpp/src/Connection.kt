package goos.auction.xmpp

import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart

fun connection(
  hostname: String,
  username: String,
  password: String
): XMPPTCPConnection {
  val connection = XMPPTCPConnection(
    XMPPTCPConnectionConfiguration.builder()
      .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
      .setXmppDomain(
        JidCreate.domainBareFrom(
          hostname
        )
      )
      .build()
  )
  connection.connect()
  connection.login(username, password, Resourcepart.from("Auction"))
  return connection
}
