plugins {
  kotlin("jvm")
}

dependencies {

  api(project(":auction-api"))

  implementation(smack("core"))
  implementation(smack("tcp"))
  implementation(smack("extensions"))
  implementation(smack("java7"))
  implementation(smack("im"))
  implementation(project(":kotlinlangext"))
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
