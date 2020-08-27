plugins {
  kotlin("jvm")
}

group = "uk.org.lidalia"

dependencies {
  api(kotlin("stdlib"))
  api(kotlinCoroutines("core-jvm"))

  implementation(kotlinCoroutines("core"))
}

idea {
  setPackagePrefix("uk.org.lidalia.kotlinlangext")
}
