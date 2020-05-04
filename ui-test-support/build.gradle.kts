plugins {
  kotlin("jvm")
}

dependencies {
  api("com.jaliansystems:marathon-java-driver:5.2.6.0")
  implementation(kotest("core"))
  implementation(kotlinCoroutines("core"))
  runtimeOnly("net.bytebuddy:byte-buddy:1.10.9")
}
