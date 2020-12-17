plugins {
  kotlin("jvm")
}

group = "uk.org.lidalia"

dependencies {
  api(kotlinCoroutines("core-jvm"))

  implementation(kotlinCoroutines("core"))
}

idea {
  setPackagePrefix("uk.org.lidalia.kotlinlangext")
}
