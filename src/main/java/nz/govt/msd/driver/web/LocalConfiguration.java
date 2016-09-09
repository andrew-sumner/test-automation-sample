package nz.govt.msd.driver.web;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import nz.govt.msd.driver.web.RemoteConfiguration.RemoteType;
import nz.govt.msd.utils.Config;

/**
 * Provides everything required to start up a local desktop browser, currently supports chrome, ie and firefox 
 *
 * Updated drivers can be downloaded from: http://www.seleniumhq.org/download/ and placed in the libs folder.
 * 
 * @author Andrew Sumner
 */
public class LocalConfiguration implements BrowserConfiguration {
	private String browser;
	private String browserSize;
	private boolean maximised;
	
	private LocalConfiguration () {
	}

    /** @return A new Selenium WebDriver based on supplied configuration */
	@Override
	public WebDriver createDriver() {
		WebDriver driver;
		
		// use web driver as specified in config.properties
		switch (browser.toLowerCase()) {
			case "chrome":
				driver = createChromeDriver();
				break;
				
			case "ie":
			case "internetexplorer":
				if (isWindows64Bit()) {
					driver = createInternetExplorerDriver("64");
				} else {
					driver = createInternetExplorerDriver("32");
				}
				break;
				
			case "ie32":
			case "internetexplorer32":
				driver = createInternetExplorerDriver("32");
				break;
				
			case "ie64":
			case "internetexplorer64":
				driver = createInternetExplorerDriver("64");
				break;
				
			case "firefox":
				driver = createFireFoxDriver();
				break;
	            
			default:
				throw new RuntimeException("Browser '" + browser + "' is not currently supported");
		}
		
		if (isViewPortDefined()) {
			driver.manage().window().setSize(new Dimension(getViewPortWidth(), getViewPortHeight()));
		} else if (maximised) {
			driver.manage().window().maximize();
		}
		
		return driver;
	}
	
	private boolean isWindows64Bit() {
		boolean is64bit = false;
		
		if (System.getProperty("os.name").contains("Windows")) {
			is64bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
			is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
		}
		
		return is64bit;
	}

	/*
	 * For running portable firefox at same time as desktop version:
	 * 		1. Edit FirefoxPortable.ini (next to FirefoxPortable.exe)
	 * 		2. If its not there then copy from "Other/Source" folder
	 * 		3. Change AllowMultipleInstances=false to true
	 */
	private WebDriver createFireFoxDriver() {
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		
		addProxyCapabilities(capabilities);

		if (!Config.getBrowserExe().isEmpty()) {
			capabilities.setCapability(FirefoxDriver.BINARY, Config.getBrowserExe());
		}
		
		if (Config.activatePlugins()) {
			FirefoxProfile profile = new FirefoxProfile();
			
			try {
				File firebug = Plugins.get("firebug");
				profile.addExtension(firebug);
				
				String version = firebug.getName();
				version = version.substring(version.indexOf("-") + 1);
				version = version.substring(0, version.indexOf("-") > 0 ? version.indexOf("-") : version.indexOf("."));
				
				profile.setPreference("extensions.firebug.currentVersion", version);
				
				profile.addExtension(Plugins.get("firepath"));
			} catch (IOException e) {
				throw new RuntimeException("Unable to add FireFox plugins", e);
			}
			
			capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		}
		
		// System.setProperty("webdriver.gecko.driver", new File("libs/geckodriver.exe").getAbsolutePath());
		// capabilities.setCapability("marionette", true);
		// return new MarionetteDriver(capabilities);
		return new FirefoxDriver(capabilities);
	}

	private WebDriver createChromeDriver() {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		System.setProperty("webdriver.chrome.driver", new File("libs/chromedriver.exe").getAbsolutePath());

		addProxyCapabilities(capabilities);
		
		if (!Config.getBrowserExe().isEmpty()) {
			ChromeOptions options = new ChromeOptions();
			options.setBinary(Config.getBrowserExe());
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		}
		
		return new ChromeDriver(capabilities);
	}

	// NOTE: Further config required to use this, see: https://code.google.com/p/selenium/wiki/InternetExplorerDriver 
	private WebDriver createInternetExplorerDriver(String bitVersion) {
		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		
		System.setProperty("webdriver.ie.driver", new File("libs/IEDriverServer" + bitVersion + ".exe").getAbsolutePath());

		// capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		// "ignore", "accept", or "dismiss".
		// capabilities.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, "dismiss");

		// capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		  
		addProxyCapabilities(capabilities);
		
		return new InternetExplorerDriver(capabilities);
	}

	private void addProxyCapabilities(DesiredCapabilities capabilities) {
		if (!Config.isProxyRequired()) {
			return;
		}
			
		String browserProxy = Config.getProxyHost() + ":" + Config.getProxyPort();
		String browserNoProxyList = Config.getNoProxyList();

		final org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
		proxy.setProxyType(org.openqa.selenium.Proxy.ProxyType.MANUAL);
		proxy.setHttpProxy(browserProxy);
		proxy.setFtpProxy(browserProxy);
		proxy.setSslProxy(browserProxy);
		proxy.setNoProxy(browserNoProxyList);
		
		capabilities.setCapability(CapabilityType.PROXY, proxy);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	}
	

	public boolean isViewPortDefined() {
		return browserSize != null && !browserSize.isEmpty(); 
	}


	@Override
	public String getViewPort() {
		return browserSize;
	}
	
	@Override
	public int getViewPortWidth() {
		if (browserSize == null || browserSize.isEmpty()) {
			return -1;
		}

		String width = browserSize.substring(0, browserSize.indexOf("x")).trim(); 

		return Integer.parseInt(width);
	}

	@Override
	public int getViewPortHeight() {
		if (browserSize == null || browserSize.isEmpty()) {
			return -1;
		}

		String height = browserSize.substring(browserSize.indexOf("x") + 1).trim();

		return Integer.parseInt(height);
	}


	@Override
	public RemoteType getDeviceType() {
		return RemoteType.DESKTOP;
	}
	
	@Override
	public String getDeviceName() {
		return "Desktop";
	}

	@Override
	public String getBrowser() {
		return browser;
	}
	
	/**
	 * Browser selected in configuration file is supported by this class.
	 * 
	 * @return true or false
	 */
	public static boolean configuredBrowserIsLocal() {
		switch (Config.getBrowser().toLowerCase()) {
			case "chrome":
			case "ie":
			case "internetexplorer":
			case "ie32":
			case "internetexplorer32":
			case "ie64":
			case "internetexplorer64":
			case "firefox":
				return true;
	            
			default:
				return false;
		}
	}

	/**
	 * @return The browser selected in the configuration file.
	 */
	public static LocalConfiguration getBrowserConfiguration() {
		LocalConfiguration config = new LocalConfiguration();

		config.browser = Config.getBrowser();
		config.browserSize = Config.getBrowserSize();
		config.maximised = true;

		return config;
	}

	/**
	 * FireFox browser.
	 * @param browserSize Dimensions to set browser to in format WxH
	 * @return FireFox configuration.
	 */
	public static LocalConfiguration firefox(String browserSize) {
		LocalConfiguration config = new LocalConfiguration();
		
		config.browser = "Firefox";
		config.browserSize = browserSize;
		config.maximised = false;
		
		return config;
	}
	
	/**
	 * Helper for finding Browser plug-ins stored in the libs folder..
	 */
	private static class Plugins {
		public static File get(final String pluginName) {
			File search = new File("libs");
			
			String[] files = search.list(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.contains(pluginName);
				}
			});
			
			if (files != null && files.length > 0) {
				return new File(search, files[0]);
			}
			
			return null;
		}
	}
}
