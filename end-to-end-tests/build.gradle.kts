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
  implementation("org.junit.platform:junit-platform-console:1.6.0")

  implementation(project(":clioptions"))
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.1")

  runtimeOnly("com.jaliansystems:marathon-java-agent:5.2.6.0")
  implementation("org.seleniumhq.selenium:selenium-remote-driver:3.14.0")

  runtimeOnly(mockk)
}

application {
  mainClassName = "goos.MainKt"
}
