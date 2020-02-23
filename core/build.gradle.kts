plugins {
  kotlin("jvm")
  application
}

dependencies {

  implementation(smack("core"))

  testImplementation("com.googlecode.windowlicker:windowlicker-swing:r268")
}

application {
  mainClassName = "goos.core.Main"
}

val jar by tasks.getting(Jar::class) {
  manifest {
    attributes["Main-Class"] = application.mainClassName
  }
}
