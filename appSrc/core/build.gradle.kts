plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":ui-api"))
  api(project(":auction-api"))
  api(project(":portfolio"))

  implementation(kotlin("stdlib"))
  implementation(project(":kotlinlangext"))
}

idea {
  setPackagePrefix("goos.core")
}
