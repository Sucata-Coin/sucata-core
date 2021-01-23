#!/bin/sh
CP="conf/;classes/;lib/*;testlib/*"
SP="src/java/;test/java/"
TESTS="sbr.crypto.Curve25519Test sbr.crypto.ReedSolomonTest"

/bin/rm -f SucataCoin.jar
/bin/rm -rf classes
/bin/mkdir -p classes/

javac -encoding utf8 -sourcepath $SP -classpath $CP -d classes/ src/java/sbr/*.java src/java/sbr/*/*.java test/java/sbr/*/*.java || exit 1

java -classpath $CP org.junit.runner.JUnitCore $TESTS

