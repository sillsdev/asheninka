#! /usr/bin/bash

cd ../../bin
# jar cmf META-INF/MANIFEST.MF asheninka.jar .
jar --create --file asheninka.jar --main-class org.sil.syllableparser.MainApp .
cp asheninka.jar ../installer/linux/input > nul
rm asheninka.jar > nul
cd ../installer/linux
