plugins {
  kotlin("jvm")
}

dependencies {
  api(kotlin("stdlib"))
  api(selenium("api"))
  api(selenium("remote-driver"))
//  constraints {
//    implementation(byteBuddy)
//  }

  implementation(kotest("assertions-core"))
  implementation(kotest("assertions-shared-jvm"))
  implementation(kotlinCoroutines("core"))
  implementation(kotlinCoroutines("core-jvm"))
  constraints {
    implementation(mockk)
  }
}
