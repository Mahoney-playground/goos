plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

dependencies {

  implementation(project(":core"))
  implementation(smack("core"))
  implementation(smack("tcp"))
  implementation(smack("extensions"))
  implementation(smack("java7"))
  implementation(smack("im"))
  implementation(kotlintest("core"))
  implementation(kotlintest("runner-junit5"))

  implementation("com.googlecode.windowlicker:windowlicker-swing:r268")
}

application {
  mainClassName = "goos.Main"
}
