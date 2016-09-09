package nz.govt.msd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads and supplies properties from the config.properties file that are required by the framework.
 * 
 * This class can be extended by an AppConfig class to provide application specific properties.
 * 
 * @author Andrew Sumner
 */
public class Config {
	/** Name of the default property file "config.properties". */
	protected static final String CONFIG_FILE = "config.properties";
	/** Name of the environment. */
	private static String environment = null;
	
    // Browser 
	private static String browserName;
	private static String browserSize;
	private static int browserDefaultTimeout;
    
	private static String localBrowserExe;
	private static boolean activatePlugins;
	private static String remoteUserName;
	private static String remoteApiKey;

	// Proxy
	private static boolean proxyIsRequired;
	private static String proxyHost;
	private static int proxyPort;
	private static String proxyDomain;
	private static String proxyUsername;
	private static String proxyPassword;
	
	/** Ensure properties have been loaded before any property is used. */ 
	static {
		synchronized (Config.class) {
			loadProperties();
		}
	}
	
	/** Prevent this class from being constructed. */
	protected Config() { }
	
	private static void loadProperties() {
		Properties prop = loadFile(CONFIG_FILE); 

		loadCommonProperties(prop);
	}
	
	/** @return Configured environment. */
	public static String getEnvironment() {
		return environment;
	}
	
	/** 
	 * Read properties from file, will ignoring the case of properties.
	 * 
	 * @param filename Name of file to read, expected that it will be located in the projects root folder
	 * @return {@link CaselessProperties}
	 */
	protected static Properties loadFile(final String filename) {
		Properties prop = new CaselessProperties();
		
		if (!new File(filename).exists()) {
			return prop;
		}

		try (InputStream input = new FileInputStream(filename);) {
			prop.load(input);
		} catch (Exception e) {
			throw new RuntimeException("Unable to read properties file.", e);
		}

		return prop;
	}
	
	private static void loadCommonProperties(Properties prop) {
		// Jenkins might supply value
		environment = System.getProperty("environment", "").toLowerCase();

		if (environment.isEmpty()) {
			environment = getProperty(prop, "environment");
		}

		// Browser
		browserName = System.getProperty("browser");
		if (browserName == null) {
			browserName = getProperty(prop, "webdriver.browser");
		}
    	
		browserDefaultTimeout = Integer.parseInt(getProperty(prop, "webdriver.defaultTimeout"));
		browserSize = getOptionalProperty(prop, "webdriver.browserSize");
		
		if (useLocalBrowser()) {
			localBrowserExe = getOptionalProperty(prop, "webdriver." + browserName + ".exe");
			activatePlugins = Boolean.valueOf(getOptionalProperty(prop, "webdriver." + browserName + ".activatePlugins"));
		}
		
		remoteUserName = getOptionalProperty(prop, "remotewebdriver.userName");
		remoteApiKey = getOptionalProperty(prop, "remotewebdriver.apiKey");
		
		// Yandex HtmlElements automatically implement 5 second implicit wait, default to zero so as not to interfere with 
		// explicit waits
		System.setProperty("webdriver.timeouts.implicitlywait", getOptionalProperty(prop, "webdriver.timeouts.implicitlywait", "0"));
		
		// Proxy
		proxyIsRequired = Boolean.parseBoolean(getProperty(prop, "proxy.required"));
		proxyHost = getProperty(prop, "proxy.host");
		proxyPort = Integer.parseInt(getProperty(prop, "proxy.port"));

		prop = loadFile("user.properties"); 

		proxyDomain = getOptionalProperty(prop, "proxy.domain");
		proxyUsername = getOptionalProperty(prop, "proxy.username");
		proxyPassword = getOptionalProperty(prop, "proxy.password");
	}
    
    /**
	 * Get the property for the current environment, if that is not found it will look for "default.{@literal <key>}".
	 *
	 * @param properties	A set of properties
	 * @param key	Id of the property to look up
	 * @return 		Property value if found, throws exception if not found 
	 */
	protected static String getProperty(Properties properties, String key) {
		String value = retrieveProperty(properties, key);
    	
		if (value.isEmpty()) {
			throw new RuntimeException(String.format("Unable to find property %s", key));
		}
		
		return value;
	}
    
    /**
	 * Get the property for the current environment, if that is not found it will look for "default.{@literal <key>}".
	 *
	 * @param properties	A set of properties
	 * @param key	Id of the property to look up
	 * @return 		Property value if found, empty string if not found 
	 */
	protected static String getOptionalProperty(Properties properties, String key) {
		return retrieveProperty(properties, key);
	}

    /**
	 * Get the property for the current environment, if that is not found it will look for "default.{@literal <key>}".
	 *
	 * @param properties	A set of properties
	 * @param key			Id of the property to look up
	 * @param defaultValue	value to use if property is not found
	 * @return 		Property value if found, defaultValue if not found 
	 */
	protected static String getOptionalProperty(Properties properties, String key, String defaultValue) {
		String value = retrieveProperty(properties, key);
    	
		if (value.isEmpty()) {
			return defaultValue;
		}
		
		return value;
	}

	private static String retrieveProperty(Properties properties, String key) {
		String prefix = null;
		String value = null;

		// Get setting if set for user
		prefix = System.getProperty("user.name").toLowerCase();
		value = properties.getProperty(prefix + "." + key);
		
		// Get setting if set for environment
		if (value == null && environment != null) {
			prefix = environment;
			value = properties.getProperty(prefix + "." + key);
		}

		// Get default setting
		if (value == null) {
			value = properties.getProperty(key);
		}
		
		if (value != null) {
			value = value.trim();
		} else {
			value = "";
		}
		
		return value;
	}
    
	// Browser
	private static boolean useLocalBrowser() {
		return !browserName.contains(" ");
	}

	public static String getBrowser() {
		return browserName;
	}

	/**
	 * Useful if local browser is not available on path.
	 *
	 * @return Path to browser executable
	 */
	public static String getBrowserExe() {
		if (localBrowserExe != null && !localBrowserExe.isEmpty()) {
			return localBrowserExe.replace("%USERPROFILE%", System.getProperty("USERPROFILE", ""));
		}
		
		return "";
	}
	
	/**
	 * Activate developer plugins - FireFox only browser supported currently and will add FireBug and FirePath.
	 * 
	 * @return true or false
	 */
	public static boolean activatePlugins() {
		return activatePlugins;
	}
	
	/**
	 * Size to set browser window - will default to maximised.
	 * 
	 * @return Size in wxh format
	 */
	public static String getBrowserSize() {
		return browserSize;
	}
	
	/**
	 * Default timeout in seconds.
	 * 
	 * @return timeout
	 */
	public static int getDefaultTimeout() {
		return browserDefaultTimeout;
	}

	/**
	 * Username for remote selenium grid service.
	 * 
	 * @return Username
	 */
	public static String getRemoteUserName() {
		return remoteUserName;
	}

	/**
	 * Api Key to access a remote selenium grid service.
	 * 
	 * @return Api Key
	 */
	public static String getRemoteApiKey() {
		return remoteApiKey;
	}
    
	/**
	 * Proxy should be setup or not.
	 * 
	 * @return true or false
	 */
	public static boolean isProxyRequired() {
		return proxyIsRequired;
	}

	/**
	 * Proxy host name.
	 * 
	 * @return host
	 */
	public static String getProxyHost() {
		return proxyHost;
	}
	
	/**
	 * Proxy port number.
	 * 
	 * @return port
	 */
	public static int getProxyPort() {
		return proxyPort;
	}
	
	/** 
	 * Proxy user's domain.
	 * 
	 * @return domain
	 */
	public static String getProxyDomain() {
		if (proxyDomain == null) {
			throw new RuntimeException("proxy.domain entry must exist in the user.properties file in the root folder");
		}
		
		return proxyDomain;
	}

	/**
	 * Proxy username.
	 * 
	 * @return username
	 */
	public static String getProxyUser() {
		if (proxyUsername == null) {
			throw new RuntimeException("proxy.username entry must exist in the user.properties file in the root folder");
		}
		
		return proxyUsername;
	}
	
	/**
	 * Proxy user.
	 * 
	 * @return user
	 */
	public static String getProxyPassword() {
		if (proxyPassword == null) {
			throw new RuntimeException("proxy.proxypassword entry must exist in the user.properties file in the root folder");
		}
		
		return proxyPassword;
	}

	/**
	 * @return Proxy bypass (noproxy) addresses, eg: "localhost, 127.0.0.1".
	 */
	public static String getNoProxyList() {
		return "localhost, 127.0.0.1";
	}

}