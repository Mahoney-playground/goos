plugins {
  kotlin("jvm")
}

dependencies {
  api("org.testcontainers:testcontainers:1.16.2")
  implementation("com.github.docker-java:docker-java-api:3.2.12")
}

idea {
  setPackagePrefix("goos.testcontainers")
}
