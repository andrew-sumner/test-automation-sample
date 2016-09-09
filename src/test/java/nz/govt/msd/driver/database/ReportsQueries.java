package nz.govt.msd.driver.database;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Mapper;

import nz.govt.msd.AppConfig;


public class ReportsQueries {
	private static final String TASK_PERFORMANCE_REPORT = "Task Performance Report";
	private static final String TRANSACTION_REPORT = "Transaction Report";
	private static final String PROCESSOR_PERFORMANCE_REPORT = "Processor Performance Report";
	
	private final String appDBSchema = AppConfig.getDatabaseSchema();
	private final String reportTable = AppConfig.getDatabaseReportTable();
	
	private String htmlForStoryBoard;
	
	public List<Map<String, Object>> getTaskPerformanceReports(String fromDate, String toDate, String site, String workType) throws SQLException {		
		List<Map<String, Object>> reportQueryMap;
		Mapper<Map<String, Object>> reportMapper = Mappers.map();
		
		StringBuilder sb = new StringBuilder();
	 		
		sb.append("SELECT tasktype WORKTYPE")
			.append(", site SITE")		
			.append(", declarationCount TASKSIZE")
			.append(", count(*) VOLUME")
			.append(", " + buildDateFormatterSQLString("round(avg(completedDuration),0)") + " AVERAGECOMPLETEDURATION")
			.append(", " + buildDateFormatterSQLString("round(min(completedDuration),0)") + " MINCOMPLETEDURATION")
			.append(", " + buildDateFormatterSQLString("round(max(completedDuration),0)") + " MAXCOMPLETEDURATION")
			.append(", " + buildDateFormatterSQLString("round(avg(waitDuration),0)") + " AVERAGEWAITTIME")
			.append(", " + buildDateFormatterSQLString("round(max(waitDuration),0)") + " MAXWAITTIME")		
			.append(", " + buildDateFormatterSQLString("round(avg(totalTaskDuration),0)") + " AVERAGETURNAROUNDTIME")
			.append(", " + buildDateFormatterSQLString("round(min(totalTaskDuration),0)") + " MINTURNAROUNDTIME")
			.append(", " + buildDateFormatterSQLString("round(max(totalTaskDuration),0)") + " MAXTURNAROUNDTIME")		
			.append(" FROM " + appDBSchema + "." + reportTable + " ")		
			.append(" WHERE taskStatus  = 'COMPLETED'")
			.append(" AND declarationCount > 0")
			.append(" AND NOT(declarationCount IS NULL)")
		
			.append(" AND (TASKTYPE=:tasktype OR :tasktype IS NULL)")		
			.append(" AND (SITE=:site OR :site IS NULL)")
					
			.append(" AND TRUNC(COMPLETEDDATETIME) BETWEEN TO_TIMESTAMP('")
			.append(fromDate).append("','DD/MM/YYYY') AND TO_TIMESTAMP('")
			.append(toDate).append("','DD/MM/YYYY') ")
			
			.append("GROUP BY tasktype, site, declarationCount ")
			.append("ORDER BY declarationCount ASC ");
				
		reportQueryMap = BPMDataSourceFactory.fluentJDBC()
												.query()
												.select(sb.toString())
												.namedParam("tasktype", workType)				
												.namedParam("site", site)			
												.listResult(reportMapper);				

		htmlForStoryBoard = buildHTMLForStoryBoard(sb.toString(), reportQueryMap, TASK_PERFORMANCE_REPORT);
		
		return reportQueryMap;
	}
	
	public List<Map<String, Object>> getProcessorPerformanceReports(String fromDate, String toDate, String workType, String site, String processor, String manager) 
				throws SQLException {
			
		List<Map<String, Object>> reportQueryMap;
		Mapper<Map<String, Object>> reportMapper = Mappers.map();
		
		StringBuilder sb = new StringBuilder();
								
		sb.append("SELECT site SITE")
			.append(", managerName MANAGER")
			.append(", assignedUser PROCESSOR ")		
			.append(", taskType WORKTYPE")
			.append(", count(*) VOLUME")						
			.append(", " + buildDateFormatterSQLString("round(avg(completedDuration),0)") + " AVERAGECOMPLETEDURATION ")
			.append(", " + buildDateFormatterSQLString("round(max(completedDuration),0)") + " MAXCOMPLETEDURATION ")				
			.append(" FROM " + appDBSchema + "." + reportTable + " ")		
			.append(" WHERE taskStatus  = 'COMPLETED' ")
			.append(" AND declarationCount > 0")
		
			.append(" AND TRUNC(COMPLETEDDATETIME) BETWEEN TO_TIMESTAMP('")
			.append(fromDate).append("','DD/MM/YYYY') AND TO_TIMESTAMP('")
			.append(toDate).append("','DD/MM/YYYY') ")		
		
			.append(" AND (TASKTYPE=:tasktype OR :tasktype IS NULL)")		
			.append(" AND (SITE=:site OR :site IS NULL)")		
			.append(" AND (MANAGERNAME=:manager OR :manager IS NULL)")		
			.append(" AND (ASSIGNEDUSER=:processor OR :processor IS NULL)")		
			.append(" GROUP BY assignedUser, managerName, taskType, site")
			.append(" ORDER BY assignedUser ASC ");
				
		reportQueryMap = BPMDataSourceFactory.fluentJDBC()
												.query()
												.select(sb.toString())
												.namedParam("tasktype", workType)
												.namedParam("site", site)
												.namedParam("manager", manager)
												.namedParam("processor", processor)
												.listResult(reportMapper);
						
		htmlForStoryBoard = buildHTMLForStoryBoard(sb.toString(), reportQueryMap, PROCESSOR_PERFORMANCE_REPORT);
				
		return reportQueryMap;

	}
	
	public String returnHTMLForStoryBoard() {
		return htmlForStoryBoard;
	}

	public List<Map<String, Object>> getTransactionDetailsOfReports(String workType, String site, String clientNumber, String manager, String processor, 
			String fromDate, String toDate) throws SQLException {
		
		List<Map<String, Object>> reportVOMap;
		Mapper<Map<String, Object>> mapper = Mappers.map();
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT CLIENTNUMBER CLIENTNUMBER,  INSTANCEID TASKID,")
			.append(" TO_CHAR(TRUNC(COMPLETEDDURATION/3600),'fm9900') || ':' ||")
			.append(" TO_CHAR(TRUNC(mod(COMPLETEDDURATION,3600)/60),'fm00') || ':' ||")
			.append(" TO_CHAR(mod(COMPLETEDDURATION,60),'fm00')")					
			.append(" COMPLETEDDURATION, DECLARATIONCOUNT,TASKTYPE WORKTYPE, ASSIGNEDUSER PROCESSOR, SITE SITE,")
			.append(" MANAGERNAME MANAGER,TO_CHAR(COMPLETEDDATETIME,'dd/mm/yyyy hh24:mi:ss') COMPLETIONDATE")
			.append(" FROM " + appDBSchema + "." + reportTable)
			.append(" WHERE TASKSTATUS = 'COMPLETED'")
			.append(" AND TRUNC(COMPLETEDDATETIME) BETWEEN TO_TIMESTAMP('")
		
			.append(fromDate).append("','DD/MM/YYYY') AND TO_TIMESTAMP('")
			.append(toDate).append("','DD/MM/YYYY') ")
		
			.append(" AND (TASKTYPE=:tasktype OR :tasktype IS NULL)")				
			.append(" AND (SITE=:site OR :site IS NULL)")		
			.append(" AND (CLIENTNUMBER=:clientNumber OR :clientNumber IS NULL)")		
			.append(" AND (MANAGERNAME=:manager OR :manager IS NULL)")		
			.append(" AND (ASSIGNEDUSER=:processor OR :processor IS NULL)")
		
			.append(" ORDER BY COMPLETEDDATETIME DESC ");		
				
		reportVOMap = BPMDataSourceFactory.fluentJDBC()
											.query()
											.select(sb.toString())
											.namedParam("tasktype", workType)
											.namedParam("site", site)
											.namedParam("clientNumber", clientNumber)
											.namedParam("manager", manager)
											.namedParam("processor", processor)
											.listResult(mapper);

		htmlForStoryBoard = buildHTMLForStoryBoard(sb.toString(), reportVOMap, TRANSACTION_REPORT);
		
		return reportVOMap;
	}
	
	private String buildHTMLForStoryBoard(String sqlQuery, List<Map<String, Object>> reportQueryMap, String title) {
		StringBuilder htmlBuilder = new StringBuilder();
		boolean isHeadersAdded = false;			
		
		htmlBuilder.append("<html><head><title>").append(title).append(" - Query Results</title></head>");
		htmlBuilder.append("<body>");
		htmlBuilder.append("<h3><br>SQL Query Results:</br></h3>");
		htmlBuilder.append("<table border=\"1\" bordercolor=\"#000000\">");	
		
		//add the column names		
		for (Map<String, Object> record : reportQueryMap) {
			
			//add the headers first for the first List record
			if (!isHeadersAdded) {
				htmlBuilder.append("<tr>");				
				for (String key : record.keySet()) {				
					htmlBuilder.append("<td>" + key + "</td>");																										
				}
				htmlBuilder.append("</tr>");
				
				//add the value
				htmlBuilder.append("<tr>");
				for (Object value : record.values()) {									
					if (value == null) {
						htmlBuilder.append("<td>(null)</td>");
					} else {
						htmlBuilder.append("<td>" + value.toString() + "</td>");
					}	
				}
				htmlBuilder.append("</tr>");			
				isHeadersAdded = true;
				
			} else {
				//add the value only
				htmlBuilder.append("<tr>");
				for (Object value : record.values()) {	
					if (value == null) {
						htmlBuilder.append("<td>(null)</td>");
					} else {
						htmlBuilder.append("<td>" + value.toString() + "</td>");
					}
				}
				htmlBuilder.append("</tr>");	
			}
									
		}
		
		//add the results		
		htmlBuilder.append("</table>");		
		htmlBuilder.append("<h3><br>SQL Query Executed:</br></h3>");
		htmlBuilder.append(sqlQuery);		
		htmlBuilder.append("</body></html>");
		
		return htmlBuilder.toString();
	}
		
	private String buildDateFormatterSQLString(String secondsToConvert) {
		String sqlQuery = "to_char(trunc(SS/3600),'FM9900') || ':' || to_char(trunc(MOD(SS,3600)/60),'FM00') || ':' || to_char(MOD(SS,60),'FM00')";
		return sqlQuery.replace("SS", secondsToConvert); 					
	}

}
