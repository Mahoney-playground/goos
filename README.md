To build:
```bash
DOCKER_BUILDKIT=1 \
docker build .
```

To run the end to end tests:
```bash
COMPOSE_DOCKER_CLI_BUILD=1 DOCKER_BUILDKIT=1 \
GOOS_TAG=latest \
docker-compose \
 -f end-to-end-tests/docker-compose.yml \
 up \
 --build \
 --exit-code-from end-to-end-tests --abort-on-container-exit
```

```plantuml
digraph Test {
  libraries -> ports
  libraries -> adapters
  libraries -> core
  ports -> adapters
  ports -> core
  adapters -> app
  core -> app

  libraries -> fakes
  libraries -> testdrivers
  libraries -> "end to end tests"
  fakes -> "end to end tests"
  testdrivers -> "end to end tests"
  
  app -> "end to end test run"
  "end to end tests" -> "end to end test run"
  "xmpp server" -> "end to end test run"
}
```
