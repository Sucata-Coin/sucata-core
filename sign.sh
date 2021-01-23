#!/bin/sh
java -cp "classes:lib/*:conf" sbr.tools.SignTransactionJSON $@
exit $?
