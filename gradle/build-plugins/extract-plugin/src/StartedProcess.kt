package uk.org.lidalia.gradle.plugins.reportaggregator

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.StringWriter
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

operator fun File.invoke(
  vararg args: String,
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): Unit = MultipleArgCommand(this.absolutePath, args.toList(), dir, env)
  .run()
  .await()

fun File.call(
  vararg args: String,
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): String = MultipleArgCommand(this.absolutePath, args.toList(), dir, env)
  .run()
  .awaitSuccess()

operator fun String.invoke(
  vararg args: String,
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): Unit = toCommand(dir, env, *args)
  .run()
  .await()

fun String.call(
  vararg args: String,
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
): String = toCommand(dir, env, *args)
  .run()
  .awaitSuccess()

fun String.toCommand(
  dir: Path = Paths.get("."),
  env: Map<String, String> = emptyMap(),
  vararg args: String,
): Command = if (args.isEmpty())
  SingleStringCommand(this, dir, env)
else
  MultipleArgCommand(this, args.toList(), dir, env)

sealed class Command {
  abstract fun run(): StartedProcess
  abstract val command: String
  abstract val dir: Path
  abstract val env: Map<String, String>
  internal fun run(processBuilder: ProcessBuilder): StartedProcess {
    processBuilder.environment().putAll(env)
    processBuilder.directory(dir.toFile())
    return StartedProcess(processBuilder.start(), this)
  }
}

class SingleStringCommand(
  override val command: String,
  override val dir: Path,
  override val env: Map<String, String>,
) : Command() {
  override fun run(): StartedProcess =
    run(ProcessBuilder("/usr/bin/env", "sh", "-c", command))
}

class MultipleArgCommand(
  val executable: String,
  val args: List<String>,
  override val dir: Path,
  override val env: Map<String, String>,
) : Command() {
  override val command: String = (listOf(executable) + args).joinToString(" ")
  override fun run(): StartedProcess = run(ProcessBuilder(executable, *args.toTypedArray()))
}

class StartedProcess internal constructor(
  private val process: Process,
  private val command: Command,
) : Process() {

  override fun toString(): String {
    return "$process[$command]"
  }

  fun awaitSuccess(): String =
    awaitResult(outStream = NoOpAppendable()).stdout

  fun awaitResult(
    timeout: Duration,
    outStream: Appendable = System.out,
    errStream: Appendable = System.err
  ): ProcessResult {
    val out = StringWriter()
    val err = StringWriter()
    val combined = StringBuffer()
    val t1 = consumeProcessStream(process.inputStream, MultiAppendable(out, outStream, combined))
    val t2 = consumeProcessStream(process.errorStream, MultiAppendable(err, errStream, combined))
    if (process.waitFor(timeout.toMillis(), MILLISECONDS)) {
      t1.join()
      t2.join()
      return Complete(
        command,
        ProcessStatus(process.exitValue()),
        out.toString(),
        err.toString(),
        combined.toString()
      )
    } else {
      t1.interrupt()
      t2.interrupt()
      t1.join()
      t2.join()
      return Incomplete(
        command,
        out.toString(),
        err.toString(),
        combined.toString()
      )
    }
  }

  fun awaitResult(
    outStream: Appendable = System.out,
    errStream: Appendable = System.err
  ): Complete {
    val out = StringWriter()
    val err = StringWriter()
    val combined = StringBuffer()
    val t1 = consumeProcessStream(process.inputStream, MultiAppendable(out, outStream, combined))
    val t2 = consumeProcessStream(process.errorStream, MultiAppendable(err, errStream, combined))
    val status = process.waitFor()
    t1.join()
    t2.join()
    return Complete(
      command,
      ProcessStatus(status),
      out.toString(),
      err.toString(),
      combined.toString()
    )
  }

  @JvmOverloads
  fun await(
    out: Appendable = System.out,
    err: Appendable = System.err
  ) {
    val result = awaitResult(out, err)
    if (result is Failure) {
      throw CommandFailedException(result)
    }
  }

  override fun getOutputStream(): OutputStream = process.outputStream

  override fun getInputStream(): InputStream = process.inputStream

  override fun getErrorStream(): InputStream = process.errorStream

  override fun waitFor(): Int = process.waitFor()

  override fun waitFor(timeout: Long, unit: TimeUnit): Boolean = process.waitFor(timeout, unit)

  override fun exitValue(): Int = process.exitValue()

  override fun destroy() = process.destroy()

  override fun destroyForcibly(): Process = process.destroyForcibly()

  override fun isAlive(): Boolean = process.isAlive

  companion object {

    private fun consumeProcessStream(stream: InputStream, error: Appendable) =
      Thread(TextDumper(stream, error)).apply {
        start()
      }
  }
}

private class TextDumper(private val `in`: InputStream, private val app: Appendable) : Runnable {

  override fun run() {
    `in`.reader().buffered().forEachLine {
      app.appendLine(it)
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

class NoOpAppendable : Appendable {
  override fun append(csq: CharSequence?): Appendable = this

  override fun append(
    csq: CharSequence?,
    start: Int,
    end: Int
  ): Appendable = this

  override fun append(c: Char): Appendable = this
}
