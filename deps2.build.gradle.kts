plugins {
  kotlin("jvm") version kotlinVersion
}

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation(smack("core"))
  implementation(smack("tcp"))
  implementation(smack("extensions"))
  implementation(smack("java7"))
  implementation(smack("im"))
  implementation("com.googlecode.windowlicker:windowlicker-swing:r268")
}

tasks.register<DownloadDependenciesTask>("downloadDependencies")
