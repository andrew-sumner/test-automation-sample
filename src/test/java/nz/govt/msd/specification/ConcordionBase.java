package nz.govt.msd.specification;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.AfterExample;
import org.concordion.api.AfterSuite;
import org.concordion.api.ConcordionResources;
import org.concordion.api.extension.Extension;
import org.concordion.api.extension.Extensions;
import org.concordion.api.option.ConcordionOptions;
import org.concordion.api.option.MarkdownExtensions;
import org.concordion.ext.TimestampFormatterExtension;
import org.concordion.ext.runtotals.RunTotalsExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.logback.LogbackAdaptor;
import org.junit.runner.RunWith;

import nz.govt.msd.AppConfig;
import nz.govt.msd.driver.BrowserBasedTest;
import nz.govt.msd.driver.concordion.EnvironmentExtension;
import nz.govt.msd.driver.http.HttpEasy;
import nz.govt.msd.driver.web.Browser;

/**
 * Sets up any Concordion extensions or other items that must be shared between index and test fixtures.
 * 
 * NOTE: Test can be run from a Fixture or an Index, any global (@...Suite) methods must be in this class 
 * to ensure the are executed from whichever class initiates the test run.
 */
@RunWith(ConcordionRunner.class)
@ConcordionResources("/customConcordion.css")
@Extensions({ TimestampFormatterExtension.class, RunTotalsExtension.class })
@ConcordionOptions(markdownExtensions = { MarkdownExtensions.HARDWRAPS, MarkdownExtensions.AUTOLINKS })
public abstract class ConcordionBase implements BrowserBasedTest {
	private static List<Browser> browsers = new ArrayList<Browser>();
	private static ThreadLocal<Browser> browser = new ThreadLocal<Browser>();

	@Extension
	private final EnvironmentExtension footer = new EnvironmentExtension(this.getClass().getName().replace(ConcordionBase.class.getPackage().getName() + ".", ""));
	
	static {
		LogbackAdaptor.logInternalStatus();
		AppConfig.logSettings();

		// Set the proxy rules for all rest requests made during the test run
		HttpEasy.withDefaults()
				.allowAllHosts()
				.trustAllCertificates();
		// .baseUrl(AppConfig.getBaseUrl());

		if (AppConfig.isProxyRequired()) {
			HttpEasy.withDefaults()
					.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(AppConfig.getProxyHost(), AppConfig.getProxyPort())))
					.proxyAuth(AppConfig.getProxyUser(), AppConfig.getProxyPassword())
					.bypassProxyForLocalAddresses(true);
		}
	}
	
	@AfterExample
	private final void afterExample() {
		if (browser.get() != null) {
			browser.get().removeScreenshotTaker();
		}
	}

	@AfterSuite
	private final void afterSuite() {
		for (Browser openbrowser : browsers) {
			openbrowser.close();
		}
	}
	
	@Override
	public Browser getBrowser() {
		if (browser.get() == null) {
			Browser newBrowser = new Browser();
			browser.set(newBrowser);
			browsers.add(newBrowser);
		}

		return browser.get();
	}
}
