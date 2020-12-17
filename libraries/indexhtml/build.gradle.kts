plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlinxHtml("jvm"))
  implementation(kotlinCoroutines("core"))
  implementation(kotlinCoroutines("core-jvm"))
}

idea {
  setPackagePrefix("uk.org.lidalia.indexhtml")
}
