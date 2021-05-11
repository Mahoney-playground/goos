plugins {
  kotlin("jvm")
  id("uk.org.lidalia.application")
}

dependencies {

  implementation(libs.kotest.frameworkApi)
  implementation(libs.kotest.frameworkApiJvm)

  implementation(testFixtures(projects.uiApi))
  implementation(testFixtures(projects.uiSwing))
  implementation(testFixtures(projects.auctionApi))
  implementation(testFixtures(projects.auctionXmpp))

  implementation(libs.coroutines.core)
  constraints {
    implementation(libs.mockk.core)
  }

  implementation(projects.testlauncher)
  implementation(libs.selenium.api)

  testImplementation(projects.testcontainers)
  testImplementation("ch.qos.logback:logback-classic:1.2.3")
  testImplementation(libs.kotest.extensionsTestcontainers)
  testImplementation(libs.marathon.javaDriver)
  testImplementation(projects.app)
  testImplementation(testFixtures(projects.uiStub))
  testImplementation(testFixtures(projects.auctionStub))
  testImplementation(projects.core)
}

application {
  mainClass.set("goos.MainKt")
}
