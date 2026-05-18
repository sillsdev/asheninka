@echo off
if exist output rmdir output /S /q
REM if exist apptemp rmdir apptemp /S /q
echo 	invoking jpackage, pass 1
REM use --verbose to see more
jpackage --type app-image ^
	--input input ^
	--dest output ^
	--name Asheninka ^
	--main-jar asheninka.jar ^
	--main-class org.sil.syllableparser.MainApp ^
	--add-modules java.rmi,jdk.management.jfr,jdk.jdi,jfx.incubator.input,java.xml,jdk.xml.dom,java.datatransfer,jdk.httpserver,javafx.base,java.desktop,java.security.sasl,jdk.zipfs,java.base,jfx.incubator.richtext,jdk.javadoc,jdk.management.agent,jdk.jshell,javafx.swing,jdk.sctp,java.sql.rowset,jdk.jsobject,java.smartcardio,jdk.unsupported,java.security.jgss,jdk.nio.mapmode,java.compiler,javafx.graphics,jdk.dynalink,javafx.fxml,jdk.unsupported.desktop,javafx.media,jdk.accessibility,jdk.security.jgss,java.sql,jdk.incubator.vector,javafx.web,java.transaction.xa,java.xml.crypto,java.logging,jdk.jfr,jdk.internal.md,jdk.net,java.naming,javafx.controls,jdk.internal.ed,java.prefs,java.net.http,jdk.compiler,jdk.internal.opt,jdk.jconsole,jdk.attach,jdk.internal.le,java.management,jdk.jdwp.agent,jdk.internal.jvmstat,java.instrument,jdk.management,jdk.security.auth,java.scripting,com.azul.tooling,jdk.jartool,java.management.rmi,jdk.localedata ^
	--jlink-options "--include-locales=en,fr,es" ^
	--icon input/Asheninka.ico ^
	--module-path jmods ^
	--vendor "SIL International"
echo 	MoveResources
call MoveResources.bat
