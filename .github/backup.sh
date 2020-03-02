#!/usr/bin/env bash

set -exuo pipefail

function main {
  local tmpdir=$1

  local me
  me=$(whoami)
  local my_group
  my_group=$(id -g -n "$me")

  local cache_dir="$tmpdir/docker_cache"
  local cache_tar="$cache_dir/cache.tar"
  mkdir -p "$cache_dir"
  rm -f "$cache_tar"

  time sudo service docker stop
  time sudo /bin/tar -c -f "$cache_tar" -C /var/lib/docker .
  sudo chown "$me:$my_group" "$cache_tar"
  ls -lh "$cache_tar"
}

main "$@"
