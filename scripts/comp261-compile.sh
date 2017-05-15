#!/bin/bash


cd ../src/
rm -rf comp-pre-compile/src/*
#cp main/java/**/* comp-pre-compile/src/.

find main/java/ -type f -exec cp {} comp-pre-compile/src \;
#find main/java/ -type f -print0 | xargs -0 cp -t /

for file in comp-pre-compile/src/*
do
  echo $file
  sed -r -i 's/(^package comp261.+?$)|(^import comp261.+?$)//g' $file
done