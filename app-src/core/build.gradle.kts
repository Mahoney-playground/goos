plugins {
  kotlin("jvm")
}

dependencies {
  api(projects.uiApi)
  api(projects.auctionApi)
  api(projects.kotlinlangext)
}

idea {
  setPackagePrefix("goos.core")
}
