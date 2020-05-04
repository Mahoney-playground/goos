plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {
  implementation(smack("core"))
  implementation(smack("tcp"))
  implementation(smack("extensions"))
  implementation(smack("java7"))
  implementation(smack("im"))
  implementation(project(":kotlinlangext"))
  testImplementation(marathon("java-driver"))
  testImplementation(project(":ui-test-support"))
}

application {
  mainClassName = "goos.core.app.Main"
}
