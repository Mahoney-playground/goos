plugins {
  kotlin("jvm")
}

dependencies {
  api("org.testcontainers:testcontainers:1.15.2")
  implementation("com.github.docker-java:docker-java-api:3.2.8")
}

idea {
  setPackagePrefix("goos.testcontainers")
}
