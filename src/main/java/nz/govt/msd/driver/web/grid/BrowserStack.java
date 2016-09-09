package nz.govt.msd.driver.web.grid;

import java.io.IOException;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionId;

import com.google.gson.JsonElement;

import nz.govt.msd.driver.http.HttpEasy;
import nz.govt.msd.driver.http.JsonReader;
import nz.govt.msd.driver.web.BrowserConfiguration;
import nz.govt.msd.driver.web.RemoteConfiguration;
import nz.govt.msd.utils.Config;

/**
 * BrowserStack selenium grid provider.
 * 
 * <p>Browser and device options: https://www.browserstack.com/automate/java</p> 
 */
public class BrowserStack extends RemoteConfiguration {	
	private BrowserStack(RemoteType remoteType, String browser, String viewPort, DesiredCapabilities capabilites) {
		super(remoteType, browser, viewPort, capabilites);
	}
	
	private static final String REMOTE_URL = "http://[USER_NAME]:[API_KEY]@hub.browserstack.com/wd/hub";
	private static final String TYPE = "application/json";

	@Override
	protected String getRemoteDriverUrl() {
		return REMOTE_URL.replace("[USER_NAME]", Config.getRemoteUserName()).replace("[API_KEY]", Config.getRemoteApiKey());
	}
	
	@Override
	public SessionDetails getSessionDetails(SessionId sessionId) throws IOException {	
		JsonElement value;
		String url = "https://www.browserstack.com/automate/sessions/" + sessionId + ".json";

		JsonReader json = HttpEasy.request().
				path(url).
				authorization(Config.getRemoteUserName(), Config.getRemoteApiKey()).
				header("Accept", TYPE).header("Content-type", TYPE).
				get().
				getJsonReader();

		SessionDetails details = new SessionDetails();

		details.setProviderName("BrowserStack");
		
		value = json.jsonPath("automation_session.browser_url");
		details.setBrowserUrl((value == null ? "" : value.getAsString())); 

		value = json.jsonPath("automation_session.video_url");
		details.setVideoUrl(value == null ? "" : value.getAsString());

		return details;
	}
	
	/**
	 * @return Browser configuration for browser specified in the configuration file.
	 */
	public static BrowserConfiguration getBrowserConfiguration() {
		String browser = Config.getBrowser();
		if (browser == null) {
			browser = "";
		}
		
		// check desktop browsers
		String[] browserDetails = browser.split(" ");
		
		if (browserDetails.length == 2) {
			switch (browserDetails[0]) {
				case "chrome":
					// eg 46.0
					return BrowserStack.chrome(browserDetails[1]);
					
				case "internetExplorer":
				case "ie":
					// eg 11.0
					return internetExplorer(browserDetails[1]);
			        
				case "firefox":
					// eg 43.0
					return firefox(browserDetails[1]); 
					
				case "safari":
					// eg 9.0
					return safari(browserDetails[1]); 
					
				default:
					break;
			}
		}
		 
		// check devices
		switch (browser) {
			case "iphone 6s plus":
				return iPhone6SPlusEmulator();
				
			case "google nexus 5":
				return googleNexus5Emulator();
				
			default:
				break;
		}
		
		throw new RuntimeException("Browser '" + browser + "' is not currently supported");
	}
	
	private static BrowserStack desktop(DesiredCapabilities caps, String browserVersion) {
		String browserName = caps.getCapability("browser").toString();
		
		caps.setCapability("browser_version", browserVersion);
		caps.setCapability("resolution", DEFAULT_DESKTOP_SCREENSIZE);
		
		return new BrowserStack(RemoteType.DESKTOP, browserName, DEFAULT_DESKTOP_VIEWPORT, caps);
	}
	
	/**
	 * FireFox browser.
	 * @param browserVersion Version of the browser
	 * @return Configuration required to start this browser on BrowserStack.
	 */
	public static BrowserStack firefox(String browserVersion) {
		DesiredCapabilities caps = new DesiredCapabilities();
		
		caps.setCapability("browser", "Firefox");
		caps.setCapability("os", "Windows");
		caps.setCapability("os_version", "10");
				
		return desktop(caps, browserVersion);
	}
	
	/**
	 * Chrome browser.
	 * @param browserVersion Version of the browser
	 * @return Configuration required to start this browser on BrowserStack.
	 */
	public static BrowserStack chrome(String browserVersion) {
		DesiredCapabilities caps = new DesiredCapabilities();

		caps.setCapability("browser", "Chrome");
		caps.setCapability("os", "Windows");
		caps.setCapability("os_version", "10");	
		
		return desktop(caps, browserVersion);
	}
	
	/**
	 * Internet Explorer browser.
	 * @param browserVersion Version of the browser
	 * @return Configuration required to start this browser on BrowserStack.
	 */
	public static BrowserStack internetExplorer(String browserVersion) {
		DesiredCapabilities caps = new DesiredCapabilities();
		
		caps.setCapability("browser", "IE");
		caps.setCapability("os", "Windows");
		caps.setCapability("os_version", "10");		
		
		return desktop(caps, browserVersion);
	}
	
	/**
	 * Safari browser.
	 * @param browserVersion Version of the browser
	 * @return Configuration required to start this browser on BrowserStack.
	 */
	public static BrowserStack safari(String browserVersion) {
		DesiredCapabilities caps = new DesiredCapabilities();

		caps.setCapability("browser", "Safari");
		caps.setCapability("os", "OS X");
		caps.setCapability("os_version", "El Capitan");
		
		return desktop(caps, browserVersion);
	}
	
	/**
	 * @return Configuration required to start this device on BrowserStack.
	 */
	public static BrowserStack samsungGalaxyS5Emulator() {
		DesiredCapabilities caps = new DesiredCapabilities();
		
		caps.setCapability("browserName", "android");
		caps.setCapability("platform", "ANDROID");
		caps.setCapability("device", "Samsung Galaxy S5");
		
		return new BrowserStack(RemoteType.DEVICE, "Samsung Galaxy S5", "1080x1920", caps);
	}
	
	/**
	 * @return Configuration required to start this device on BrowserStack.
	 */
	public static BrowserStack iPhone6SPlusEmulator() {
		DesiredCapabilities caps = new DesiredCapabilities();
		
		caps.setCapability("browserName", "iPhone");
		caps.setCapability("platform", "MAC");
		caps.setCapability("device", "iPhone 6S Plus");
		
		return new BrowserStack(RemoteType.DEVICE, "iPhone 6S Plus", "?x?", caps);
	}
	
	/**
	 * @return Configuration required to start this device on BrowserStack.
	 */
	public static BrowserStack googleNexus5Emulator() {
		DesiredCapabilities caps = new DesiredCapabilities();

		caps.setCapability("browserName", "android");
		caps.setCapability("platform", "ANDROID");
		caps.setCapability("device", "Google Nexus 5");
		
		return new BrowserStack(RemoteType.DEVICE, "Google Nexus 5", "1080x1920", caps);
	}
}