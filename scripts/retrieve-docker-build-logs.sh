#!/usr/bin/env bash

set -exuo pipefail

main() {
  local build_image=$1
  local build_identifier=$2

  local container_id
  container_id=$(docker create "$build_image:$build_identifier" ignored)

  mkdir -p "builds/$build_identifier"

  set +e
  docker cp "$container_id:/build-reports" "builds/$build_identifier"
  set -e
}

main "$1" "$2"
