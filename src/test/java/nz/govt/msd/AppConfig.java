package nz.govt.msd;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.govt.msd.utils.Config;

public class AppConfig extends Config {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

	private static String googleUrl;

	static {
		synchronized (AppConfig.class) {
			loadProperties();
		}
	}

	private AppConfig() { }

	public static void logSettings() {
		LOGGER.info("Environment:        " + getEnvironment());
		LOGGER.info("url:                " + googleUrl);
		LOGGER.info("Browser:            " + getBrowser());

		if (!getBrowserSize().isEmpty()) {
			LOGGER.info("browserSize:        " + getBrowserSize());
		}
	}

	private static void loadProperties() {
		Properties prop = loadFile(CONFIG_FILE);

		googleUrl = getProperty(prop, "googleUrl");
	}

	// Application specific properties
	public static String getGoogleUrl() {
		return googleUrl;
	}

	public static String getDatabaseUrl() {
		throw new RuntimeException("Not imeplemented");
	}

	public static String getDatabaseSchema() {
		throw new RuntimeException("Not imeplemented");
	}
}

