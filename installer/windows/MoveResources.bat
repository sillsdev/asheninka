@echo off
REM Work-around to get exe to find resource and doc files
xcopy output\Asheninka\app\resources output\Asheninka\resources /E/s/i > nul
rmdir output\Asheninka\app\resources /S /q > nul
xcopy output\Asheninka\app\doc output\Asheninka\doc /E/s/i > nul
rmdir output\Asheninka\app\doc /S /q > nul
copy LTBatch.bat output\Asheninka\app /Y  > nul
REM does not do the copy:
REM copy %JAVA_HOME%\bin\java.exe output\Asheninka /B /Y > nul
