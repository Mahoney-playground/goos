plugins {
  kotlin("jvm")
}

dependencies {
  api(kotlin("stdlib"))
  api(project(":auction-api"))
  api(project(":auction-sol"))
  implementation(kotest("assertions-core"))
  implementation(kotest("assertions-shared"))
}

idea {
  setPackagePrefix("goos.auction.stub")
}
