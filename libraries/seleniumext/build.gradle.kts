plugins {
  kotlin("jvm")
}

dependencies {
  api(kotlin("stdlib"))
  api(selenium("api"))
  api(selenium("remote-driver"))
}

idea {
  setPackagePrefix("uk.org.lidalia.seleniumext")
}
