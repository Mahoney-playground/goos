#!/usr/bin/env bash

set -exuo pipefail

function main() {
  local tmpdir=$1
  local cache_tar=$2

  ls -lh "$cache_tar"
  time sudo service docker stop
  # mv is c. 25 seconds faster than rm -rf here
  time sudo mv /var/lib/docker "$tmpdir/olddocker"
  sudo mkdir -p /var/lib/docker
  time sudo tar -xf "$cache_tar" -C /var/lib/docker
  time sudo service docker start
}

main "$@"
