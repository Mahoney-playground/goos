plugins {
  kotlin("jvm")
  id("uk.org.lidalia.application")
}

dependencies {

  implementation(testFixtures(projects.uiApi))
  implementation(testFixtures(projects.auctionApi))

  implementation(libs.kotest.frameworkApi)
  implementation(libs.kotest.frameworkApiJvm)
  implementation(libs.slf4j.api)
  implementation(testFixtures(projects.uiSwing))
  implementation(testFixtures(projects.auctionXmpp))

  implementation(libs.coroutines.core)
  constraints {
    implementation(libs.mockk.core)
  }

  implementation(projects.testlauncher)

  testImplementation(libs.selenium.api)
  testImplementation(projects.testcontainers)
  testImplementation("ch.qos.logback:logback-classic:1.2.9")
  testImplementation(libs.kotestextensions.testcontainers)
  testImplementation(libs.marathon.javaDriver)
  testImplementation(projects.app)
  testImplementation(testFixtures(projects.uiStub))
  testImplementation(testFixtures(projects.auctionStub))
  testImplementation(projects.core)
  testImplementation(libs.testcontainers)
}

application {
  mainClass.set("goos.MainKt")
}
