package goos.ui.api

import java.util.EventListener

interface UserRequestListener : EventListener {
  fun joinAuction(itemId: String)
  fun reset()
  fun disconnect()
}
