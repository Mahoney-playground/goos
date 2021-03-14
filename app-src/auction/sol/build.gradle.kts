plugins {
  kotlin("jvm")
  id("lidalia-idea-plugin")
}

dependencies {
  api(project(":auction-api"))
  implementation(project(":kotlinlangext"))
}

idea {
  setPackagePrefix("goos.auction.sol")
}
