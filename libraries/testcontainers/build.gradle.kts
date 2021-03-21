plugins {
  kotlin("jvm")
  id("uk.org.lidalia.idea-ext-plugin")
}

dependencies {
  api("org.testcontainers:testcontainers:1.15.1")
  implementation("com.github.docker-java:docker-java-api:3.2.7")
}

idea {
  setPackagePrefix("goos.testcontainers")
}
