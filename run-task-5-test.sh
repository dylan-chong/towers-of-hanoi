#!/bin/bash

# WARNING: Makes crazy zombie processes if you kill it

./gradlew compileJava
cd build/classes/java/main/

RESULTS=../../../../results.tmp

run() {
    CLIENTS=$1
    CHUNKSIZE=$2

    MSG="Running for CHUNKSIZE=$CHUNKSIZE CLIENTS=$CLIENTS :"
    echo $MSG >> $RESULTS
    echo $MSG

    for (( i=0; i<$CLIENTS; i++)); do
        java Task5Client localhost 57843 $CHUNKSIZE 2> /dev/null & 
    done

    echo "Processes (in case you need to kill them)" 
    jobs -p

    java Task5KeyManager 3184309670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng== 57843 \
        | grep Results \
        >> $RESULTS
}

for (( i=0; i<10; i++)); do
    bash -c 'ulimit -t 1; run 5 100000'
    RESULT=$?

    jobs -p | xargs kill -9 # Kill clients

    if [[ $RESULT == 0 ]]; then
        echo "*** Test $i PASSED! ***"
    else
        echo "*** Test $i FAILED ***"
        echo "*** TEST FAILED ***"
        echo "*** exiting... ***"
        exit 1
    fi

done

echo "*** TEST PASSED ***"
