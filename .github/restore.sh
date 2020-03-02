#!/usr/bin/env bash

set -exuo pipefail

function main() {
  local event_name=$1
  local tmpdir=$2

  local cache_tar="$tmpdir/docker_cache/cache.tar"

  if [[ -f "$cache_tar" && $event_name != schedule ]]; then
    ls -lh "$cache_tar"
    time sudo service docker stop
    # mv is c. 25 seconds faster than rm -rf here
    time sudo mv /var/lib/docker "$tmpdir/olddocker"
    sudo mkdir -p /var/lib/docker
    time sudo tar -xf "$cache_tar" -C /var/lib/docker
    time sudo service docker start
  else
    docker system prune -a -f --volumes
  fi
}

main "$@"
