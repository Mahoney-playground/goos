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

To run the end to end tests locally:
* Install dnsmasq: `brew install dnsmasq`
* Ensure it resolves *.internal to localhost:
  ```bash
  echo address=/internal/127.0.0.1 >> /usr/local/etc/dnsmasq.conf
  echo 'nameserver 127.0.0.1' | sudo tee /etc/resolver/internal > /dev/null
  sudo dscacheutil -flushcache
  sudo killall -HUP mDNSResponder
  ping -c1 foo.internal
* Run up the required docker containers:
  ```bash
  DOCKER_BUILDKIT=1 docker build -t goos-instrumentedapp . && docker run -p 1234:1234 goos-instrumentedapp
  cd docker-openfire && docker build -t openfire . && docker run -p 5222:5222 -p 9090:9090 -h auctionhost.internal openfire 
  ```
  ```
You should now be able to run the tests locally.
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
