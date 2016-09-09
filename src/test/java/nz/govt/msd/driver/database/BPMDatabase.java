package nz.govt.msd.driver.database;

import java.sql.SQLException;
import java.util.Map;

import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.mapper.ObjectMappers;
import org.junit.Test;

import nz.govt.msd.AppConfig;

public class BPMDatabase {
	protected static final String DB_SCHEMA = AppConfig.getDatabaseSchema();

/*
	public String getReponseStatusJDBI(String auditId) throws SQLException {
		DBI dbi = new DBI(BPMDataSourceFactory.getOracleDataSource());

		// Does this have a listener for logging?

		try (Handle handle = dbi.open()) {
			Map<String, Object> result = handle
					.createQuery("SELECT * from " + DB_SCHEMA + ".tg_usertasktg where actiontaken = 'Complete' and (auditid = :auditId OR :auditId IS NULL)")
					.bind("auditId", auditId)
					.first();

			// Retrieve first column as a string
			// String instanceId = ...
			// .map(StringColumnMapper.INSTANCE)
			// .first();

			if (result == null) {
				return null;
			}

			return result.get("instanceid").toString();
		}
	}
*/
	public String getReponseStatusFluent(String auditId) throws SQLException {
		// This does this have a listener for logging, but can we easily return a Map<String, Object>?

		Map<String, Object> result = BPMDataSourceFactory.fluentJDBC()
				.query()
				.select(setSchema("SELECT * from ${SCHEMA}.TASKHISTORY WHERE TASKSTATUS = 'COMPLETED' and instanceid = :instanceid"))
				.namedParam("instanceid", 160507)
				.singleResult(Mappers.map());

		// Retrieve first column as a string
		// .singleResult(Mappers.singleString());
		
		return result.get("INSTANCEID").toString();
	}

	public String getReponseStatusFluentClass(String auditId) throws SQLException {
		// This does this have a listener for logging, but can we easily return a Map<String, Object>?

		UserTask result = BPMDataSourceFactory.fluentJDBC()
				.query()
				.select(setSchema("SELECT * from ${SCHEMA}.tg_usertasktg where actiontaken = 'Complete' and (auditid = :auditId OR :auditId IS NULL)"))
				.namedParam("auditId", auditId)
				.singleResult(ObjectMappers.builder().build().forClass(UserTask.class));

		// Retrieve first column as a string
		// .singleResult(Mappers.singleString());

		return result.instanceId;
	}

	private String setSchema(String sql) {
		return sql.replace("${SCHEMA}", DB_SCHEMA);
	}

	@Test
	public void runQuery() throws SQLException {
		System.out.println(AppConfig.getEnvironment());
		System.out.println(AppConfig.getDatabaseUrl());
		System.out.println(AppConfig.getDatabaseSchema());
		// System.out.println("JDBI: " + getReponseStatusJDBI(null));
		System.out.println("Fluent: " + getReponseStatusFluent(null));
		// System.out.println("FluentClass: " + getReponseStatusFluentClass(null));
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
