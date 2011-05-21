#!/bin/bash
#For TLN use only
#Execute this shell script from the build directory to build and deploy the new client

ant clean
ant standalone-release
ant web-release
cp -v dist/webstart/* /Users/naps/Sites/code
cp -v dist/standalone/jhave.jar /Users/naps/Sites/code

 
