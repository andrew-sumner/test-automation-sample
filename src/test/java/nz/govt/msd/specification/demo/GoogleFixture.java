package nz.govt.msd.specification.demo;

import java.io.IOException;

import org.concordion.slf4j.ext.ReportLoggerFactory;

import nz.govt.msd.driver.service.YahooWeather;
import nz.govt.msd.driver.ui.GoogleResultsPage;
import nz.govt.msd.driver.ui.GoogleSearchPage;
import nz.govt.msd.specification.ConcordionFixture;

/**
 * A fixture class for the StoryboardDemo.md specification.
 * <p>
 * This adds the Storyboard Extension to Concordion to add a storyboard to each example.
 * <p>
 * Two examples are included, a browser UI example using WebDriver, and a basic web service performing a HTTP GET.
 * <p>
 * Run this class as a JUnit test to produce the Concordion results.
 */
public class GoogleFixture extends ConcordionFixture {
	private GoogleSearchPage searchPage;
    private GoogleResultsPage resultsPage;

    /**
     * Searches for the specified topic, and waits for the results page to load.
     */
    public void searchFor(final String topic) {
        searchPage = GoogleSearchPage.open(this);
        resultsPage = searchPage.searchFor(topic);
    }

    /**
     * Returns the result from Google calculation.
     */
    public String getCalculatorResult() {
        return resultsPage.getCalculatorResult();
    }

    public boolean makeRestCall(String url) throws IOException {
    	YahooWeather weather = new YahooWeather();
    	
        String responseMessage = weather.getWeather();

        return !responseMessage.isEmpty();
    }
    
    public String searchForTopic(String topic) {
    	getStoryboard().addSectionContainer(topic);
    	
    	return GoogleSearchPage
    			.open(this)
    			.searchFor(topic)
    			.getCalculatorResult();
    }
}
