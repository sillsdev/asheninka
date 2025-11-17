#! /usr/bin/bash
if [ -d output ]; then
 echo "	Cleaning output"
 rm -rf output > /dev/null
fi
echo "	invoking jpackage, part 1"
# to see more details, add the --verbose option
#	--verbose \
jpackage --type app-image \
	--input input \
	--dest output \
	--name Asheninka \
	--main-jar asheninka.jar \
	--main-class org.sil.syllableparser.MainApp \
	--icon input/Asheninka.png \
	--module-path jmods \
	--vendor "SIL International"
echo "	MoveResources"
./MoveResources.sh

