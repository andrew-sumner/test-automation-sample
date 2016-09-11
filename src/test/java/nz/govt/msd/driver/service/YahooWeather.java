package nz.govt.msd.driver.service;

import java.io.IOException;

import org.concordion.ext.StoryboardMarkerFactory;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.slf4j.ext.MediaType;
import org.concordion.slf4j.ext.ReportLogger;
import org.concordion.slf4j.ext.ReportLoggerFactory;

import nz.govt.msd.driver.http.HttpEasy;
import nz.govt.msd.driver.http.JsonReader;

/**
 * https://developer.yahoo.com/weather/
 */
public class YahooWeather {
	private static final ReportLogger LOGGER = ReportLoggerFactory.getReportLogger(YahooWeather.class);
		    
	//xmlurl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	private static String URL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
		
	public String getWeather() throws IOException {
		JsonReader reader = HttpEasy.request()
			.path(URL)
			.get()
			.getJsonReader();
		
		LOGGER.with()
			.message("Response")
			.attachment(reader.asPrettyString(), "response.json", MediaType.JSON)
			.marker(StoryboardMarkerFactory.addCard("Yahoo Weather", StockCardImage.JSON))
			.debug();
		
		return reader.asPrettyString();
	}
}
