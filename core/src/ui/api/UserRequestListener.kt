package goos.core.ui.api

import java.util.EventListener

interface UserRequestListener : EventListener {
  fun joinAuction(itemId: String)
  fun reset()
}
