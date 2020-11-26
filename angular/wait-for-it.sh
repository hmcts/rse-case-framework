#!/bin/sh

COMMAND="curl -f $1 > /dev/null 2>&1"
TIMEOUT=${2:-600}

if [ "$COMMAND" == "" ]; then
  echo "You should provide command to wait for."
  exit 1
fi

echo "Waiting ${TIMEOUT}s for command: $COMMAND"

for i in `seq $TIMEOUT` ; do
  eval "$COMMAND"
  if [ $? -eq 0 ]; then
    exit 0
  fi
  sleep 1
done

echo "Timeout ${TIMEOUT}s exceeded."
exit 1

