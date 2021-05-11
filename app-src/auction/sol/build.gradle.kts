plugins {
  kotlin("jvm")
}

dependencies {
  api(projects.auctionApi)
  implementation(projects.kotlinlangext)
}

idea {
  setPackagePrefix("goos.auction.sol")
}
