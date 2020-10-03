package goos.auction.xmpp

import goos.auction.sol.MessageListener
import goos.auction.sol.MessageTransport
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat.ChatManager
import org.jxmpp.jid.EntityJid

internal class XMPPAuctionMessageTransport(
  connection: XMPPConnection,
  userJID: EntityJid,
  messageListener: MessageListener
) : MessageTransport {

  private val chat = ChatManager.getInstanceFor(connection)
    .createChat(userJID) { _, message ->
      messageListener.processMessage(message.body)
    }

  override fun sendMessage(message: String) {
    chat.sendMessage(message)
  }
}
