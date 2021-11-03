package goos.ui.common

import goos.ui.api.Item
import goos.ui.api.ItemId

data class ItemData(
  override val identifier: ItemId,
  override val stopPrice: Int
) : Item
