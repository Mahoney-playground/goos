plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlin("stdlib"))
}

idea {
  setPackagePrefix("goos.auction.api")
}
