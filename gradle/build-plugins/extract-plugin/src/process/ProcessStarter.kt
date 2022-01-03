package uk.org.lidalia.gradle.plugins.extractplugin.process

import java.nio.file.Path

interface ProcessStarter {
  fun run(
    command: Command,
    dir: Path = Path.of("."),
    env: Map<String, String> = emptyMap(),
    outStream: Appendable = System.out,
    errStream: Appendable = System.err,
  ): Process
}
