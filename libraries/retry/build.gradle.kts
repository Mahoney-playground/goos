plugins {
  kotlin("jvm")
}

dependencies {
  implementation(arrow("core-data"))
  implementation(kotlinCoroutines("core"))
  implementation(kotlinCoroutines("core-jvm"))
}
