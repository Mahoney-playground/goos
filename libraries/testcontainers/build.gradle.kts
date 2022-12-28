plugins {
  kotlin("jvm")
}

dependencies {
  api(libs.testcontainers)
  implementation(libs.dockerJava.api)
}

idea {
  setPackagePrefix("goos.testcontainers")
}
