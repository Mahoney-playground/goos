plugins {
  kotlin("jvm")
}

dependencies {
  api(kotlin("stdlib"))

  implementation(arrow("core-data"))
  implementation(kotlinCoroutines("core"))
  implementation(kotlinCoroutines("core-jvm"))
}
