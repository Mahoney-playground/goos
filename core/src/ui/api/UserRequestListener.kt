package goos.ui.api

import goos.portfolio.Item

interface UserRequestListener {
  fun joinAuction(item: Item)
  fun reset()
  fun disconnect()
}

class MultiUserRequestListener : UserRequestListener {
  private val listeners = mutableListOf<UserRequestListener>()

  fun addListener(listener: UserRequestListener) {
    listeners.add(listener)
  }

  override fun joinAuction(item: Item) {
    listeners.forEach { it.joinAuction(item) }
  }

  override fun reset() {
    listeners.forEach { it.reset() }
  }

  override fun disconnect() {
    listeners.forEach { it.disconnect() }
  }
}
