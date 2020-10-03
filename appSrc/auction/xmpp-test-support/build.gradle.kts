plugins {
  kotlin("jvm")
}

dependencies {
  api(kotlin("stdlib"))
  api(smack("core"))
  api(smack("im"))

  api(project(":auction-stub"))

  implementation(smack("tcp"))
  implementation(smack("extensions"))
  implementation(jxmpp("jid"))
  implementation(kotest("assertions-core"))
  implementation(kotest("assertions-shared"))
  implementation(kotest("assertions-shared-jvm"))
  constraints {
    implementation(mockk)
  }

  runtimeOnly(smack("java7"))
}
