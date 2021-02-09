plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {

  implementation(kotest("framework-api"))
  implementation(kotest("framework-api-jvm"))

  implementation(testFixtures(project(":ui-api")))
  implementation(testFixtures(project(":ui-swing")))
  implementation(testFixtures(project(":auction-api")))
  implementation(testFixtures(project(":auction-xmpp")))

  implementation(libs.coroutines.core)
  constraints {
    implementation(mockk)
  }

  implementation(project(":testlauncher"))
  implementation(selenium("api"))

  testImplementation(project(":testcontainers"))
  testImplementation("ch.qos.logback:logback-classic:1.2.3")
  testImplementation(kotest("extensions-testcontainers"))
  testImplementation(marathon("java-driver"))
  testImplementation(project(":app"))
  testImplementation(testFixtures(project(":ui-stub")))
  testImplementation(testFixtures(project(":auction-stub")))
  testImplementation(project(":core"))
}

application {
  mainClass.set("goos.MainKt")
}
