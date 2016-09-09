package nz.govt.msd.driver.web;

import org.openqa.selenium.WebDriver;

import nz.govt.msd.driver.web.RemoteConfiguration.RemoteType;

/**
 * Interface for information required to start a browser locally or remotely.
 * 
 * @author Andrew Sumner
 */
public interface BrowserConfiguration {
	/** @return A new Selenium WebDriver based on supplied configuration */
	public WebDriver createDriver();

	/** @return If running on a PC/MAC returns 'Desktop', otherwise returns name of the device */
	public String getDeviceName();
	
	/** @return Type of device */
	public RemoteType getDeviceType();
	
	/** @return Browser name if running a desktop browser, otherwise the name of the device */
	public String getBrowser();
	
	/** 
	 * ViewPort can mean different things on different devices:
	 * 	- Desktop using Applitools-Eyes: internal dimensions of the browser
	 *  - Desktop: external dimensions of the browser
	 *  - Device: screen resolution (information only, is not used to set browser size)
	 * @return the viewport size in format '{@literal <width>x<height>}' 
	 */
	public String getViewPort();
	
	/** @return The viewport width */
	public int getViewPortWidth();
	
	/** @return The viewport height */
	public int getViewPortHeight();
	
	/** @return The viewport has been supplied */
	public boolean isViewPortDefined();
	
}
