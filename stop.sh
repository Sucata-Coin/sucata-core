#!/bin/sh
APPLICATION="SucataCoin"
if [ -e ~/.${APPLICATION}/sbr.pid ]; then
    PID=`cat ~/.${APPLICATION}/sbr.pid`
    ps -p $PID > /dev/null
    STATUS=$?
    echo "stopping"
    while [ $STATUS -eq 0 ]; do
        kill `cat ~/.${APPLICATION}/sbr.pid` > /dev/null
        sleep 5
        ps -p $PID > /dev/null
        STATUS=$?
    done
    rm -f ~/.${APPLICATION}/sbr.pid
    echo "Sbr server stopped"
fi

