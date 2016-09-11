package nz.govt.msd.driver.ui;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.govt.msd.driver.BrowserBasedTest;

/**
 * A WebDriver Page Object corresponding to the Google Results Page.
 */
public class GoogleResultsPage extends PageObject<GoogleResultsPage> {

    @CacheLookup
    @FindBy(id = "res")
    private WebElement resultWrapper;

    @CacheLookup
    @FindBy(className = "l")
    private WebElement firstResultLink;

    @CacheLookup
    @FindBy(id = "cwos")
    private WebElement calcResultLink;

    private static Logger logger = LoggerFactory.getLogger(GoogleResultsPage.class.getName());

    /**
     * Initialises the results page and waits for the page to fully load. Assumes that the results page is already
     * loading.
     */
    public GoogleResultsPage(BrowserBasedTest test) {
    	super(test);
    }


	@Override
	public ExpectedCondition<?> pageIsLoaded(Object... params) {
		return ExpectedConditions.visibilityOfElementLocated(By.id("foot"));
	}
	
    /**
     * Checks whether the specified text occurs in any result on the results page.
     */
    public boolean resultsContain(String text) {
        List<WebElement> resultsText = resultWrapper.findElements(By.className("s"));
        for (WebElement result : resultsText) {
            if (result.getText().contains(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the text of the topmost result from the results page.
     */
    public String getTopResultTitle() {
        return firstResultLink.getText();
    }

    /**
     * Returns the text of the topmost result from the results page.
     */
    public String getCalculatorResult() {
        String result = calcResultLink.getText();
        logger.info(String.format("result is '%s'", result));
        return result;
    }
}