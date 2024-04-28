#!/usr/bin/env bash

x=$(dd if=/dev/urandom bs=16 count=4)
echo "$x"
