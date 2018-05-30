#!/bin/bash

# WARNING: Makes crazy zombie processes if you kill it

TIME_LIMIT=300 # seconds

./gradlew compileJava

for (( i=0; i<10; i++)); do
    ./run-task-5-test-run-one.sh $TIME_LIMIT 5 100000
    RESULT=$?

    if [[ $RESULT -eq 0 ]]; then
        echo "*** Test $i PASSED! ***"
    else
        echo "*** Test $i FAILED ***"
        echo "*** TEST FAILED ***"
        echo "*** exiting... ***"
        exit 1
    fi

done

echo "*** TEST PASSED ***"
