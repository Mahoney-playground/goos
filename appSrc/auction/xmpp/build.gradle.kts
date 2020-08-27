plugins {
  kotlin("jvm")
}

dependencies {

  api(project(":auction-api"))

  implementation(smack("tcp"))
  implementation(smack("core"))
  implementation(smack("im"))
  implementation(jxmpp("jid"))
  implementation(project(":kotlinlangext"))

  runtimeOnly(smack("extensions"))
  runtimeOnly(smack("java7"))

  testImplementation(project(":xmpp-test-support"))
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
