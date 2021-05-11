plugins {
  kotlin("jvm")
}

dependencies {
  api(projects.uiApi)
  api(projects.auctionApi)

  implementation(projects.kotlinlangext)
}

idea {
  setPackagePrefix("goos.core")
}
