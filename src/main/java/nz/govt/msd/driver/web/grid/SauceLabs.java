package nz.govt.msd.driver.web.grid;

import java.io.IOException;

import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionId;

import com.google.gson.JsonElement;

import nz.govt.msd.driver.http.HttpEasy;
import nz.govt.msd.driver.http.JsonReader;
import nz.govt.msd.driver.web.RemoteConfiguration;
import nz.govt.msd.utils.Config;

/**
 * BrowserStack selenium grid provider.
 * 
 * <p>Browser and device options: https://wiki.saucelabs.com/display/DOCS/Platform+Configurator</p> 
 */
public class SauceLabs extends RemoteConfiguration {
	private SauceLabs(RemoteType remoteType, String browser, String viewPort, DesiredCapabilities capabilites) {
		super(remoteType, browser, viewPort, capabilites);
	}
	
	private static final String REMOTE_URL = "http://[USER_NAME]:[API_KEY]@ondemand.saucelabs.com:80/wd/hub";
	private static final String TYPE = "application/json";
	
	@Override
	protected String getRemoteDriverUrl() {
		return REMOTE_URL.replace("[USER_NAME]", Config.getRemoteUserName()).replace("[API_KEY]", Config.getRemoteApiKey());
	}

	@Override
	public SessionDetails getSessionDetails(SessionId sessionId) throws IOException {	
		JsonElement value;
		String url = "https://saucelabs.com/rest/v1/" + Config.getRemoteUserName() + "/jobs/" + sessionId;

		JsonReader reader = HttpEasy.request()
									.path(url)
									.authorization(Config.getRemoteUserName(), Config.getRemoteApiKey())
									.header("Accept", TYPE).header("Content-type", TYPE)
									.get()
									.getJsonReader();

		SessionDetails details = new SessionDetails();
		
		details.setProviderName("SauceLabs");
		
		//TODO - remove beta when this interface becomes the default 
		details.setBrowserUrl("https://www.saucelabs.com/beta/tests/" + sessionId);
			
		value = reader.jsonPath("video_url");
		details.setVideoUrl((value == null ? "" : value.getAsString()));
		
		return details;
	}
		
	private static SauceLabs desktop(DesiredCapabilities caps, String browserName, String browserVersion) {
		String platform = caps.getCapability(CapabilityType.PLATFORM).toString();
		
		caps.setCapability("version", browserVersion);
		caps.setCapability("screenResolution", DEFAULT_DESKTOP_SCREENSIZE);
		caps.setCapability("name", String.format("%s %s, %s", browserName, browserVersion, platform));
		
		return new SauceLabs(RemoteType.DESKTOP, browserName, DEFAULT_DESKTOP_VIEWPORT, caps);
	}
	
	private static SauceLabs desktop(DesiredCapabilities caps, String browserVersion) {
		String browserName = caps.getCapability(CapabilityType.BROWSER_NAME).toString();
		
		return desktop(caps, browserName, browserVersion);
	}
	
	/**
	 * FireFox browser.
	 * @param browserVersion Version of the browser
	 * @return Configuration required to start this browser on BrowserStack.
	 */
	public static SauceLabs firefox(String browserVersion) {
		DesiredCapabilities caps = DesiredCapabilities.firefox();
		caps.setCapability("platform", "Windows 10");
				
		return desktop(caps, browserVersion);
	}
	
	/**
	 * Chrome browser.
	 * @param browserVersion Version of the browser
	 * @return Configuration required to start this browser on BrowserStack.
	 */
	public static SauceLabs chrome(String browserVersion) {
		DesiredCapabilities caps = DesiredCapabilities.chrome();
		caps.setCapability("platform", "Windows 10");		
		
		return desktop(caps, browserVersion);
	}
	
	/**
	 * Internet Explorer browser.
	 * @param browserVersion Version of the browser
	 * @return Configuration required to start this browser on BrowserStack.
	 */
	public static SauceLabs internetExplorer(String browserVersion) {
		DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
		caps.setCapability("platform", "Windows 10");		
		
		return desktop(caps, "ie", browserVersion);
	}

	/**
	 * Safari browser.
	 * @param browserVersion Version of the browser
	 * @return Configuration required to start this browser on BrowserStack.
	 */
	public static SauceLabs safari(String browserVersion) {
		DesiredCapabilities caps = DesiredCapabilities.safari();
		caps.setCapability("platform", "OS X 10.11");
		
		return desktop(caps, browserVersion);
	}
	
	/**
	 * @return Configuration required to start this device on BrowserStack.
	 */
	public static SauceLabs googleNexus7CEmulator() {
		DesiredCapabilities caps = DesiredCapabilities.android();
		caps.setCapability("deviceName", "Google Nexus 7C Emulator");
		caps.setCapability("deviceOrientation", "portrait");
		caps.setCapability("name", "Google Nexus 7C Emulator");
		
		return new SauceLabs(RemoteType.DEVICE, "Google Nexus 7C Emulator", "?x?", caps);
	}
	
	/**
	 * @return Configuration required to start this device on BrowserStack.
	 */
	public static SauceLabs samsungGalaxyS4Emulator() {
		DesiredCapabilities caps = DesiredCapabilities.android();
		caps.setCapability("deviceName", "Samsung Galaxy S4 Emulator");
		caps.setCapability("deviceOrientation", "portrait");
		caps.setCapability("name", "Samsung Galaxy S4 Emulator");
		
		return new SauceLabs(RemoteType.DEVICE, "Samsung Galaxy S4 Emulator", "?x?", caps);
	}
	
	/**
	 * @return Configuration required to start this device on BrowserStack.
	 */
	public static SauceLabs samsungGalaxyS5() {
		DesiredCapabilities caps = new DesiredCapabilities();
		
		caps.setCapability("deviceName", "Samsung Galaxy S5 Device");
		caps.setCapability("platformName", "Android");
		caps.setCapability("platformVersion", "4.4");
		caps.setCapability("browserName", "Chrome");
		caps.setCapability("name", "Samsung Galaxy S5");
		
		return new SauceLabs(RemoteType.DEVICE, "Samsung Galaxy S5", "?x?", caps);
	}
	
	/**
	 * @return Configuration required to start this device on BrowserStack.
	 */
	public static SauceLabs iPhone6PlusEmulator() {
		DesiredCapabilities caps = DesiredCapabilities.iphone();
		caps.setCapability("platform", "OS X 10.10");
		caps.setCapability("version", "9.2");
		caps.setCapability("deviceName", "iPhone 6 Plus");
		caps.setCapability("deviceOrientation", "portrait");
		caps.setCapability("name", "iPhone 6 Plus");
		
		return new SauceLabs(RemoteType.DEVICE, "iPhone 6 Plus", "?x?", caps);		
	}
	
	/**
	 * @return Configuration required to start this device on BrowserStack.
	 */
	public static SauceLabs iPhone6() {
		DesiredCapabilities caps = new DesiredCapabilities();

		caps.setCapability("deviceName", "iPhone 6 Device");
		caps.setCapability("platformName", "iOS");
		caps.setCapability("platformVersion", "8.0");
		caps.setCapability("browserName", "Safari");
		caps.setCapability("name", "iPhone 6");
		
		return new SauceLabs(RemoteType.DEVICE, "iPhone 6", "?x?", caps);
	}
}