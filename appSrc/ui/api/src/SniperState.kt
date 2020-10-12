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

fun SniperSnapshot.stateText(): String = when (state) {
  SniperState.JOINING -> "Joining"
  SniperState.BIDDING -> "Bidding"
  SniperState.WINNING -> "Winning"
  SniperState.LOSING -> "Losing"
  SniperState.LOST -> "Lost"
  SniperState.WON -> "Won"
  SniperState.FAILED -> "Failed"
}
