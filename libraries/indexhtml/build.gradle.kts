plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlinxHtml("jvm"))
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
