;This file will be executed next to the application bundle image
;I.e. current directory will contain folder PAWS with application files
[Setup]
AppId={{fxApplication}}
AppName=Asheninka Syllable Parser
AppVersion=0.8.0.0
AppVerName=Asheninka Syllable Parser version 0.8.0.0
AppPublisher=SIL International
AppComments=Asheninka Syllable Parser
AppCopyright=Copyright ﾩ 2016-2021 SIL International
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\Asheninka Syllable Parser
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=No
DisableFinishedPage=No
DisableWelcomePage=No
DefaultGroupName=SIL International
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=Asheninka Syllable Parser-0.8.0.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=Asheninka Syllable Parser\Asheninka Syllable Parser.ico
UninstallDisplayIcon={app}\Asheninka Syllable Parser.ico
UninstallDisplayName=Asheninka Syllable Parser version 0.8.0.0
WizardImageStretch=No
WizardSmallImageFile=Asheninka Syllable Parser-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64
ChangesAssociations=yes

[Registry]
Root: HKCR; Subkey: ".ashedata"; ValueType: string; ValueName: ""; ValueData: "AsheninkaFile"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Mime\Database\Content Type\text/x-asheninka"; ValueType: string; ValueName: "Extension"; ValueData: ".ashedata"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "AsheninkaFile"; ValueType: string; ValueName: ""; ValueData: "Asheninka file"; Flags: uninsdeletekey
Root: HKCR; Subkey: "AsheninkaFile\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\Asheninka Syllable Parser"" ""%1"""

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "Asheninka Syllable Parser\Asheninka Syllable Parser.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "Asheninka Syllable Parser\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Asheninka Syllable Parser"; Filename: "{app}\Asheninka Syllable Parser.exe"; IconFilename: "{app}\Asheninka Syllable Parser.ico"; Check: returnTrue()
Name: "{commondesktop}\Asheninka Syllable Parser"; Filename: "{app}\Asheninka Syllable Parser.exe";  IconFilename: "{app}\Asheninka Syllable Parser.ico"; Check: returnTrue()

[Run]
Filename: "{app}\Asheninka Syllable Parser.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\Asheninka Syllable Parser.exe"; Description: "{cm:LaunchProgram,Asheninka Syllable Parser}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\Asheninka Syllable Parser.exe"; Parameters: "-install -svcName ""Asheninka Syllable Parser"" -svcDesc ""A tool for exploring syllabification algorithms and for inserting discretionay hyphens in word lists."" -mainExe ""Asheninka Syllable Parser.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\Asheninka Syllable Parser.exe "; Parameters: "-uninstall -svcName ""Asheninka Syllable Parser"" -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
	begin
	  Result := True;
	end;

function returnFalse(): Boolean;
	begin
	  Result := False;
	end;

function InitializeSetup(): Boolean;
	begin
	// Possible future improvements:
	//   if version less or same => just launch app
	//   if upgrade => check if same app is running and wait for it to exit
	//   Add pack200/unpack200 support? 
	  Result := True;
	end;
