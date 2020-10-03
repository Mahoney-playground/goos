plugins {
  kotlin("jvm")
}

dependencies {
  api(kotlin("stdlib"))
  api(project(":auction-api"))
  implementation(kotest("assertions-core"))
  implementation(kotest("assertions-shared"))
  implementation(kotest("assertions-core-jvm"))
}

idea {
  setPackagePrefix("goos.auction.stub")
}
