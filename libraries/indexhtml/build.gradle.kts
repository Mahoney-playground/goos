plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlinxHtml("jvm"))
  implementation(kotlinCoroutines("core"))
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
