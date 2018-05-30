#!/bin/bash

# Do not run this file. run ./run-task-5-test.sh instread

TIME_LIMIT=$1
CLIENTS=$2
CHUNKSIZE=$3

cd build/classes/java/main/

RESULTS=../../../../results.tmp

MSG="Running for CHUNKSIZE=$CHUNKSIZE CLIENTS=$CLIENTS :"
echo $MSG >> $RESULTS
echo $MSG

for (( i=0; i<$CLIENTS; i++)); do
    java Task5Client localhost 57843 $CHUNKSIZE 2> /dev/null &
done

echo "Processes (in case you need to kill them)"
jobs -p

java Task5KeyManager 3184309670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng== 57843 false $TIME_LIMIT \
    | grep Results \
    >> $RESULTS
RESULT=${PIPESTATUS[0]}

jobs -p | xargs kill -9

exit $RESULT
