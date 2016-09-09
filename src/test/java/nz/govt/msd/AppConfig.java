package nz.govt.msd;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.govt.msd.utils.Config;

public class AppConfig extends Config {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

	private static String baseUrl;
	private static String loginUrl;
	private static String processPerformanceUrl;
	private static String teamPerformanceUrl;
	private static String operationalReportingUrl;
	private static String activityPerformanceReportUrl;
	private static String databaseUrl;
	private static String databaseSchema;
	private static String databaseReportTable;
	private static String taskManagementDetailUrl;	
	private static String bpmAdminUser;
	private static String searchByClientNumber;
	private static String reportSimulatorDeclareWagesUrl;
	private static String reportSimulatorApplicationUrl;
	
	private static String wmbManageIncomeServiceUrl = "";
	private static String dpManageIncomeServiceUrl = "";

	private static String odmDecisionServiceUrl;

	static {
		synchronized (AppConfig.class) {
			loadProperties();
		}
	}

	private AppConfig() { }

	public static void logSettings() {
		LOGGER.info("Environment:        " + getEnvironment());
		LOGGER.info("url:                " + loginUrl);
		LOGGER.info("Browser:            " + getBrowser());

		if (!getBrowserSize().isEmpty()) {
			LOGGER.info("browserSize:        " + getBrowserSize());
		}
	}

	private static void loadProperties() {
		Properties prop = loadFile(CONFIG_FILE);

		// Declare Wages specific properties
		baseUrl = getProperty(prop, "baseUrl");
		loginUrl = getProperty(prop, "loginUrl").replace("${baseUrl}", baseUrl);
		processPerformanceUrl = getProperty(prop, "processPerformanceUrl").replace("${baseUrl}", baseUrl);
		teamPerformanceUrl = getProperty(prop, "teamPerformanceUrl").replace("${baseUrl}", baseUrl);
		operationalReportingUrl = getProperty(prop, "operationalReportingUrl").replace("${baseUrl}", baseUrl);
		activityPerformanceReportUrl = getProperty(prop, "activityPerformanceReportUrl").replace("${baseUrl}", baseUrl);
		databaseUrl = getProperty(prop, "databaseUrl");
		databaseSchema = getProperty(prop, "databaseSchema");
		databaseReportTable = getProperty(prop, "databaseReportTable");
		taskManagementDetailUrl = getProperty(prop, "taskManagementDetailUrl").replace("${baseUrl}", baseUrl);
		bpmAdminUser = getProperty(prop, "bpmAdminUser");
		searchByClientNumber = getProperty(prop, "searchByClientNumber").replace("${baseUrl}", baseUrl);		
		reportSimulatorDeclareWagesUrl = getProperty(prop, "reportSimulatorDeclareWagesUrl").replace("${baseUrl}", baseUrl);
		reportSimulatorApplicationUrl = getProperty(prop, "reportSimulatorApplicationUrl").replace("${baseUrl}", baseUrl);

		String serviceBaseUrl = getOptionalProperty(prop, "wmbBaseUrl");
		if (!serviceBaseUrl.isEmpty()) {
			wmbManageIncomeServiceUrl = getProperty(prop, "wmbManageIncomeUrl").replace("${wmbBaseUrl}", serviceBaseUrl);			
		}

		String dpBaseUrl = getOptionalProperty(prop, "dpBaseUrl");
		if (!dpBaseUrl.isEmpty()) {
			dpManageIncomeServiceUrl = getProperty(prop, "dpManageIncomeUrl").replace("${dpBaseUrl}", dpBaseUrl);
		}

		String odmBaseUrl = getOptionalProperty(prop, "odmBaseUrl");
		if (!odmBaseUrl.isEmpty()) {
			odmDecisionServiceUrl = getProperty(prop, "odmDecisionServiceUrl").replace("${odmBaseUrl}", odmBaseUrl);
		}
	}

	// Declare Wages specific properties
	public static String getBaseUrl() {
		return baseUrl;
	}

	public static String getLoginUrl() {
		return loginUrl;
	}

	public static String getProcessPerformanceUrl() {
		return processPerformanceUrl;
	}

	public static String getTeamPerformanceUrl() {
		return teamPerformanceUrl;
	}

	public static String getOperationalReportingUrl() {
		return operationalReportingUrl;
	}

	public static String getActivityPerformanceReportUrl() {
		return activityPerformanceReportUrl;
	}

	public static String getDatabaseUrl() {
		return databaseUrl;
	}

	public static String getDatabaseSchema() {
		return databaseSchema;
	}
	
	public static String getDatabaseReportTable() {
		return databaseReportTable;
	}

	public static String getTaskManagementDetailUrl() {
		return taskManagementDetailUrl;
	}

	public static String getBpmAdminUser() {
		return bpmAdminUser;
	}

	public static String getSearchByClientNumber() {
		return searchByClientNumber;
	}
	
	public static String getReportSimulatorDeclareWagesUrl() {
		return reportSimulatorDeclareWagesUrl;
	}
	
	public static String getReportSimulatorApplicationUrl() {
		return reportSimulatorApplicationUrl;
	}

	public static String getWmbManageIncomeServiceUrl() {
		return wmbManageIncomeServiceUrl;
	}

	public static String getDpManageIncomeServiceUrl() {
		return dpManageIncomeServiceUrl;
	}

	public static String getOdmDecisionServiceUrl() {
		return odmDecisionServiceUrl;
	}

}

