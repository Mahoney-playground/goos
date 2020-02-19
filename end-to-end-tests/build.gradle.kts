plugins {
  kotlin("jvm")
}

dependencies {

  implementation(project(":core"))
  implementation(smack("core"))
  implementation(smack("tcp"))
  implementation(smack("extensions"))
  implementation(smack("java7"))
  implementation(smack("im"))

  implementation("com.googlecode.windowlicker:windowlicker-swing:r268")
}
