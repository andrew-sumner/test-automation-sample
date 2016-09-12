package nz.govt.msd.driver.ui;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

import nz.govt.msd.AppConfig;
import nz.govt.msd.driver.BrowserBasedTest;

/**
 * A WebDriver Page Object corresponding to the Google Search Page.
 */
public class GoogleSearchPage extends PageObject<GoogleSearchPage> {

    @CacheLookup
    @FindBy(name = "q")
    private WebElement queryBox;

    @CacheLookup
    @FindBy(name = "btnG")
    private WebElement submitButton;

    @FindBy(className = "nonExistent")
    private WebElement nonExistentLink;
   
    /**
     * Opens the Google Search Page.
     */
    public static GoogleSearchPage open(BrowserBasedTest test) {
		test.getBrowser().getDriver().get(AppConfig.getGoogleUrl());
    	
    	return new GoogleSearchPage(test);
    }
    
    public GoogleSearchPage(BrowserBasedTest test) {
        super(test);        
    }
    
	@Override
	public ExpectedCondition<?> pageIsLoaded(Object... params) {
		return null;
	}

    /**
     * Searches for the specified string and opens the results page, waiting for the page to fully load.
     */
    public GoogleResultsPage searchFor(String query) {
        queryBox.sendKeys(query);
        queryBox.sendKeys(Keys.ESCAPE);
        String description = "Entered search text, and about to click search button";
        capturePage(description);
        submitButton.click();
        
        return newInstance(GoogleResultsPage.class);
    }
}