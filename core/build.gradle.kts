plugins {
  kotlin("jvm")
}

dependencies {

  implementation(smack("core"))

  testImplementation("com.googlecode.windowlicker:windowlicker-swing:r268")
}
