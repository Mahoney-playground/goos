package goos.ui.api

import uk.org.lidalia.kotlinlangext.notifiers.Notifier

interface UserRequestListener {
  fun joinAuction(item: Item)
  fun reset()
  fun closeApplication()
}

class MultiUserRequestListener : UserRequestListener, Notifier<UserRequestListener>() {
  override fun joinAuction(item: Item) = notify { joinAuction(item) }

  override fun reset() = notify { reset() }

  override fun closeApplication() = notify { closeApplication() }
}
