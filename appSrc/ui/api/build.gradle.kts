plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":portfolio"))

  implementation(kotlin("stdlib"))
}

idea {
  setPackagePrefix("goos.ui.api")
}
