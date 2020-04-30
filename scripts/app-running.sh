#!/usr/bin/env bash

set -euo pipefail

makeSession() {
  curl -fsS -X POST -H 'Content-Type: application/json' -D - -d '{"desiredCapabilities": {}}' localhost:1234/session | grep 'Location:' | cut -d' ' -f2 | tr -d '\r\n'
}

checkAppHasActiveElements() {
  local sessionPath=$1
  curl -fsS -X POST "$sessionPath/element/active" | grep '"status":0'
}

checkAppHasActiveElements $(makeSession)
