package uk.org.lidalia.gradle.plugins.extractplugin.process

import java.nio.file.Path

object JavaProcessStarter : ProcessStarter {
  override fun run(
    command: Command,
    dir: Path,
    env: Map<String, String>,
    outStream: Appendable,
    errStream: Appendable
  ): Process = when (command) {
    is Exec -> command.run(dir, env, outStream, errStream)
    is Shell -> command.run(dir, env, outStream, errStream)
    is Pipe -> command.run(dir, env, outStream, errStream)
  }
}

private fun Command.run(
  processBuilder: ProcessBuilder,
  dir: Path,
  env: Map<String, String>,
  outStream: Appendable,
  errStream: Appendable,
): Process {
  processBuilder.environment().putAll(env)
  processBuilder.directory(dir.toFile())
  return JavaProcess(
    processBuilder,
    this,
    outStream,
    errStream,
  )
}

private fun Exec.run(
  dir: Path,
  env: Map<String, String>,
  outStream: Appendable,
  errStream: Appendable,
) = run(
  ProcessBuilder(executable, *args.toTypedArray()),
  dir,
  env,
  outStream,
  errStream,
)

private fun Shell.run(
  dir: Path,
  env: Map<String, String>,
  outStream: Appendable,
  errStream: Appendable,
) = run(
  ProcessBuilder("/usr/bin/env", "sh", "-c", command),
  dir,
  env,
  outStream,
  errStream,
)

private fun Pipe.run(
  dir: Path,
  env: Map<String, String>,
  outStream: Appendable,
  errStream: Appendable,
): JavaProcess {
  TODO("not implemented")
}
