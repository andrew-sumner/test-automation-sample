# Eclipse Setup

1. Make Directory
	Can put this where ever you like, this is where I keep it...

	D:\Java
	D:\Java\workspace
	D:\Java\gradle_user_home
	
2. Set Envrionment variables
	SETX GRADLE_USER_HOME D:\Java\gradle_user_home

3. Install Java 1.8.60 JDK from Setup\Java folder - if you don't already have a 1.8 JDK

4. Extract eclipse-jee-mars-2-win32.zip to D:\Java\

5. Install FireFox and set network connection to automatic

6. Configure Eclipse
	Edit eclipse.ini (goes under the Eclipse root directory):
	* Set path to java (MSD machines defaulting to 1.6 and this version requires minimum of 1.7), goes at top of file
	-vm
	C:/Program Files/Java/jdk1.8.0_60/bin/javaw.exe
                                
	* Place these under -vmargs. Allows downloading files via proxy, can't remember why need user home set but do
	-Dorg.eclipse.ecf.provider.filetransfer.excludeContributors=org.eclipse.ecf.provider.filetransfer.httpclient4
	-Duser.home=C:\Users\<your username>

	Now open Eclipse

	Open Window > Preferences > General > Network Connections
	Add proxy settings (all manual for http and https): 
		host: nwsa001
		port: 8080
		authentication: required
		proxy bypass: nexus.ssi.govt.nz

	Installed Plugins (open Help > Eclipse Market Place):
		buildship (gradle)
		subclipse (subversion)
		Checkstyle  
			- might require a manual install, see: http://eclipse-cs.sourceforge.net/#!/install
			- plugin has been downloaded to net.sf.eclipsecs-updatesite_6.16.0.201603042325.zip
		Findbugs		

		* To get Eclipse to not flag the @SuppressWarnings("checkstyle:...") annotation, look under the menu headings
		Window -> Preferences -> Java -> Compiler -> Errors/Warnings -> Annotations -> Unhandled Token in '@SuppressWarnings' and set it to ignore.



	Markdown Support
	================
	Markdown support is very poor compared to IntelliJ IDEA.
	
	There is a GitHub Flavoured Markdown Viewer: https://github.com/satyagraha/gfm_viewer
	But I couldn't install it via MarketPlace, in the end I had to do via add new software pointing at at specific version: http://dl.bintray.com/satyagraha/generic/1.8.3 

8. Setup project
	Import SVN Project, either through Tortoise SVN or via Eclipse.

		Eclipse Instructions:
		--------------------
		1. File -> Import -> SVN -> Checkout Projects from SVN
		2. Assuming never checked out this project before select "Create a new repository location" (and click next)
		3. Enter URL: http://subversion.ssi.govt.nz/repos/bpm_change_circumstances/test-automation-declarewages (and click next)
		4. Select project root folder (and click next)
		5. Select "Check out as a project in the workspace" (and click next)
		6. Leave "Use default worpsace location" selected, location should be D:/Java/workspace (and click finish)
		7. When project has finished downloading it will be automatically imported into the Eclipse:
			right click on the project -> select delete -> ensure "Delete project contents on disk" is NOT selected -> click ok
		8. Now do Import GRADLE Project instructions


		Import GRADLE Project:
		---------------------
		1. File -> Import -> Gradle -> Gradle Project
		2. If you get the Gradle welcome screen click next
		3. Enter "project root directory": D:\Java\workspace\test-automation-declarewages (and click next)
		4. Ideally you'd use the gralde wrapper but having issues bootstrapping gradle through the proxy so:
			select "Local installation directory": D:\Java\gradle-2.11 (and click next)
		5. Verify the following settings:
			* Project root directory: 	D:\Java\workspace\test-automation-declarewages
			* Gradle user home directory: 	D:\Java\gradle_user_home
			* Gradle version: 		2.11
			* Java home directory:		C:\Program Files\Java\jdk1.8.0_60
			(and click finish)
		6. Project will load and download all project dependencies
		7. If you get prompted to overwrite existing project descriptors click overwrite - these got created when eclipse first imported the project

		CheckStyle:
		----------
		Right click on project -> Properties -> Checkstyle - Check "Checkstyle active for this project" -> ok - yes
		
		Code Formatter:
		---------------
		1. Window -> Preferences -> Java -> Code Style -> Formatter -> Import... 
		2. Select file "formatter-cyf-msd-5.xml" from project root
		3. Ensure it's the active profile
		4. Click OK button
		
		To format the code manually:
		* On Windows press: Ctrl + Shift + F
		* On Mac press: Command + Shift + F
		* In the main menu: Source -> Format
		
		To format the code automatically on save:
		* Window -> Preferences -> Java -> Editor -> SaveActions -> Check "Perform the selected actions on save" -> Check "Format source code" -> Select "Format edited lines"

	If using Firefox Portable you will need to update the config.properties file to supply the path using <username>.webdriver.firefox.exe setting.
	Also advice setting <username>.webdriver.firefox.activatePlugins = true


	
	
	
======================================================================================================================================================================================
== APPENDIX 1: SETTING UP GRADLE WRAPPER TO WORK BEHIND FIREWALL
======================================================================================================================================================================================

1. CREATE gradle.properties FILE to allow gradle to get through the proxy

C:\Java\gradle_user_home\gradle.properties
systemProp.http.proxyHost=nwsa001
systemProp.http.proxyPort=8080
#systemProp.http.proxyUser=XXXXX
#systemProp.http.proxyPassword=XXXXX
systemProp.http.nonProxyHosts=nexus.ssi.govt.nz|localhost|127.0.0.1

systemProp.https.proxyHost=nwsa001
systemProp.https.proxyPort=8080
#systemProp.https.proxyUser=XXXXX
#systemProp.https.proxyPassword=XXXXX
systemProp.https.nonProxyHosts=nexus.ssi.govt.nz|localhost|127.0.0.1

2. EDIT gradle-wrapper.properties FILE to download via http rather than https to allow gradle wrapper to bootstrap gradle

C:\Java\workspace\<project>\gradle\wrapper
#distributionUrl=https\://services.gradle.org/distributions/gradle-2.12-bin.zip
distributionUrl=http\://services.gradle.org/distributions/gradle-2.12-bin.zip


3. EDIT build.gradle FILE 

if your build.gradle file contains a buildscript section you'll probably need to get that to download directly from repository via http rather than https

buildscript {
  repositories {
        //jcenter()
        jcenter {
		    url "http://jcenter.bintray.com/"
		}
   }
   ...
}

your standard repository list should point at nexus/artificatory although you can set this up as above

repositories {
    //jcenter()
    maven {
		url "http://nexus.ssi.govt.nz/content/groups/public"
	}
}