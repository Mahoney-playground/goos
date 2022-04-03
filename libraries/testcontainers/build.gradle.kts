plugins {
  kotlin("jvm")
}

dependencies {
  api(libs.testcontainers)
  implementation(libs.dockerJava.core)
}

idea {
  setPackagePrefix("goos.testcontainers")
}
