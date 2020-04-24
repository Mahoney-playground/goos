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
}

application {
  mainClassName = "goos.core.Main"
}
