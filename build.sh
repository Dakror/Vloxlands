#!/usr/bin/env bash

# Set verbose and xtrace on to give the user a better idea of what is happening when the script runs.
set -xv

# Assume that we are using a dumb terminal (produces cleaner output on build servers).
export TERM="dumb"

# If ANDROID_HOME is not set then assume it is /opt.
if [ -z "${ANDROID_HOME}" ]; then
	export ANDROID_HOME="/opt/android-sdk-linux"
fi

# Make sure that the ANDROID_HOME is valid.
if [ ! -x "${ANDROID_HOME}/tools/android" ]; then
	echo " ==> ERROR: Unable to locate Android SDK." >&2
	exit 1
fi

# Add the Android SDK tools to the PATH.
export PATH="${PATH}:${ANDROID_HOME}/tools"

./gradlew desktop:dist
./gradlew android:assembleRelease
