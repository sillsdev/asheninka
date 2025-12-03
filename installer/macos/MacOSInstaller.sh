#! /bin/bash
if [ -d installtemp ]; then
 echo "	removing installtemp"
 rm -rf installtemp
fi
echo "	invoking jpackage, part 2"
# 	--verbose \
jpackage --type dmg \
	--copyright "2021-2025 SIL International" \
	--description "Asheninka Syllable Parser" \
	--name Asheninka \
	--resource-dir input/resources \
	--app-image output/Asheninka.app \
    --mac-package-name "Syllable Parser" \
	--license-file License.txt \
	--icon input/Asheninka.icns \
	--temp installtemp \
	--app-version $1 \
	--file-associations asheninka.properties \
	--vendor "SIL International"

