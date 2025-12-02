#! /usr/bin/bash
if [ -d installtemp ]; then
 echo "	removing installtemp"
 rm -rf installtemp
fi
echo "	invoking jpackage, part 2"
VERSION=$1
# 	--verbose \
#	--linux-shortcut \
#ls -l -R
jpackage --type deb \
	--verbose \
	--copyright "2021-2025 SIL International" \
	--description "Asheninka Syllable Parser" \
	--name Asheninka \
	--install-dir /opt/sil \
	--resource-dir jpackageResources \
	--app-image output/Asheninka \
	--linux-menu-group "Education" \
	--license-file License.txt \
	--icon input/Asheninka.png \
	--temp installtemp \
	--app-version $1 \
	--file-associations asheninka.properties \
	--vendor "SIL International"
# ./FixDesktopShortcutInDebFile.sh $VERSION
