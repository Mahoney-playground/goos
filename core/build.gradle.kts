plugins {
  kotlin("jvm")
  application
}

dependencies {
}

application {
  mainClassName = "goos.core.Main"
}

val jar by tasks.getting(Jar::class) {
  manifest {
    attributes["Main-Class"] = application.mainClassName
  }
}
