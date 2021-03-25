import uk.org.lidalia.gradle.plugins.copywithoutversion.CopyWithoutVersionsTask

plugins {
  kotlin("jvm")
  id("uk.org.lidalia.application")
  id("dev.jacomet.logging-capabilities") version "0.9.0"
  id("uk.org.lidalia.copy-without-version")
}

val javaAgents: Configuration by configurations.creating

dependencies {

  implementation(project(":core"))

  implementation(project(":ui-api"))
  implementation(project(":ui-swing"))

  implementation(project(":auction-api"))
  implementation(project(":auction-xmpp"))

  @Suppress("UnstableApiUsage")
  javaAgents(libs.marathon.javaAgent)
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

  val copyJavaAgents by registering(CopyWithoutVersionsTask::class) {
    from = javaAgents
    into = buildDir.resolve("lib/agents")
  }

  named("build") {
    dependsOn(copyJavaAgents)
  }
}

loggingCapabilities {
  enforceLogback()
}
