package goos.testcontainers

import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Path

@Suppress("DEPRECATION") // This is just a way to run on an ad hoc basis
fun openfireTestContainer(
  pathToOpenfireDockerImage: Path,
): FixedHostPortGenericContainer = FixedHostPortGenericContainer(
  ImageFromDockerfile()
    .withFileFromPath(".", pathToOpenfireDockerImage),
).apply {
  withFixedExposedPort(5222, 5222)
  withFixedExposedPort(9090, 9090)
  withCreateContainerCmdModifier { it.withHostName("auctionhost.internal") }
  waitingFor(Wait.forHealthcheck())
}
