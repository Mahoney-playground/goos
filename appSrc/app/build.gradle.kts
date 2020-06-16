plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

val javaAgents by configurations.creating

dependencies {
  implementation(project(":core"))
  implementation(project(":ui-swing"))
  implementation(project(":auction-xmpp"))

  javaAgents(marathon("java-agent"))
}

application {
  mainClassName = "goos.app.MainKt"
  applicationName = "goos"
}

tasks.jar {
  archiveBaseName.set(application.applicationName)
}

idea {
  setPackagePrefix("goos.app")
}

tasks {

  val copyJavaAgents by registering(Copy::class) {
    from(javaAgents)
    into(buildDir.resolve("libs/agents"))
  }

  named("build") {
    dependsOn(copyJavaAgents)
  }
}
