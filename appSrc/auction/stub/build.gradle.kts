plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlin("stdlib"))
  api(project(":auction-api"))
  implementation(kotest("assertions-core"))
  implementation(kotest("assertions-shared"))
  implementation(kotest("assertions-shared-jvm"))
}

idea {
  setPackagePrefix("goos.auction.stub")
}
