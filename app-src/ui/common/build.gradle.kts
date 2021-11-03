plugins {
  kotlin("jvm")
}

dependencies {
  api(projects.uiApi)
}

idea {
  setPackagePrefix("goos.ui.common")
}
