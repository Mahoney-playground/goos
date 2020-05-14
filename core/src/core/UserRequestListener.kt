package goos.core

import java.util.EventListener

interface UserRequestListener : EventListener {
  fun joinAuction(itemId: String, stopPrice: Int)
  fun reset()
  fun disconnect()
}

class MultiUserRequestListener : UserRequestListener {
  private val listeners = mutableListOf<UserRequestListener>()

  fun addListener(listener: UserRequestListener) {
    listeners.add(listener)
  }

  override fun joinAuction(itemId: String, stopPrice: Int) {
    listeners.forEach { it.joinAuction(itemId, stopPrice) }
  }

  override fun reset() {
    listeners.forEach { it.reset() }
  }

  override fun disconnect() {
    listeners.forEach { it.disconnect() }
  }
}
