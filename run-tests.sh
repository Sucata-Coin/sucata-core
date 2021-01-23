#!/bin/sh
CP=conf/:classes/:lib/*:testlib/*
SP=src/java/:test/java/

if [ $# -eq 0 ]; then
TESTS="sbr.crypto.Curve25519Test sbr.crypto.ReedSolomonTest sbr.peer.HallmarkTest sbr.TokenTest sbr.FakeForgingTest
sbr.FastForgingTest sbr.ManualForgingTest"
else
TESTS=$@
fi

/bin/rm -f SucataCoin.jar
/bin/rm -rf classes
/bin/mkdir -p classes/

javac -encoding utf8 -sourcepath ${SP} -classpath ${CP} -d classes/ src/java/sbr/*.java src/java/sbr/*/*.java test/java/sbr/*.java test/java/sbr/*/*.java || exit 1

for TEST in ${TESTS} ; do
java -classpath ${CP} org.junit.runner.JUnitCore ${TEST} ;
done



