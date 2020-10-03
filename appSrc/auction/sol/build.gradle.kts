plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":auction-api"))
  api(kotlin("stdlib"))
  implementation(project(":kotlinlangext"))
}

idea {
  setPackagePrefix("goos.auction.sol")
}
