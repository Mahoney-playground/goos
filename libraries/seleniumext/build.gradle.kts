plugins {
  kotlin("jvm")
}

dependencies {
  api(selenium("api"))
  api(selenium("remote-driver"))
}

idea {
  setPackagePrefix("uk.org.lidalia.seleniumext")
}
