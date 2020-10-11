plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {

  implementation(kotlin("stdlib"))
  implementation(kotest("framework-api"))
  implementation(kotest("framework-api-jvm"))

  implementation(testFixtures(project(":ui-api")))
  implementation(testFixtures(project(":ui-swing")))
  implementation(testFixtures(project(":auction-api")))
  implementation(testFixtures(project(":auction-xmpp")))

  implementation(kotlinCoroutines("core"))
  constraints {
    implementation(mockk)
  }

  implementation(project(":testlauncher"))

  testImplementation(project(":testcontainers"))
  testImplementation("ch.qos.logback:logback-classic:1.2.3")
  testImplementation(kotest("extensions-testcontainers"))
  testImplementation(project(":seleniumext"))
  testImplementation(marathon("java-driver"))
  testImplementation(project(":app"))
}

application {
  mainClass.set("goos.MainKt")
}
