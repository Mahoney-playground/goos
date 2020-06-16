plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":ui-api"))
  api(project(":auction-api"))
  implementation(project(":kotlinlangext"))
}

idea {
  setPackagePrefix("goos.core")
}
