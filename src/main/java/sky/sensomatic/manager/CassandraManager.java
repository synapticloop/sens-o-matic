package sky.sensomatic.manager;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import sky.sensomatic.utils.JsonUtils;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class CassandraManager {

	private static final String KEYSPACE_CREATE = "CREATE KEYSPACE sensomatic ";
	private static final String TABLE_YEARLY_CREATE = "create table sensomatic.yearly (id text, time timestamp, payload text, primary key(id, time))";
	private static final String TABLE_MONTHLY_CREATE = "create table sensomatic.monthly (id text, time timestamp, payload text, primary key(id, time))";
	private static final String TABLE_DAILY_CREATE = "create table sensomatic.daily (id text, time timestamp, payload text, primary key(id, time))";
	private static final String TABLE_HOURLY_CREATE = "create table sensomatic.hourly (id text, time timestamp, payload text, primary key(id, time))";
	private static final String TABLE_MINUTELY_CREATE = "create table sensomatic.minutely (id text, time timestamp, payload text, primary key(id, time))";
	private static final String TABLE_SECONDLY_CREATE = "create table sensomatic.secondly (id text, time timestamp, payload text, primary key(id, time))";

	private static final String[] CREATE_QUERIES = { TABLE_YEARLY_CREATE, TABLE_MONTHLY_CREATE, TABLE_DAILY_CREATE, TABLE_HOURLY_CREATE, TABLE_MINUTELY_CREATE, TABLE_SECONDLY_CREATE };

	private static final String CQL_INSERT_YEARLY = "insert into sensomatic.yearly (id, time, payload) values (?, ?, ?);";
	private static final String CQL_INSERT_MONTHLY = "insert into sensomatic.monthly (id, time, payload) values (?, ?, ?);";
	private static final String CQL_INSERT_DAILY = "insert into sensomatic.daily (id, time, payload) values (?, ?, ?);";
	private static final String CQL_INSERT_HOURLY = "insert into sensomatic.hourly (id, time, payload) values (?, ?, ?);";
	private static final String CQL_INSERT_MINUTELY = "insert into sensomatic.minutely (id, time, payload) values (?, ?, ?);";
	private static final String CQL_INSERT_SECONDLY = "insert into sensomatic.secondly (id, time, payload) values (?, ?, ?);";

	private static final String CQL_SELECT_LATEST_YEARLY = "select time, payload from sensomatic.yearly where id = ? order by time desc limit 1";
	private static final String CQL_SELECT_LATEST_MONTHLY = "select time, payload from sensomatic.monthly where id = ? order by time desc limit 1";
	private static final String CQL_SELECT_LATEST_DAILY = "select time, payload from sensomatic.daily where id = ? order by time desc limit 1";
	private static final String CQL_SELECT_LATEST_HOURLY = "select time, payload from sensomatic.hourly where id = ? order by time desc limit 1";
	private static final String CQL_SELECT_LATEST_MINUTELY = "select time, payload from sensomatic.minutely where id = ? order by time desc limit 1";
	private static final String CQL_SELECT_LATEST_SECONDLY = "select time, payload from sensomatic.secondly where id = ? order by time desc limit 1";

	private static final String CQL_SELECT_LAST_YEARLY = "select time, payload from sensomatic.yearly where id = ? and time >= ? and time <= ? order by time desc";
	private static final String CQL_SELECT_LAST_MONTHLY = "select time, payload from sensomatic.monthly where id = ? and time >= ? and time <= ? order by time desc";
	private static final String CQL_SELECT_LAST_DAILY = "select time, payload from sensomatic.daily where id = ? and time >= ? and time <= ? order by time desc";
	private static final String CQL_SELECT_LAST_HOURLY = "select time, payload from sensomatic.hourly where id = ? and time >= ? and time <= ? order by time desc";
	private static final String CQL_SELECT_LAST_MINUTELY = "select time, payload from sensomatic.minutely where id = ? and time >= ? and time <= ? order by time desc";
	private static final String CQL_SELECT_LAST_SECONDLY = "select time, payload from sensomatic.secondly where id = ? and time >= ? and time <= ? order by time desc";

	// static lookups for pre-prepared statements
	private static HashMap<Integer, PreparedStatement> TABLE_LOOKUP  = new HashMap<Integer, PreparedStatement>();
	private static HashMap<Integer, PreparedStatement> SELECT_LATEST_LOOKUP  = new HashMap<Integer, PreparedStatement>();
	private static HashMap<Integer, PreparedStatement> SELECT_LAST_LOOKUP  = new HashMap<Integer, PreparedStatement>();

	private static Cluster cluster;
	private static Session session;

	public static void initialise() {
		cluster = Cluster.builder().addContactPoint(PropertyManager.getCassandraNodeName()).build();
		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
		for ( Host host : metadata.getAllHosts() ) {
			System.out.printf("Datatacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(), host.getRack());
		}

		session = cluster.connect();

		KeyspaceMetadata keyspace = cluster.getMetadata().getKeyspace("sensomatic");
		if(keyspace == null) {
			System.out.println("Creating keyspace");
			session.execute(KEYSPACE_CREATE + PropertyManager.getCassandraReplication() + ";");

			for (int i = 0; i < CREATE_QUERIES.length; i++) {
				String query = CREATE_QUERIES[i];
				System.out.println("Executing query: " + query);
				session.execute(query);
			}
		} else {
			System.out.println("keyspace exists");
		}

		// now it is time to prepare all of the statements...
		TABLE_LOOKUP.put(4, session.prepare(CQL_INSERT_YEARLY));
		TABLE_LOOKUP.put(7, session.prepare(CQL_INSERT_MONTHLY));
		TABLE_LOOKUP.put(10, session.prepare(CQL_INSERT_DAILY));
		TABLE_LOOKUP.put(13, session.prepare(CQL_INSERT_HOURLY));
		TABLE_LOOKUP.put(16, session.prepare(CQL_INSERT_MINUTELY));
		TABLE_LOOKUP.put(19, session.prepare(CQL_INSERT_SECONDLY));

		SELECT_LATEST_LOOKUP.put(4, session.prepare(CQL_SELECT_LATEST_YEARLY));
		SELECT_LATEST_LOOKUP.put(7, session.prepare(CQL_SELECT_LATEST_MONTHLY));
		SELECT_LATEST_LOOKUP.put(10, session.prepare(CQL_SELECT_LATEST_DAILY));
		SELECT_LATEST_LOOKUP.put(13, session.prepare(CQL_SELECT_LATEST_HOURLY));
		SELECT_LATEST_LOOKUP.put(16, session.prepare(CQL_SELECT_LATEST_MINUTELY));
		SELECT_LATEST_LOOKUP.put(19, session.prepare(CQL_SELECT_LATEST_SECONDLY));

		SELECT_LAST_LOOKUP.put(4, session.prepare(CQL_SELECT_LAST_YEARLY));
		SELECT_LAST_LOOKUP.put(7, session.prepare(CQL_SELECT_LAST_MONTHLY));
		SELECT_LAST_LOOKUP.put(10, session.prepare(CQL_SELECT_LAST_DAILY));
		SELECT_LAST_LOOKUP.put(13, session.prepare(CQL_SELECT_LAST_HOURLY));
		SELECT_LAST_LOOKUP.put(16, session.prepare(CQL_SELECT_LAST_MINUTELY));
		SELECT_LAST_LOOKUP.put(19, session.prepare(CQL_SELECT_LAST_SECONDLY));
	}

	/**
	 * Insert a JSON payload into the cassandra database.  This all depends on
	 * the length of the time string as to which cassandra 'table' it gets
	 * inserted into
	 *
	 * @param jsonObject the son payload to be inserted into
	 */
	public static void insertPayload(JSONObject jsonObject) {
		String hash = jsonObject.getString("hash");

		String time = jsonObject.getString("time");
		System.out.println(time);
		long longTime = JsonUtils.convertTime(time);
		if(longTime == -1) {
			// TODO
			System.out.println("could not insert payload '" + jsonObject.toString() + "'.");
		} else {
			String payload = jsonObject.getJSONObject("payload").toString();
			BoundStatement boundStatement = new BoundStatement(TABLE_LOOKUP.get(time.length()));
			session.execute(boundStatement.bind(hash, new Date(longTime), payload));
		}
	}

	public static String getLatestResultsYearly(String hash) { return(getLatestResults(hash, SELECT_LATEST_LOOKUP.get(4))); }
	public static String getLatestResultsMonthly(String hash) { return(getLatestResults(hash, SELECT_LATEST_LOOKUP.get(7))); }
	public static String getLatestResultsDaily(String hash) { return(getLatestResults(hash, SELECT_LATEST_LOOKUP.get(10))); }
	public static String getLatestResultsHourly(String hash) { return(getLatestResults(hash, SELECT_LATEST_LOOKUP.get(13))); }
	public static String getLatestResultsMinutely(String hash) { return(getLatestResults(hash, SELECT_LATEST_LOOKUP.get(16))); }
	public static String getLatestResultsSecondly(String hash) { return(getLatestResults(hash, SELECT_LATEST_LOOKUP.get(19))); }

	/**
	 * Get the last inserted result from the cassandra 'table'
	 *
	 * @param hash the hash rowkey
	 * @param preparedStatement the looked up prepared statement
	 *
	 * @return the json data
	 */
	private static String getLatestResults(String hash, PreparedStatement preparedStatement) {
		BoundStatement boundStatement = new BoundStatement(preparedStatement);
		ResultSet resultSet = session.execute(boundStatement.bind(hash));
		// there is only one result
		String results = getResults(resultSet);

		return(results);
	}

	public static String getLastResultsYearly(Date from, Date to, String hash) { return(getLastResults(from, to, hash, SELECT_LAST_LOOKUP.get(4))); }
	public static String getLastResultsMonthly(Date from, Date to, String hash) { return(getLastResults(from, to, hash, SELECT_LAST_LOOKUP.get(7))); }
	public static String getLastResultsDaily(Date from, Date to, String hash) { return(getLastResults(from, to, hash, SELECT_LAST_LOOKUP.get(10))); }
	public static String getLastResultsHourly(Date from, Date to, String hash) { return(getLastResults(from, to, hash, SELECT_LAST_LOOKUP.get(13))); }
	public static String getLastResultsMinutely(Date from, Date to, String hash) { return(getLastResults(from, to, hash, SELECT_LAST_LOOKUP.get(16))); }
	public static String getLastResultsSecondly(Date from, Date to, String hash) { return(getLastResults(from, to, hash, SELECT_LAST_LOOKUP.get(19))); }

	private static String getLastResults(Date from, Date to, String hash, PreparedStatement preparedStatement) {
		BoundStatement boundStatement = new BoundStatement(preparedStatement);
		ResultSet resultSet = session.execute(boundStatement.bind(hash, from, to));
		return(getResults(resultSet));
	}

	private static String getResults(ResultSet resultSet) {
		JSONArray jsonArray = new JSONArray();
		for (Row row : resultSet) {
			jsonArray
					.put(new JSONObject()
					.put("time", JsonUtils.SIMPLE_DATE_FORMAT_SECOND.format(row.getDate("time")))
					.put("payload", new JSONObject(row.getString("payload"))));
		}
		return(new JSONObject().put("results", jsonArray).toString());
	}

	public static void close() {
		cluster.close();
	}

	public static void main(String[] args) {
		CassandraManager.initialise();
	}
}
