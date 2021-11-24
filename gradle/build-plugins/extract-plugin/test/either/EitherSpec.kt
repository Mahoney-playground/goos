package uk.org.lidalia.gradle.plugins.extractplugin.either

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EitherSpec : StringSpec({

  val left: Either<Int, String> = Left(1)
  val right: Either<Int, String> = Right("2")

  "can map a Left" {
    left.map { it.toInt() } shouldBe left
  }

  "can map a Right" {
    right.map { it.toInt() } shouldBe Right(2)
  }

  "can flatMap a Left" {
    left.map { it.toInt() } shouldBe left
  }

  "can flatMap a Right" {
    right.map { it.toInt() } shouldBe Right(2)
  }
})
