#!/bin/sh
if [ -x jre/bin/java ]; then
    JAVA=./jre/bin/java
else
    JAVA=java
fi
${JAVA} -cp classes:lib/*:conf:addons/classes:addons/lib/* -Dsbr.runtime.mode=desktop -Dsbr.runtime.dirProvider=sbr.env.DefaultDirProvider sbr.Sbr
