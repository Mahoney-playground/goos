plugins {
  kotlin("jvm")
}

group = "uk.org.lidalia"

dependencies {
  implementation(kotlinCoroutines("core"))
  implementation(kotlinCoroutines("core-common"))
  implementation(kotlinCoroutines("jdk8"))
}

idea {
  setPackagePrefix("uk.org.lidalia.kotlinlangext")
}
