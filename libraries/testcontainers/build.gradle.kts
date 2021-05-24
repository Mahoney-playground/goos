plugins {
  kotlin("jvm")
}

dependencies {
  api("org.testcontainers:testcontainers:1.15.3")
  implementation("com.github.docker-java:docker-java-api:3.2.8")
}

idea {
  setPackagePrefix("goos.testcontainers")
}
