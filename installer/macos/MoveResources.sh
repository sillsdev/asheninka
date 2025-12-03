#! /bin/bash
# we run from bin and the executable looks here for the resources
#chmod +x input/resources/Keyboards/macOS/xkbswitch
mkdir -p output/Asheninka/resources
cp -r input/resources output/Asheninka/ > /dev/null
#chmod +x output/Asheninka/resources/Keyboards/macOS/xkbswitch
cp -r input/doc output/Asheninka > /dev/null
