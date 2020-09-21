#!/bin/bash

CLASSPATH="target/dataset-1.0-SNAPSHOT.jar"
JVM_ARGS=""

java -cp $CLASSPATH -Djava.util.logging.SimpleFormatter.format='[%1$tF %1$tT] [%4$-7s] %5$s %n'\
 org.examples.Main $1 $2 $3



