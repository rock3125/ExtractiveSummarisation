#!/bin/bash

if [ "$JAVA_HOME" == "" ]; then
  echo "JAVA_HOME not set"
  exit 1
fi

HOME=`dirname "$0"`

CP=`echo $HOME/lib/*.jar | tr ' ' ':'`
$JAVA_HOME/bin/java -cp $CP summarisation.Main "$@"
