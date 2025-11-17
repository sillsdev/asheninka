@echo off
if exist installtemp rmdir installtemp /S /q
echo 	invoking jpackage, pass 2
jpackage --type exe ^
	--copyright "2017-2025 SIL International" ^
	--description "Asheninka Syllable Parser" ^
	--name Asheninka ^
	--install-dir "SIL\Asheninka" ^
	--resource-dir input/resources ^
	--app-image output/Asheninka ^
	--win-menu ^
	--win-shortcut ^
	--license-file License.txt ^
	--icon input/Asheninka.ico ^
	--win-upgrade-uuid 1BDC5198-5C0B-4BE0-ACC4-DF4DDC7029DC ^
	--temp installtemp ^
	--app-version %1 ^
	--file-associations asheninka.properties ^
	--vendor "SIL International" 
