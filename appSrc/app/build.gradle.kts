plugins {
  kotlin("jvm")
  id("alt-application-plugin")
}

val javaAgents: Configuration by configurations.creating

dependencies {

  implementation(kotlin("stdlib"))

  implementation(project(":core"))

  implementation(project(":ui-api"))
  implementation(project(":ui-swing"))

  implementation(project(":auction-api"))
  implementation(project(":auction-xmpp"))

  javaAgents(marathon("java-agent"))
}

application {
  mainClass.set("goos.app.MainKt")
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
