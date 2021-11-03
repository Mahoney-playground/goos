package goos.ui.api

enum class SniperState {
  LOST,
  WON,
  JOINING,
  BIDDING,
  WINNING,
  LOSING,
  FAILED;
}

fun SniperState.text(): String = when (this) {
  SniperState.JOINING -> "Joining"
  SniperState.BIDDING -> "Bidding"
  SniperState.WINNING -> "Winning"
  SniperState.LOSING -> "Losing"
  SniperState.LOST -> "Lost"
  SniperState.WON -> "Won"
  SniperState.FAILED -> "Failed"
}

fun SniperSnapshot.stateText(): String = state.text()
