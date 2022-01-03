package uk.org.lidalia.gradle.plugins.extractplugin.process

import uk.org.lidalia.gradle.plugins.extractplugin.either.Outcome
import java.time.Duration

interface Process {
  val pid: Long
  fun await(timeout: Duration): Outcome<ProcessState, Succeeded>
  fun await(): Outcome<Failed, Succeeded>
  fun destroy(): JavaProcess
  fun destroyForcibly(): JavaProcess
  fun isAlive(): Boolean
  fun info(): ProcessHandle.Info
}
