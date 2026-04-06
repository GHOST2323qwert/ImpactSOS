#!/bin/sh
DIR="$(cd "$(dirname "$0")" && pwd)"
sh "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
