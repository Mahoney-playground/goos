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
}

application {
  mainClassName = "goos.MainKt"
}
