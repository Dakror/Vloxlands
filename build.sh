#!/usr/bin/env bash

set -xv

export TERM="dumb"

chmod +x ./gradlew

./gradlew desktop:dist