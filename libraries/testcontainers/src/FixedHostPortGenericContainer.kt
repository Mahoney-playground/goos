package goos.testcontainers

import org.testcontainers.containers.GenericContainer
import java.util.concurrent.Future

class FixedHostPortGenericContainer(
  image: Future<String>,
) : GenericContainer<FixedHostPortGenericContainer>(image) {
  fun withFixedExposedPort(hostPort: Int, containerPort: Int): FixedHostPortGenericContainer {
    super.addFixedExposedPort(hostPort, containerPort)
    return this
  }
}
