plugins {
  kotlin("jvm")
  id("uk.org.lidalia.application")
}

dependencies {

  implementation(testFixtures(projects.uiApi))
  implementation(testFixtures(projects.auctionApi))

  implementation(libs.kotest.frameworkApi)
  implementation(libs.kotest.frameworkApiJvm)
  implementation(testFixtures(projects.uiSwing))
  implementation(testFixtures(projects.auctionXmpp))

  implementation(libs.coroutines.core)
  constraints {
    implementation(libs.mockk.core)
  }

  implementation(projects.testlauncher)

  testImplementation(testFixtures(projects.uiStub))
  testImplementation(testFixtures(projects.auctionStub))
  testImplementation(projects.core)
}

application {
  mainClass.set("goos.MainKt")
}
