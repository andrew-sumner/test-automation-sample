package nz.govt.msd.driver;

import nz.govt.msd.driver.web.Browser;

/**
 * Interface that tests using the framework should implement .
 *  
 * @author Andrew Sumner
 */
public interface BrowserBasedTest {
	
	/**
	 * @return Reference to the Browser. 
	 */
	public Browser getBrowser();
}
