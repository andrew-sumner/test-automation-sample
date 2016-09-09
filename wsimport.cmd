@ECHO OFF
SETLOCAL

GOTO :DO_IMPORT

#################################################################################################################################################################################

OVERVIEW
========
Importing a WSDL is easiest if done directly from a URL, unfortunately these are often behind an https connection and require a certificate to reach them.  The good news is that
if your browser can reach the WSDL then you can easily obtain the certificate and add it to Java's keystore.  If you don't wish to do that then you will have to download the WSLD 
file (and anything it points at) and run the necessary commands manually. 
 
IMPORTING THE SECURITY CERTIFICATE
==================================

The following instructions:
 * are based on FireFox but can also be done with IE
 * assume you are using Java installed at C:\Program Files\Java\jdk1.8.0_60
 * are based on this article: http://magicmonster.com/kb/prg/java/ssl/pkix_path_building_failed.html. 

1. First try the importing a wsdl using this utility to confirm you need to import the security certificate!

2. Export the security certificate 
  2.1 Open Fox and navigate to https://bpmprocesscenter.ssi.govt.nz/teamworks/webservices/MSD_WD/ManageIncomeBPMService.tws?WSDL
  2.2 Click on lock icon to left of url
  2.3 Click 'right arrow' on popup menu
  2.4 Click 'More Information' 
  2.5 Click 'Security' tab
  2.6 Click 'View Certificate' button
  2.7 Click 'Details' tab
  2.8 Click 'Export...' button
  2.9 Choose the "X.509 Certificate (DER)" type, so the exported file has a der extension and save the file to C:\NotBackedUp (or you preferred location)
 
3. Make a copy of the "C:\Program Files\Java\jdk1.8.0_60\jre\lib\security\cacerts" folder, just in case...

4. From the command line run:
  4.1 C:\NotBackedUp>"C:\Program Files\Java\jdk1.8.0_60\bin\keytool" -import -alias example -keystore  "C:/Program Files/Java/jdk1.8.0_60/jre/lib/security/cacerts" -file MSDIntranetIssuingCA1.der
  4.2 Enter keystore password: changeit
  4.3 Trust this certificate? [no]:  yes
  
  And hopefully you will get the message "Certificate was added to keystore"

#################################################################################################################################################################################

:DO_IMPORT

CLS

REM WHEN ADDING NEW IMPORT, ENSURE THAT YOU:
REM 1. ADD ENTRY BEFORE [X]Cancel
REM 2. UPDATE THE CHOICE OPTION AT THE CORRECT POSITION
REM 3. CHECK THAT CANCEL OPTION STILL WORKS

@echo.
@echo Import the following service:
@echo   [M]anageIncomeBPMService
@echo   [H]ardshipApplicationWS
@echo   [P]rocessMedCertsService
@echo   [O]nlineApplications
@echo   O[D]M
@echo.
@echo   [U]pdate All Services
@echo   [X]Cancel
@echo.
@echo Note: this will remove anything in the src\generated\java folder
@echo.

CHOICE /C MHPODUX /N /M Choice?


SET OPTION=%ERRORLEVEL%

IF %OPTION% EQU 1 GOTO :MANAGE_INCOME
IF %OPTION% EQU 2 GOTO :HARDSHIP
IF %OPTION% EQU 3 GOTO :MEDCERTS
IF %OPTION% EQU 4 GOTO :ONLINEAPP
IF %OPTION% EQU 5 GOTO :ODM
IF %OPTION% EQU 6 GOTO :ALL


GOTO :EOF

:ALL
call :eraseExisting
SET ALL=yes

:MANAGE_INCOME
CALL :importWSDL MSD_WD/ManageIncomeBPMService.tws?WSDL
IF ERRORlEVEL 1 GOTO :EOF
IF NOT DEFINED ALL GOTO :EOF

:HARDSHIP
CALL :importWSDL MSD_HA/HardshipApplicationWS.tws?WSDL
IF ERRORlEVEL 1 GOTO :EOF
IF NOT DEFINED ALL GOTO :EOF

:MEDCERTS 
CALL :importWSDL MSD_MC/ProcessMedCertsService.tws?WSDL
IF ERRORlEVEL 1 GOTO :EOF
IF NOT DEFINED ALL GOTO :EOF

:ONLINEAPP
CALL :importWSDL MSD_OG/OnlineApplication_WS.tws?WSDL
IF ERRORlEVEL 1 GOTO :EOF
IF NOT DEFINED ALL GOTO :EOF

:ODM
ECHO.
ECHO ===============================================================================================================================
ECHO Importing WSDL http://trivs258.ssi.govt.nz:9081/DecisionService/ws/CIC_RuleApp/CIC_Rules/v75?WSDL...
"C:\Program Files\Java\jdk1.8.0_60\bin\wsimport.exe" -d src/generated/java -keep -verbose -Xnocompile http://trivs258.ssi.govt.nz:9081/DecisionService/ws/CIC_RuleApp/CIC_Rules/v75?WSDL 
IF ERRORlEVEL 1 GOTO :EOF
IF NOT DEFINED ALL GOTO :EOF


GOTO :EOF
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:importWSDL
ECHO.

IF NOT DEFINED ALL call :eraseExisting

ECHO.
ECHO ===============================================================================================================================
ECHO Importing WSDL https://bpmprocesscenter.ssi.govt.nz/teamworks/webservices/%1...
"C:\Program Files\Java\jdk1.8.0_60\bin\wsimport.exe" -d src/generated/java -keep -verbose -Xnocompile https://bpmprocesscenter.ssi.govt.nz/teamworks/webservices/%1
IF ERRORlEVEL 1 GOTO :EOF

GOTO :EOF

:eraseExisting
echo Erase existing files...
IF EXIST src\generated\java rd src\generated\java /S /Q
md src\generated\java