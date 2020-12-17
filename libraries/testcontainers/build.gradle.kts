plugins {
  kotlin("jvm")
}

dependencies {
  api("org.testcontainers:testcontainers:1.15.0-rc2")
  implementation("com.github.docker-java:docker-java-api:3.2.7")
}

idea {
  setPackagePrefix("goos.testcontainers")
}
