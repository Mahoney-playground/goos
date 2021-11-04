package uk.org.lidalia.gradle.plugins.reportaggregator

import uk.org.lidalia.gradle.plugins.reportaggregator.ProcessStatus.Companion.SUCCESS

class ProcessStatus private constructor(val status: Int) {

  val isSuccess = status == 0

  companion object {
    val SUCCESS = ProcessStatus(0)
    operator fun invoke(status: Int): ProcessStatus {
      require(status >= 0)
      return if (status == 0) SUCCESS
      else ProcessStatus(status)
    }
  }

  override fun equals(other: Any?): Boolean =
    this === other || (other is ProcessStatus && other.status == status)

  override fun hashCode(): Int = status
  override fun toString(): String = status.toString()
}

sealed class ProcessResult {

  abstract val command: Command
  abstract val status: ProcessStatus?
  abstract val stdout: String
  abstract val stderr: String
  abstract val output: String
  abstract val isSuccess: Boolean

  final override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ProcessResult) return false

    if (command != other.command) return false
    if (status != other.status) return false
    if (stdout != other.stdout) return false
    if (stderr != other.stderr) return false

    return true
  }

  final override fun hashCode(): Int {
    var result = command.hashCode()
    result = 31 * result + (status?.hashCode() ?: -1)
    result = 31 * result + stdout.hashCode()
    result = 31 * result + stderr.hashCode()
    return result
  }
}

sealed class Complete : ProcessResult() {
  abstract override val status: ProcessStatus

  companion object {
    operator fun invoke(
      command: Command,
      status: ProcessStatus,
      stdout: String,
      stderr: String,
      combinedOutput: String
    ): Complete = if (status.isSuccess) {
      Success(command, stdout, stderr, combinedOutput)
    } else {
      Failure(command, status, stdout, stderr, combinedOutput)
    }
  }
}

class Success(
  override val command: Command,
  override val stdout: String,
  override val stderr: String,
  override val output: String
) : Complete() {

  override val status: ProcessStatus = SUCCESS
  override val isSuccess: Boolean = true

  override fun toString(): String =
    "Success(command='$command', output='$output')"
}

class Failure(
  override val command: Command,
  override val status: ProcessStatus,
  override val stdout: String,
  override val stderr: String,
  override val output: String
) : Complete() {
  init {
    require(status != SUCCESS) { "status [$status] must not be $SUCCESS" }
  }

  override val isSuccess: Boolean = false

  override fun toString(): String =
    "Failure(command='$command', status=$status, output='$output')"
}

class Incomplete(
  override val command: Command,
  override val stdout: String,
  override val stderr: String,
  override val output: String
) : ProcessResult() {

  override val status: ProcessStatus? = null

  override val isSuccess: Boolean = false

  override fun toString(): String =
    "Incomplete(command='$command', output='$output')"
}
