plugins {
  kotlin("jvm")
}

dependencies {

  api(project(":auction-api"))
  implementation(project(":auction-sol"))

  implementation(kotlin("stdlib"))
  implementation(smack("tcp"))
  implementation(smack("core"))
  implementation(smack("im"))
  implementation(jxmpp("jid"))

  runtimeOnly(smack("extensions"))
  runtimeOnly(smack("java7"))
}

idea {
  setPackagePrefix("goos.auction.xmpp")
}
