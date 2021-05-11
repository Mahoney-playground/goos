import uk.org.lidalia.gradle.plugins.copywithoutversion.CopyWithoutVersionsTask

plugins {
  kotlin("jvm")
  id("uk.org.lidalia.application")
  id("dev.jacomet.logging-capabilities") version "0.9.0"
  id("uk.org.lidalia.copy-without-version")
}

val javaAgents: Configuration by configurations.creating

dependencies {

  implementation(projects.core)

  implementation(projects.uiApi)
  implementation(projects.uiSwing)

  implementation(projects.auctionApi)
  implementation(projects.auctionXmpp)

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
    into = buildDir.resolve("libs/agents")
  }

  named("build") {
    dependsOn(copyJavaAgents)
  }
}

loggingCapabilities {
  enforceLogback()
}
