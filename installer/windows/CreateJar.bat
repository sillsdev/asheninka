@echo off
cd ..\..\bin
REM jar cmf META-INF\MANIFEST.MF pcpatreditor.jar .
jar --create --file asheninka.jar --main-class org.sil.syllableparser.MainApp .
copy asheninka.jar ..\installer\windows\input > nul
del asheninka.jar > nul
cd ..\installer\windows

