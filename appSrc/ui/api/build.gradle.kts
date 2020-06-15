plugins {
  kotlin("jvm")
}

dependencies {
  api(project(":portfolio"))
}

idea {
  setPackagePrefix("goos.ui.api")
}
