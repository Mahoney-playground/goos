plugins {
  kotlin("jvm")
  id("uk.org.lidalia.application")
}

dependencies {

  api(libs.kotest.frameworkApiJvm)
  api(testFixtures(projects.uiApi))
  api(testFixtures(projects.auctionApi))

  implementation(libs.kotest.frameworkApi)
  implementation(testFixtures(projects.uiSwing))
  implementation(testFixtures(projects.auctionXmpp))

  implementation(libs.coroutines.core)
  constraints {
    implementation(libs.mockk.core)
  }

  implementation(projects.testlauncher)
  implementation(libs.selenium.api)

  testImplementation(projects.testcontainers)
  testImplementation("ch.qos.logback:logback-classic:1.2.3")
  testImplementation(libs.kotestextensions.testcontainers)
  testImplementation(libs.marathon.javaDriver)
  testImplementation(projects.app)
  testImplementation(testFixtures(projects.uiStub))
  testImplementation(testFixtures(projects.auctionStub))
  testImplementation(projects.core)
}

application {
  mainClass.set("goos.MainKt")
}
