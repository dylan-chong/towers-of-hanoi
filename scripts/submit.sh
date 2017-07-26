#!/bin/bash

echo
echo '*** Cleaning ***'
./gradlew clean

echo
echo '*** Creating App Jar ***'
./gradlew _submit

echo
echo '*** Creating App Test Jar ***'
TEST=true ./gradlew _submit
