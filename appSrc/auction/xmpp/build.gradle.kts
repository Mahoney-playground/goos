plugins {
  kotlin("jvm")
}

dependencies {

  api(project(":auction-api"))

  implementation(smack("tcp"))
  implementation(smack("core"))
  implementation(smack("extensions"))
  implementation(smack("java7"))
  implementation(smack("im"))
  implementation(project(":kotlinlangext"))

  testImplementation(project(":xmpp-test-support"))
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
