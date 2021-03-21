plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":auction-api"))
  implementation(project(":kotlinlangext"))
}

idea {
  setPackagePrefix("goos.auction.sol")
}
