plugins {
  kotlin("jvm")
}

group = "uk.org.lidalia"

dependencies {
  implementation(kotlinCoroutines("core"))
}

idea {
  setPackagePrefix("uk.org.lidalia.kotlinlangext")
}
