plugins {
  kotlin("jvm")
}

dependencies {

  api(project(":auction-api"))

  implementation(kotlin("stdlib"))
  implementation(smack("tcp"))
  implementation(smack("core"))
  implementation(smack("im"))
  implementation(jxmpp("jid"))
  implementation(project(":kotlinlangext"))

  runtimeOnly(smack("extensions"))
  runtimeOnly(smack("java7"))
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
