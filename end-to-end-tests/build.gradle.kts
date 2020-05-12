plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {

  implementation(kotest("core"))
  implementation(kotest("runner-junit5"))
  constraints {
    implementation(mockk)
  }
  implementation("org.junit.platform:junit-platform-console:1.6.0")

  implementation(project(":clioptions"))
  implementation(kotlinxHtml("jvm"))

  runtimeOnly(marathon("java-agent"))
  implementation(project(":ui-test-support"))
  implementation(project(":xmpp-test-support"))
}

application {
  mainClassName = "goos.MainKt"
}
