java -cp classes sbr.tools.ManifestGenerator
del sbr.jar
jar cfm sbr.jar resource\sbr.manifest.mf -C classes .
del sbrservice.jar
jar cfm sbrservice.jar resource\sbrservice.manifest.mf -C classes .

echo "jar files generated successfully"