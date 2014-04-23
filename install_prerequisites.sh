#!/usr/bin/env bash

# Set verbose and xtrace on to give the user a better idea of what is happening when the script runs.
set -xv

# Assume that we are using a dumb terminal (produces cleaner output on build servers).
export TERM="dumb"

# If possible, update the system and install any necessary packages.
if hash apt-get 2>/dev/null; then

	apt-get update -qq
	if [ `uname -m` = x86_64 ]; then
		apt-get install -qq --force-yes libgd2-xpm ia32-libs ia32-libs-multiarch > /dev/null
	fi

fi

# If the Android SDK is not installed then download and install it.
if [ -z "${ANDROID_HOME}" ]; then

	# Set the version of the Android SDK to install.
	android_sdk_version="22.2.1"

	# Download the Android SDK.
	wget --quiet "http://dl.google.com/android/android-sdk_r${android_sdk_version}-linux.tgz"

	# Install the Android SDK to /opt.
	tar -C /opt -xzf "android-sdk_r${android_sdk_version}-linux.tgz"
	export ANDROID_HOME="/opt/android-sdk-linux"

fi

# Add the Android SDK tools to the PATH.
export PATH="${PATH}:${ANDROID_HOME}/tools"

# Install/update the required Android SDK components.
echo yes | android update sdk -a --filter "platform-tools,build-tools-17.0.0,android-17" --no-ui --force > /dev/null
