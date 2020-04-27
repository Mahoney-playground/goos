#!/usr/bin/env bash

set -euo pipefail

Xvfb :99 -ac &
XVFBPID=$!
# TODO work out how to wait for SIGUSR1 from Xvfb

clean_up() {
  if kill -0 $XVFBPID 2>/dev/null; then
    kill $XVFBPID
  fi
  rm -rf /tmp/.X11-unix/X99
  rm -rf /tmp/.X99-lock
}

trap clean_up EXIT

set +e
DISPLAY=:99 "$@"
RETVAL=$?
set -e

exit $RETVAL
