#!/usr/bin/env bash

set -euo pipefail
IFS=$'\n\t'

function append_config {
  local configuration_file=$1
  local config=$2
  sed -i "\|</jive>|i \ \ \ \ $config" "$configuration_file"
}

function setup_openfire {
  local configuration_file=$1
  if ! grep '<setup>true</setup>' "$configuration_file" ; then
    append_config "$configuration_file" '<connectionProvider><className>org.jivesoftware.database.EmbeddedConnectionProvider</className></connectionProvider>'
    append_config "$configuration_file" '<setup>true</setup>'
  fi
}

function run_openfire {
    java \
      -server \
      -Dlog4j.configurationFile=/usr/share/openfire/lib/log4j2.xml \
      -DopenfireHome=/usr/share/openfire \
      -Dopenfire.lib.dir=/usr/share/openfire/lib \
      -classpath /usr/share/openfire/lib/startup.jar \
      -jar /usr/share/openfire/lib/startup.jar
}

function main {
  setup_openfire /usr/share/openfire/conf/openfire.xml

  # default behaviour is to launch openfire
  if [ $# -eq 0 ]; then
    run_openfire
  else
    exec "$@"
  fi
}

main "$@"
