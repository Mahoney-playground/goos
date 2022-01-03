package uk.org.lidalia.gradle.plugins.extractplugin.process

import uk.org.lidalia.gradle.plugins.extractplugin.either.Outcome
import uk.org.lidalia.gradle.plugins.extractplugin.either.failure
import uk.org.lidalia.gradle.plugins.extractplugin.either.success
import java.io.InputStream
import java.io.StringWriter
import java.time.Duration
import java.util.concurrent.TimeUnit.MILLISECONDS

class JavaProcess internal constructor(
  private val process: java.lang.Process,
  private val command: Command,
  outStream: Appendable,
  errStream: Appendable,
) : Process {

  override fun toString(): String {
    return "$process[$command]"
  }

  private val out = StringWriter()
  private val err = StringWriter()
  private val combined = StringBuffer()
  private val t1 = process.inputStream?.appendTo(MultiAppendable(out, outStream, combined))
  private val t2 = process.errorStream?.appendTo(MultiAppendable(err, errStream, combined))
  override val pid = process.pid()

  override fun await(timeout: Duration): Outcome<ProcessState, Succeeded> {
    val completed = process.waitFor(timeout.toMillis(), MILLISECONDS)
    if (!completed) {
      t1?.interrupt()
      t2?.interrupt()
    }
    joinThreads()
    val result = if (completed) {
      completedCommand()
    } else {
      Incomplete(
        command,
        out.toString(),
        err.toString(),
        combined.toString()
      )
    }
    return when (result) {
      is Succeeded -> result.success()
      else -> result.failure()
    }
  }

  override fun await(): Outcome<Failed, Succeeded> {
    process.waitFor()
    joinThreads()
    return when (val result = completedCommand()) {
      is Succeeded -> result.success()
      is Failed -> result.failure()
    }
  }

  private fun joinThreads() {
    t1?.join()
    t2?.join()
  }

  private fun completedCommand() = Completed(
    command,
    ExitStatus(process.exitValue()),
    out.toString(),
    err.toString(),
    combined.toString()
  )

  override fun destroy(): Process {
    process.destroy()
    return this
  }

  override fun destroyForcibly(): Process {
    process.destroyForcibly()
    return this
  }

  override fun isAlive(): Boolean = process.isAlive

  override fun info(): ProcessHandle.Info {
    return process.info()
  }

  companion object {
    private fun InputStream.appendTo(appendable: Appendable) =
      Thread {
        reader().buffered().forEachChar {
          appendable.append(it)
        }
      }.apply {
        start()
      }
  }
}

private class MultiAppendable(
  private vararg val appendables: Appendable
) : Appendable {

  override fun append(csq: CharSequence?): java.lang.Appendable {
    appendables.forEach { it.append(csq) }
    return this
  }

  override fun append(
    csq: CharSequence?,
    start: Int,
    end: Int
  ): Appendable {
    appendables.forEach { it.append(csq, start, end) }
    return this
  }

  override fun append(c: Char): java.lang.Appendable {
    appendables.forEach { it.append(c) }
    return this
  }
}

object Discard : Appendable {
  override fun append(csq: CharSequence?): Appendable = this

  override fun append(
    csq: CharSequence?,
    start: Int,
    end: Int
  ): Appendable = this

  override fun append(c: Char): Appendable = this
}
