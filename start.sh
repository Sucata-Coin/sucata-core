#!/bin/sh
APPLICATION="SucataCoin"
if [ -e ~/.${APPLICATION}/sbr.pid ]; then
    PID=`cat ~/.${APPLICATION}/sbr.pid`
    ps -p $PID > /dev/null
    STATUS=$?
    if [ $STATUS -eq 0 ]; then
        echo "Sbr server already running"
        exit 1
    fi
fi
mkdir -p ~/.${APPLICATION}/
DIR=`dirname "$0"`
cd "${DIR}"
if [ -x jre/bin/java ]; then
    JAVA=./jre/bin/java
else
    JAVA=java
fi
nohup ${JAVA} -cp classes:lib/*:conf:addons/classes:addons/lib/* -Dsbr.runtime.mode=desktop sbr.Sbr > /dev/null 2>&1 &
echo $! > ~/.${APPLICATION}/sbr.pid
cd - > /dev/null
