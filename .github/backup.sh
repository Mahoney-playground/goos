#!/usr/bin/env bash

set -exuo pipefail

function main {
  local cache_tar=$1
  local cache_dir
  cache_dir=$(dirname "$cache_tar")

  local me
  me=$(whoami)
  local my_group
  my_group=$(id -g -n "$me")

  mkdir -p "$cache_dir"
  rm -f "$cache_tar"

  time sudo service docker stop
  time sudo /bin/tar -c -f "$cache_tar" -C /var/lib/docker .
  sudo chown "$me:$my_group" "$cache_tar"
  ls -lh "$cache_tar"
}

main "$@"
