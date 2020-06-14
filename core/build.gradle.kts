plugins {
  kotlin("jvm")
  id("alt-application-plugin")
  kotlin("plugin.serialization") version "1.3.70"
}

val javaAgents by configurations.register("javaAgents")

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
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

  testImplementation(marathon("java-driver"))
  testImplementation(project(":ui-test-support"))
  testImplementation("org.threeten:threeten-extra:1.5.0")

  javaAgents(marathon("java-agent"))
}

application {
  mainClassName = "goos.app.MainKt"
}

idea {
  setPackagePrefix("goos")
}

tasks {

  named("test", Test::class) {
    // silence warnings due to using marathon java agent
    jvmArgs(
      "-Xshare:off",
      "--illegal-access=deny",
      "--add-exports", "java.desktop/sun.awt=ALL-UNNAMED"
    )
  }

  val copyJavaAgents by registering(Copy::class) {
    from(javaAgents)
    into(buildDir.resolve("libs/agents"))
  }

  named("build") {
    dependsOn(copyJavaAgents)
  }
}
