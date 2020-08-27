plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":ui-api"))
  api(project(":auction-api"))
  api(project(":portfolio"))
  implementation(project(":kotlinlangext"))
}

idea {
  setPackagePrefix("goos.core")
}
