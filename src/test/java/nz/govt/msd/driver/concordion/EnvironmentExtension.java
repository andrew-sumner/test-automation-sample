package nz.govt.msd.driver.concordion;

import org.concordion.api.Element;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;

import nz.govt.msd.AppConfig;
import nz.govt.msd.utils.Config;

/**
 * Displays the environment details to the left hand side of the standard Concordion footer. 
 */
public class EnvironmentExtension implements ConcordionExtension, SpecificationProcessingListener {
	private String rerunClassName;

	public EnvironmentExtension(String rerunClassName) {
		this.rerunClassName = rerunClassName;
	}
	
	@Override
	public void addTo(final ConcordionExtender concordionExtender) {
		concordionExtender.withSpecificationProcessingListener(this);
	}

	@Override
	public void beforeProcessingSpecification(SpecificationProcessingEvent event) {
		
	}

	@Override
	public void afterProcessingSpecification(SpecificationProcessingEvent event) {
		Element leftFooter = getLeftFooter(event);

		appendEnviromentToFooter(leftFooter);
		appendJenkinsRerunToFooter(leftFooter);
	}
	
	private Element getLeftFooter(final SpecificationProcessingEvent event) {
		Element body = event.getRootElement().getFirstChildElement("body");

		if (body == null) {
			return null;
		}

		Element[] divs = body.getChildElements("div");
		for (Element div : divs) {
			if ("footer".equals(div.getAttributeValue("class"))) {
				Element footer = div;
				Element leftFooter = new Element("div");

				leftFooter.addStyleClass("footerLeft");

				footer.prependChild(leftFooter);

				return leftFooter;
			}
		}

		return null;
	}

	private void appendEnviromentToFooter(Element leftFooter) {
		Element anchor = new Element("a");
		anchor.addAttribute("href", AppConfig.getLoginUrl());
		anchor.addAttribute("style", "text-decoration: none; color: #89C;");
		anchor.appendText(AppConfig.getBaseUrl());

		leftFooter.appendText(AppConfig.getEnvironment().toUpperCase() + " (");
		leftFooter.appendChild(anchor);
		leftFooter.appendText(")");
	}

	private void appendJenkinsRerunToFooter(Element leftFooter) {
		String jenkinsUrl = System.getProperty("JENKINS_URL", "");
		String svnUrl = System.getProperty("SVN_URL", "");

		if (jenkinsUrl.isEmpty() || svnUrl.isEmpty()) {
			return;
		}

		String[] repos = {
				"/trunk", "/tags/", "/branches/"
		};

		for (String repo : repos) {
			int index = svnUrl.indexOf(repo);

			if (index > -1) {
				svnUrl = svnUrl.substring(index + 1);
				break;
			}
		}

		String jobUrl = String.format("%sjob/01%%20-%%20ProcessAndRules-RunSelectedTest", jenkinsUrl);
		String rerunUrl = String.format("%s/buildWithParameters?token=ALLOW&environment=%s&TEST_CLASSNAME=%s&SVN_TAG=%s",
				jobUrl, Config.getEnvironment(), rerunClassName, svnUrl);

		Element anchor = new Element("a");
		anchor.addAttribute("href", "#");
		anchor.addAttribute("onclick", "runtest(); return false;");
		anchor.addAttribute("style", "text-decoration: none; color: #89C; font-weight: bold;");
		anchor.appendText("Rerun this test");

		Element script = new Element("script");

		// The appendText method encodes '&' as '&amp;' so need to decode it in Javascript before navigate to URL
		script.appendText("function decodeEntities(encodedString) {");
		script.appendText("  var textArea = document.createElement('textarea');");
		script.appendText("  textArea.innerHTML = encodedString;");
		script.appendText("  return textArea.value;");
		script.appendText("}");

		script.appendText("function runtest() {");
		// Start test
		script.appendText("  console.log('Running test: ' + decodeEntities('" + rerunUrl + "'));");
		script.appendText("  var xmlHttp = new XMLHttpRequest();");
		script.appendText("  var xmlHttp = new XMLHttpRequest();");
		script.appendText("  xmlHttp.open('GET', decodeEntities('" + rerunUrl + "'), true );");
		script.appendText("  xmlHttp.send( null );");
		// View test
		script.appendText("  window.open(decodeEntities('" + jobUrl + "'), '_blank');");
		script.appendText("}");

		leftFooter.appendChild(script);

		leftFooter.appendChild(new Element("br"));
		leftFooter.appendChild(anchor);
	}
}
