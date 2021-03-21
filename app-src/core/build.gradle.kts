plugins {
  kotlin("jvm")
  id("uk.org.lidalia.idea-ext-plugin")
}

dependencies {
  api(project(":ui-api"))
  api(project(":auction-api"))

  implementation(project(":kotlinlangext"))
}

idea {
  setPackagePrefix("goos.core")
}
