@echo off

REM Libraries
REM echo java_path=%1
echo 	Libraries
if not exist input\libs mkdir input\libs
del /q input\libs\*
copy ..\..\libs\ANTLR\* input\libs > nul
copy ..\..\libs\ControlsFX\* input\libs > nul
copy ..\..\libs\diff-match-patch\* input\libs > nul
copy ..\..\libs\ICU\*  input\libs > nul
copy ..\..\libs\JAXB input\libs > nul
copy ..\..\libs\jna input\libs > nul
copy ..\..\libs\json input\libs > nul
copy ..\..\libs\LibJavaDev input\libs > nul
copy ..\..\libs\lingtree\* input\libs > nul
copy ..\..\libs\Richtextfx input\libs > nul

REM Documentation
echo 	Documentation
if not exist input\doc mkdir input\doc
del /q input\doc\*
copy ..\..\doc\*.pdf input\doc > nul

REM Resources
echo 	Resources
if not exist input\resources mkdir input\resources
del /s /q input\resources\* > nul
xcopy ..\..\src\org\sil\syllableparser\resources input\resources /E > nul
copy Asheninka.ico input > nul

REM Jar file
echo 	Create Jar file
call CreateJar.bat %1
