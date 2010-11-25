package ortus;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;
import ortus.db.IEngine;
import sagex.api.Configuration;
import sagex.api.Global;

/**
 *
 * @author jphipps
 */
public class h2engine implements IEngine {

	private String h2driver = "org.h2.Driver";
	private String h2protocol = null;
	private Server tcpserver = null;
	private Server webserver = null;
	private Server pgserver = null;
	private Map<String, JdbcConnectionPool> DatabaseMap = Collections.synchronizedMap(new HashMap(5));

	public h2engine() {
		h2protocol = Configuration.GetProperty("h2/protocol", "jdbc:h2:H2");
		StartupDatabase();
	}

	public void StartupDatabase() {
		Global.DebugLog("H2: Startup: " + h2protocol);
		if (!ConnectDB(h2protocol, "sageDB", "sage", "sage")) {
			return;
		}

		executeSQLQueryJar(getClass(), "sageDB","/resources/createApiAlias.sql");
		
		if (Configuration.GetProperty("h2/tcpserver_start", "yes").equals("yes")) {
			StartTcpServer();
		}

		if (Configuration.GetProperty("h2/webserver_start", "yes").equals("yes")) {
			StartWebServer();
		}

		if (Configuration.GetProperty("h2/pgserver_start", "no").equals("yes")) {
			StartPgServer();
		}
		Global.DebugLog("H2: Startup Completed");
	}

	public void ShutdownDatabase() {
		Global.DebugLog("H2: Shutdown Starting");
		try {
			tcpserver.stop();
			webserver.stop();
			Iterator ActiveDBConnections = DatabaseMap.keySet().iterator();
			while (ActiveDBConnections.hasNext()) {
				String dbname = (String) ActiveDBConnections.next();
				Global.DebugLog("H2:    Stopping " + dbname);
				JdbcConnectionPool jp = (JdbcConnectionPool) DatabaseMap.get(dbname);
				jp.dispose();
			}
			Global.DebugLog("H2: Shutdwon Completed");
		} catch (Exception ex) {
		}
	}

	public boolean ConnectDB(String connectinfo, String database, String user, String passwd) {
		if (!loadDriver()) {
			Global.DebugLog("H2: h2 jar files are missing");
			return false;
		}

		if (DatabaseMap.get(database) != null) {
			return true;
		}

		try {
			String dbconnectinfo = connectinfo;
			if (connectinfo.endsWith("/")) {
				dbconnectinfo += database;
			} else {
				dbconnectinfo += "/" + database;
			}

			JdbcConnectionPool cp = JdbcConnectionPool.create(dbconnectinfo, "sage", "sage");
			cp.setMaxConnections(256);
			DatabaseMap.put(database, cp);
			List<List> result = executeSQLQuery(database, "call database_path()");
			if (result.size() > 0) {
				Global.DebugLog("H2: Database Path: " + result.get(0).get(0));
			}
		} catch (Exception e) {
			Global.DebugLog("H2: SQLEexception: " + e);
			return false;
		}
		return true;
	}

	private boolean loadDriver() {
		try {
			Class.forName(h2driver);
			Global.DebugLog("H2: Successfully Loaded Embedded Driver");
		} catch (ClassNotFoundException cnfe) {
			Global.DebugLog("H2: Exception: Unable to load the JDBC driver " + h2driver);
			Global.DebugLog("H2: Exception: Please check your CLASSPATH.");
			return false;
		}
		return true;
	}

	public void StartTcpServer() {
		if (tcpserver != null) {
			return;
		}

		try {
			String TcpPort = Configuration.GetProperty("h2/tcpserver", "9092");
			Global.DebugLog("H2: Starting Tcp Server on port: " + TcpPort);
			tcpserver = Server.createTcpServer(new String[]{"-tcpAllowOthers", "-tcpPort", TcpPort}).start();
		} catch (Exception e) {
			Global.DebugLog("H2: Tcp server failed to start");
		}
	}

	public void StopTcpServer() {
		if (tcpserver != null) {
			tcpserver.stop();
			tcpserver = null;
		}
	}

	public boolean IsTcpServerRunning() {
		if (tcpserver == null) {
			return false;
		} else {
			return true;
		}
	}

	public void StartWebServer() {
		if (webserver != null) {
			return;
		}

		try {
			String WebPort = Configuration.GetProperty("h2/webserver", "8082");
			Global.DebugLog("H2: Starting Web Server on port: " + WebPort);
			webserver = Server.createWebServer(new String[]{"-webAllowOthers", "-webPort", WebPort}).start();
		} catch (Exception e) {
			Global.DebugLog("H2: Web server failed to start");
		}
	}

	public void StopWebServer() {
		if (webserver != null) {
			webserver.stop();
			webserver = null;
		}
	}

	public boolean IsWebServerRunning() {
		if (webserver == null) {
			return false;
		} else {
			return true;
		}
	}

	public void StartPgServer() {
		if (pgserver != null) {
			return;
		}

		try {
			String PgPort = Configuration.GetProperty("h2/pgserver", "5435");
			Global.DebugLog("H2: Starting Web Server on port: " + PgPort);
			pgserver = Server.createPgServer(new String[]{"-pgPort", PgPort}).start();
		} catch (Exception e) {
			Global.DebugLog("H2: Pg server failed to start");
		}
	}

	public void StopPgServer() {
		if (pgserver != null) {
			pgserver.stop();
			pgserver = null;
		}
	}

	public boolean IsPgServerRunning() {
		if (pgserver == null) {
			return false;
		} else {
			return true;
		}
	}

	public Object[] GetDatabases() {
		return DatabaseMap.keySet().toArray();
	}

	public Connection GetConnection(String dbname) {
		try {
			return DatabaseMap.get(dbname).getConnection();
		} catch (Exception ex) {
			Global.DebugLog("H2: Exception: GetConnection: " + ex);
			return null;
		}
	}

	public List<Object> GetStatus(String dbname) {
		List<Object> status = new ArrayList<Object>();

		List<List> result = executeSQLQuery(dbname, "call database()");
		if (result.size() > 0) {
			status.add("Database Name: " + result.get(0).get(0));
		}
		result = executeSQLQuery(dbname, "call database_path()");
		if (result.size() > 0) {
			status.add("Database Path: " + result.get(0).get(0));
		}
		result = executeSQLQuery(dbname, "call memory_used()");
		if (result.size() > 0) {
			status.add("Memory Used: " + result.get(0).get(0));
		}
		result = executeSQLQuery(dbname, "call memory_free()");
		if (result.size() > 0) {
			status.add("Memory Free: " + result.get(0).get(0));
		}
		result = executeSQLQuery(dbname, "select count(*) from INFORMATION_SCHEMA.SESSIONS");
		if (result.size() > 0) {
			status.add("Active Sessions: " + result.get(0).get(0));
		}
		result = executeSQLQuery(dbname, "select count(*) from sage.media");
		if (result.size() > 0) {
			status.add("Total Media Objects: " + result.get(0).get(0));
		}
		status.add("Procotol: " + h2protocol);

		return status;
	}

	public int executeSQL(String dbname, String sql) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = GetConnection(dbname);
			stmt = conn.createStatement();
			stmt.execute(sql);
			int UpdateCount = stmt.getUpdateCount();
			return UpdateCount;
		} catch (SQLException e) {
			Global.DebugLog("H2: SQL: " + sql);
			Global.DebugLog("H2: SQLstate: " + e.getSQLState());
			Global.DebugLog("H2: SQLException: " + e);
			return -1;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Execute a query to return data such as select
	 * @param sql SQL to execute
	 * @return An array of results
	 */
	public List<List> executeSQLQuery(String dbname, String sql) {
		List<List> ra = new ArrayList<List>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = GetConnection(dbname);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				List recarray = new ArrayList();
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int x = 1; x <= rsmd.getColumnCount(); x++) {
					recarray.add(rs.getString(x));
				}
				ra.add(recarray);
			}
			return ra;
		} catch (Exception e) {
			Global.DebugLog("SQL: " + sql);
			Global.DebugLog("SQLException: " + e);
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Execute a query to return data such as select
	 * @param sql SQL to execute
	 * @return An array of results
	 */
	public List<HashMap> executeSQLQueryHash(String dbname, String sql) {
		List<HashMap> ra = new ArrayList<HashMap>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = GetConnection(dbname);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				List recarray = new ArrayList();
				ResultSetMetaData rsmd = rs.getMetaData();
				HashMap rechash = new HashMap();
				for (int x = 1; x <= rsmd.getColumnCount(); x++) {
					rechash.put(rsmd.getColumnName(x), rs.getString(x));
				}
				ra.add(rechash);
			}
			return ra;
		} catch (Exception e) {
			Global.DebugLog("H2: SQL: " + sql);
			Global.DebugLog("H2: SQLException: " + e);
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public int executeSQLQueryJar(Class jarclass, String dbname, String sqlfile) {
		List<String> sql = LoadJarFile(jarclass, sqlfile);
		int totalrows = 0;

		StringBuffer sqlglob = new StringBuffer();
		for (String x : sql) {
			x = x.trim();
			if (!x.isEmpty()) {
				sqlglob.append(" " + x);
			}
		}

		String[] sqlstmts = sqlglob.toString().split(";");
		for (String s : sqlstmts) {
			Global.DebugLog("H2: executeSQLQueryJar: executing: " + s + " on DB: " + dbname);
			totalrows += executeSQL(dbname, s);
		}
		return totalrows;
	}

	public int executeSQLQueryFile(Class jarclass, String dbname, String sqlfile) {
		List<String> sql = LoadFile(sqlfile);
		int totalrows = 0;

		StringBuffer sqlglob = new StringBuffer();
		for (String x : sql) {
			x = x.trim();
			if (!x.isEmpty()) {
				sqlglob.append(" " + x);
			}
		}

		String[] sqlstmts = sqlglob.toString().split(";");
		for (String s : sqlstmts) {
			Global.DebugLog("H2: executeSQLQueryFile: executing: " + s + " on DB: " + dbname);
			totalrows += executeSQL(dbname, s);
		}
		return totalrows;
	}

	public boolean backupDB() {
		Global.DebugLog("H2: backupDB: Performing database backup");
		boolean backupresult = true;
		String currentdate = new java.text.SimpleDateFormat("yyyyMMddhhmm").format(new Date());
		String backuppath = Configuration.GetProperty("h2/backuppath", configurationEngine.getInstance().getBasePath()) + java.io.File.separator + "H2Backups";
		File bp = new File(backuppath);
		if (!bp.exists()) {
			bp.mkdirs();
		}
		Iterator dbs = DatabaseMap.keySet().iterator();
		while (dbs.hasNext()) {
			String dbname = (String) dbs.next();
			String backupfile = dbname + "-" + currentdate + ".zip";
			Global.DebugLog("backupDB: Backing up to " + backuppath + java.io.File.separator + backupfile);
			int result = executeSQL(dbname, "backup to '" + backuppath + java.io.File.separator + backupfile + "'");
			if (result == 0) {
				Global.DebugLog("Backup of " + dbname + "successful");
			} else {
				Global.DebugLog("Backup of " + dbname + " Failed, result: " + result);
				return false;
			}
		}
		return backupresult;
	}

	public boolean restoreDB(String backupfile) {
		return true;
	}

	private List<String> LoadJarFile(Class jarclass, String jarfile) {
		Global.DebugLog("H2: LoadJarFile: Loading: " + jarfile);
		List<String> Entries = new ArrayList<String>();
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = jarclass.getResourceAsStream(jarfile);
			br = new BufferedReader(new InputStreamReader(is));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				Entries.add(strLine);
			}
			return Entries;
		} catch (Exception e) {
			Global.DebugLog("H2: loadJarFile: " + jarfile + " Exception: " + e);
			return Entries;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private List<String> LoadFile(String logfile) {
		List<String> Entries = new ArrayList<String>();
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		try {
			fstream = new FileInputStream(logfile);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				Entries.add(strLine);
			}
			in.close();

			return Entries;
		} catch (Exception e) {
			Global.DebugLog("H2: LoadFile: " + logfile + " Failed");
			return Entries;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			if (fstream != null) {
				try {
					fstream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public Connection GetConnection() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Object> GetStatus() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean createDB() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int executeSQL(String sql) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Object> executeSQLQuery(String sql) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Object> executeSQLQueryCache(String sql) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<List> executeSQLQueryArray(String sql) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<List> executeSQLQueryArrayCache(String sql) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<HashMap> executeSQLQueryHash(String sql) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<HashMap> executeSQLQueryHashCache(String sql) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Object> getMediaFilesSQL(String sql) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean GetCleanDB() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
