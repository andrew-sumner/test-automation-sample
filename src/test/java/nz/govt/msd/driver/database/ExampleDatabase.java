package nz.govt.msd.driver.database;

import java.sql.SQLException;
import java.util.Map;

import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.mapper.ObjectMappers;

import nz.govt.msd.AppConfig;

public class ExampleDatabase {
	protected static final String DB_SCHEMA = AppConfig.getDatabaseSchema();

	public String getReponseStatusFluent(String auditId) throws SQLException {
		// This does this have a listener for logging, but can we easily return a Map<String, Object>?

		Map<String, Object> result = ExampleDataSourceFactory.fluentJDBC()
				.query()
				.select(setSchema("SELECT * from ${SCHEMA}.TASKHISTORY WHERE TASKSTATUS = 'COMPLETED' and instanceid = :instanceid"))
				.namedParam("instanceid", 160507)
				.singleResult(Mappers.map());

		return result.get("INSTANCEID").toString();
	}

	public String getReponseStatusFluentClass(String auditId) throws SQLException {
		// This does this have a listener for logging, but can we easily return a Map<String, Object>?

		UserTask result = ExampleDataSourceFactory.fluentJDBC()
				.query()
				.select(setSchema("SELECT * from ${SCHEMA}.tg_usertasktg where actiontaken = 'Complete' and (auditid = :auditId OR :auditId IS NULL)"))
				.namedParam("auditId", auditId)
				.singleResult(ObjectMappers.builder().build().forClass(UserTask.class));

		return result.instanceId;
	}

	private String setSchema(String sql) {
		return sql.replace("${SCHEMA}", DB_SCHEMA);
	}

	public class UserTask {
		private String instanceId;

		public UserTask() {

		}

		public String getInstanceId() {
			return instanceId;
		}

		public void setInstanceId(String instanceId) {
			this.instanceId = instanceId;
		}
	}
}
