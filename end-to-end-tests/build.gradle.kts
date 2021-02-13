plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {

  implementation(libs.kotest.frameworkApi)
  implementation(libs.kotest.frameworkApiJvm)

  implementation(testFixtures(project(":ui-api")))
  implementation(testFixtures(project(":ui-swing")))
  implementation(testFixtures(project(":auction-api")))
  implementation(testFixtures(project(":auction-xmpp")))

  implementation(libs.coroutines.core)
  constraints {
    implementation(libs.mockk.core)
  }

  implementation(project(":testlauncher"))
  implementation(libs.selenium.api)

  testImplementation(project(":testcontainers"))
  testImplementation("ch.qos.logback:logback-classic:1.2.3")
  testImplementation(libs.kotest.extensionsTestcontainers)
  testImplementation(libs.marathon.javaDriver)
  testImplementation(project(":app"))
  testImplementation(testFixtures(project(":ui-stub")))
  testImplementation(testFixtures(project(":auction-stub")))
  testImplementation(project(":core"))
}

application {
  mainClass.set("goos.MainKt")
}
