#!/bin/sh
APPLICATION="SucataCoin"
java -cp classes sbr.tools.ManifestGenerator
/bin/rm -f ${APPLICATION}.jar
jar cfm ${APPLICATION}.jar resource/sbr.manifest.mf -C classes . || exit 1
/bin/rm -f ${APPLICATION}service.jar
jar cfm ${APPLICATION}service.jar resource/sbrservice.manifest.mf -C classes . || exit 1

echo "jar files generated successfully"