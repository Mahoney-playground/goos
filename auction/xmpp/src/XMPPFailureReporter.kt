package goos.auction.xmpp

interface XMPPFailureReporter {

  fun cannotTranslateMessage(auctionId: String, failedMessage: String?, exception: Exception)
}
