package uk.org.lidalia.gradle.plugins.extractplugin.process

import uk.org.lidalia.gradle.plugins.extractplugin.either.Outcome
import uk.org.lidalia.gradle.plugins.extractplugin.either.isFailure
import java.time.Duration

class PipedProcess(
  val processes: List<Process>
) : Process {

  override val pid: Long = processes.last().pid

  override fun await(timeout: Duration): Outcome<ProcessState, Succeeded> {
    val results = processes.map { it.await(timeout) }
    return results.firstOrNull { it.isFailure } ?: results.last()
  }

  override fun await(): Outcome<Failed, Succeeded> {
    val results = processes.map { it.await() }
    return results.firstOrNull { it.isFailure } ?: results.last()
  }

  override fun destroy(): Process {
    processes.forEach { it.destroy() }
    return this
  }

  override fun destroyForcibly(): Process {
    processes.forEach { it.destroyForcibly() }
    return this
  }

  override fun isAlive(): Boolean {
    return processes.any { it.isAlive() }
  }

  override fun info(): ProcessHandle.Info {
    TODO("Not yet implemented")
  }
}
