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
  implementation(kotest("core"))
  implementation(kotest("runner-junit5"))
  implementation("org.junit.platform:junit-platform-console:1.6.0")

  implementation("com.googlecode.windowlicker:windowlicker-swing:r268")
  implementation(project(":retry"))
  implementation(project(":clioptions"))
  implementation(fileTree("${project.rootDir}/buildSrc/build/"))
  implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.1")
}

application {
  mainClassName = "goos.MainKt"
}
