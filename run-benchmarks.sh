#!/bin/bash

# WARNING: Makes crazy zombie processes if you kill it

./gradlew compileJava
cd build/classes/java/main/

CLIENTS=1

for (( i=0; i<$CLIENTS; i++)); do
    while true; do java Client localhost 57843 100000 2> /dev/null; done &
done

echo "Processes (in case you need to kill them)" 
jobs -p

java KeyManagerBenchmarker 3184909670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng== 57843 \
    | grep Results \
    >> ../../../../results.tmp

jobs -p | xargs kill -9 # Kill clients
