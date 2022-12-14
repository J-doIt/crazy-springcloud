#!/bin/bash
echo 'start server'
export JAVA_HOME=/usr/local/java/jdk1.8.0_11
export PATH=.:$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
java -jar  /cloud-zuul-1.0-SNAPSHOT.jar