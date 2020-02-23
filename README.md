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
