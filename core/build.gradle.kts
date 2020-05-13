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
  implementation(project(":auction-xmpp"))
  implementation(project(":auction-api"))
  implementation(kotlinCoroutines("core"))
  implementation(kotlinCoroutines("core-common"))
  implementation(kotlinCoroutines("jdk8"))
  implementation(kotlinCoroutines("swing"))

  testImplementation(marathon("java-driver"))
  testImplementation(project(":ui-test-support"))
}

application {
  mainClassName = "goos.app.MainKt"
}

idea {
  setPackagePrefix("goos")
}

val test by tasks.existing(Test::class) {
  // silence warnings due to using marathon java agent
  jvmArgs(
    "-Xshare:off",
    "--illegal-access=deny",
    "--add-exports", "java.desktop/sun.awt=ALL-UNNAMED"
  )
}
