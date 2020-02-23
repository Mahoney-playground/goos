plugins {
  kotlin("jvm") version kotlinVersion
}

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlintest("core"))
  implementation(kotlintest("runner-junit5"))
  implementation("io.mockk:mockk:1.9.3")
}

tasks.register<DownloadDependenciesTask>("downloadDependencies")
