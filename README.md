To build:

```bash
DOCKER_BUILDKIT=1 \
docker build .
```

To build locally:

```bash
./gradlew build
```

The output will be in `build/goos`.

To run the end-to-end tests:

```bash
COMPOSE_DOCKER_CLI_BUILD=1 DOCKER_BUILDKIT=1 \
GOOS_TAG=latest \
docker-compose \
 -f end-to-end-tests/docker-compose.yml \
 up \
 --build \
 --exit-code-from end-to-end-tests --abort-on-container-exit
```

To run the end-to-end tests locally:

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

You should now be able to run the tests locally.

## Architecture

The basic architecture is ports-and-adapters, or hexagonal. The core logic is I/O free, and
interacts with the world via ports, fulfilled by injected adapters.

Each port should have a stub that runs in memory. It should also have a set of contract tests that
can be run against any adapter implementation of the port, including the stub.

### Module structure

The app has the following modules:

- `app`  
  The app module contains the main method that:
    - instantiates the desired adapters for the ports the core depends on
    - instantiates the core domain passing it the adapters
    - runs the core logic
- `core`  
  The core module contains the domain logic of the app. It does no I/O; instead it depends on ports
  that do all the I/O. These are passed in when the core logic is instantiated - this module is not
  responsible for instantiating them and has no knowledge of the specific adapter implementation it
  runs against.
- multiple ports, of the form:
    - `{port}/api`
        - `src`
          Defines the interface(s) and data classes (logic free structs or DTOs) that make up the
          API of a port
        - `testFixtures`
          Defines:
            - abstract contract tests for the API. They accept an instance of the port (interface),
              and so can be implemented for any adapter by injecting an instance of that adapter
              into them. They are written using:
            - an interface describing a driver for the API. An implementation of this will allow
              interacting with the API in ways the core application does not need to in order to
              facilitate testing. For instance, the UI driver will allow interaction with the UI
              from the user's perspective.
    - `{port}/stub`
        - `src`
          This is a purely in-memory, fake implementation of the port. It allows any tests of code
          that needs to interact with this port to run very fast, without doing any I/O.
        - `testFixtures`
          This contains a stub implementation of the port driver defined
          in `{port}/api/testFixtures`. It allows any tests that need to interact with this port to
          run very fast, without doing any I/O.
        - `tests`
          This contains an implementation of the contract tests defined in `{port}/api/testFixtures`
          , proving that the stub implementation fulfils that contract.
    - `{port}/{adapter}`
        - `src`
          This is a working implementation of the port defined in `{port}/api/src`
        - `testFixtures`
          This contains a working implementation of the port driver defined in
          `{port}/api/testFixtures`. It allows any tests that need to interact with this port to do
          so.
        - `tests`
          This contains an implementation of the contract test defined in `{port}/api/testFixtures`,
          proving that the adapter implementation fulfils that contract.

          This is an odd one - it's highly likely to involve I/O, so the contract tests in here
          should *not* run as part of the basic build. They should be run as specific extra steps.
- `end-to-end-tests`
    - `src`
      Defines abstract contract tests for the whole application. These interact with the various
      ports using the drivers defined in `{port}/api/testFixtures`. Implementations are responsible
      for initialising the app with the corresponding adapters and starting it.
    - `tests`
      This contains an implementation of the abstract contract tests defined in
      `end-to-end-tests/src` constructing the app with the adapters defined in `{port}/stub/src`
      and the tests with the drivers defined in `{port}/stub/testFixtures` that can be run as part
      of the main build. There would typically be at least two implementations:
        - a real one, constructing the app with the adapters defined in `{port}/{adapter}/src` and
          the tests with the drivers defined in `{port}/{adapter}/testFixtures`

```plantuml
@startuml

component app
component core
component "port-contract-test" as port_contract_test
interface "port-test-driver" as port_test_driver
component adapter
component "stub-adapter" as stub_adapter
component "stub-contract-test" as stub_contract_test
component "stub-test-driver" as stub_test_driver
component "end-to-end-contract-test" as end_to_end_contract_test
component "end-to-end-stubbed-test" as end_to_end_stubbed_test
interface port

app --> core
app --> adapter
core --> port
adapter --> port

stub_adapter --> port

port_contract_test --> port
port_contract_test --> port_test_driver

stub_test_driver --> port_test_driver
stub_contract_test --> port_contract_test
stub_contract_test --> stub_test_driver
stub_contract_test --> stub_adapter

end_to_end_contract_test --> port_test_driver
end_to_end_contract_test --> port

end_to_end_stubbed_test --> end_to_end_contract_test
end_to_end_stubbed_test --> stub_test_driver
end_to_end_stubbed_test --> core
end_to_end_stubbed_test --> stub_adapter
@enduml
```

```plantuml
@startuml

component app
component core
component adapter
component "adapter-contract-test" as adapter_contract_test
component "end-to-end-real-test" as end_to_end_real_test
component "adapter-test-driver" as adapter_test_driver
component "end-to-end-contract-test" as end_to_end_contract_test
component "port-contract-test" as port_contract_test
interface "port-test-driver" as port_test_driver
interface port

app --> core
app --> adapter
core --> port
adapter --> port

port_contract_test --> port
port_contract_test --> port_test_driver

adapter_test_driver --> port_test_driver
adapter_contract_test --> port_contract_test
adapter_contract_test --> adapter_test_driver
adapter_contract_test --> adapter

end_to_end_contract_test --> port_test_driver
end_to_end_contract_test --> port

end_to_end_real_test --> end_to_end_contract_test
end_to_end_real_test --> adapter_test_driver
end_to_end_real_test --> app
@enduml
```
