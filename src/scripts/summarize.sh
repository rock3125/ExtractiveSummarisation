#!/bin/bash

if [ "$JAVA_HOME" == "" ]; then
  echo "JAVA_HOME not set"
  exit 1
fi

HOME=`dirname "$0"`

CP=`echo $HOME/lib/*.jar | tr ' ' ':'`
$JAVA_HOME/bin/java --add-modules java.se \
                    --add-exports java.base/jdk.internal.ref=ALL-UNNAMED \
                    --add-opens java.base/java.io=ALL-UNNAMED \
                    --add-opens java.base/java.lang=ALL-UNNAMED \
                    --add-opens java.base/java.nio=ALL-UNNAMED \
                    --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
                    --add-opens java.management/sun.management=ALL-UNNAMED \
                    --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED \
                    -XX:+ExitOnOutOfMemoryError -cp $CP summarisation.MainKt "$@"
