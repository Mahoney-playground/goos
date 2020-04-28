#!/usr/bin/env bash

set -euo pipefail

wait_for_xvfb_to_be_ready() {
  local display=$1
  while [ ! -e "/tmp/.X11-unix/X$display" ]; do
    sleep 0.1
  done
}

clean_up_process() {
  local pid=$1
  kill "$pid" 2>/dev/null || :
  wait "$pid" || :
}

main() {
  local display=99

  Xvfb ":$display" -ac &

  # shellcheck disable=SC2064
  trap "clean_up_process $!" EXIT

  wait_for_xvfb_to_be_ready $display

  DISPLAY=":$display" "$@"
}

main "$@"
