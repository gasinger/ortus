package ortus.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;
import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;
import ortus.vars.LogLevel;
import sagex.api.Global;
import sagex.api.MediaFileAPI;

/**
 *
 * @author jphipps
 */
public class h2engine extends EventListener implements IEngine {

	private final String H2PROTOCOL = "org.h2.Driver";
	private String h2protocol = null;
	private JdbcConnectionPool cp = null;
	public boolean indexMediaRunning = false;
	private Server tcpserver = null;
	private Server webserver = null;
	private Server pgserver = null;
	private boolean cleandb = false;

	public h2engine() {
		super();

		boolean dbstarted = false;
		h2protocol = ortus.api.GetProperty("h2protocol", "");

		if ( ! h2protocol.isEmpty()) {
			dbstarted = connectDB(h2protocol, "sage", "sage");
		}

//		if ( ! dbstarted && ortus.util.ui.IsClient()) {
//			h2protocol="jdbc:h2:tcp://" + Global.GetServerAddress() + "/STVs/Ortus/db/ortusDB";
//			dbstarted = connectDB(h2protocol, "sage", "sage");
//			if ( dbstarted )
//				ortus.api.SetProperty("remotehost", Global.GetServerAddress());
//		}

		if ( !dbstarted ) {
			h2protocol="jdbc:h2:STVs/Ortus/db/ortusDB";
			dbstarted = connectDB(h2protocol, "sage", "sage");
			if ( dbstarted )
				ortus.api.SetProperty("remotehost", "none");
		}

		if ( !dbstarted) {
			ortus.api.DebugLog(LogLevel.Fatal, "OrtusDB: Can't find a database connection...");
			return;
		}

		List<Object> result = executeSQLQuery("SELECT schema_name  FROM INFORMATION_SCHEMA.SCHEMATA where schema_name = 'SAGE'");
		ortus.api.DebugLog(LogLevel.Trace, " schema result count: " + result.size());
		if (result.size() > 0) {
			ortus.api.DebugLog(LogLevel.Trace, " schema name: " + result.get(0));
		}
		if (result.size() < 1) {
			ortus.api.DebugLog(LogLevel.Info, "Ortus: Database not found, creating...");
			cleandb = true;
			createDB();
		}

//                ortus.api.DebugLog(LogLevel.Trace, "OrtusDB: Starting web interface");
//                Thread webThread = new Thread() {
//                    public void run() {
//                        try {
//                            ortus.api.DebugLog(LogLevel.Trace,"OrtusDB: Web Thread is starting");
//                            Server serv = Server.createWebServer(new String[] { "-webAllowOthers","-tcpPort","8069"}).start();
//                            serv.startWebServer(GetConnection());
////                            Server.startWebServer(GetConnection());
//                            ortus.api.DebugLog(LogLevel.Trace, "OrtusDB: Web Thread is terminating");
//                        } catch( Exception e) {
//                            ortus.api.DebugLog(LogLevel.Trace, "OrtusDB: Start Web Server Exception: " + e);
//                        }
//                    }
//                };
//                webThread.start();
        }

	@OrtusEvent("Shutdown")
	public void ShutdownDatabase() {
		ortus.api.DebugLog(LogLevel.Debug, "Shutting down ortusDB connection");
		try {
			cp.dispose();
		} catch (Exception ex) {
		}
	}

	private boolean connectDB(String connectinfo, String user, String passwd) {
		ortus.api.DebugLog(LogLevel.Debug, "OrtusDB: Connecting to: " + connectinfo);
		if (!loadDriver()) {
			ortus.api.DebugLog(LogLevel.Fatal, "h2 jar files are missing");
			return false;
		}
		Connection conn = null;
		try {
			cp = JdbcConnectionPool.create(connectinfo, "sage", "sage");
			cp.setMaxConnections(100);
			conn = GetConnection();
			if ( conn == null) {
				ortus.api.DebugLog(LogLevel.Error, "OrtusDB: Connect Failure for: " + connectinfo);
				return false;
			}

			List<List> result = executeSQLQueryArray("call database_path()");
			if (result.size() > 0) {
				ortus.api.DebugLog(LogLevel.Trace, "OrtusDB: Database Path: " + result.get(0).get(0));
			}
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "OrtusDB: Connect SQLEexception: " + e);
			return false;
		} finally {
			if ( conn != null)
				try { conn.close(); } catch(Exception e) {}
		}
		ortus.api.DebugLog(LogLevel.Debug, "OrtusDB: Successfully connected to " + h2protocol);
		return true;
	}

	private boolean loadDriver() {
		String driver = null;
		try {
			driver = H2PROTOCOL;
			Class.forName(driver);
//			ortus.api.DebugLog(LogLevel.Info, "ortusDB: Successfully Loaded Embedded Driver");
		} catch (ClassNotFoundException cnfe) {
			ortus.api.DebugLog(LogLevel.Error, "Unable to load the JDBC driver " + driver);
			ortus.api.DebugLog(LogLevel.Error, "Please check your CLASSPATH.");
			return false;
		}
		return true;
	}

	public boolean GetCleanDB() {
		return cleandb;
	}

	public void SetCleanDB(boolean cleandb) {
		this.cleandb = cleandb;
	}

	public Connection GetConnection() {
		try {
			return cp.getConnection();
		} catch (Exception ex) {
			ortus.api.DebugLog(LogLevel.Error, "SQLException: GetConnection: " + ex);
			return null;
		}
	}

	public List<Object> GetStatus() {
		List<Object> status = new ArrayList<Object>();

		ortus.api.DebugLog(LogLevel.Trace2, "database()");
		List<List> result = executeSQLQueryArray("call database()");
		if (result.size() > 0) {
			status.add("Database Name: " + result.get(0).get(0));
		}
		ortus.api.DebugLog(LogLevel.Trace2, "db path");
		result = executeSQLQueryArray("call database_path()");
		if (result.size() > 0) {
			status.add("Database Path: " + result.get(0).get(0));
		}
		ortus.api.DebugLog(LogLevel.Trace2, "memused)");
		result = executeSQLQueryArray("call memory_used()");
		if (result.size() > 0) {
			status.add("Memory Used: " + result.get(0).get(0));
		}
		ortus.api.DebugLog(LogLevel.Trace2, "memfree");
		result = executeSQLQueryArray("call memory_free()");
		if (result.size() > 0) {
			status.add("Memory Free: " + result.get(0).get(0));
		}
		ortus.api.DebugLog(LogLevel.Trace2, "sessions");
		result = executeSQLQueryArray("select count(*) from INFORMATION_SCHEMA.SESSIONS");
		if (result.size() > 0) {
			status.add("Active Sessions: " + result.get(0).get(0));
		}
		ortus.api.DebugLog(LogLevel.Trace2, "sagemediacount");
		result = executeSQLQueryArray("select count(*) from sage.media");
		if (result.size() > 0) {
			status.add("Total Media Objects: " + result.get(0).get(0));
		}
		status.add("Procotol: " + h2protocol);

		return status;
	}

	public int executeSQL(String sql) {
		Connection conn = GetConnection();
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			int UpdateCount = stmt.getUpdateCount();
			return UpdateCount;
		} catch (SQLException e) {
			try {
				conn.close();
			} catch (Exception ex) {
			}
			ortus.api.DebugLog(LogLevel.Error, "executeSQL: SQL: " + sql);
			ortus.api.DebugLog(LogLevel.Error, "executeSQL: SQLstate: " + e.getSQLState());
			ortus.api.DebugLog(LogLevel.Error, "executeSQL: SQLException: " ,e);
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
	public List<Object> executeSQLQuery(String sql) {
		List<Object> ra = new ArrayList<Object>();
		Connection conn = GetConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				StringBuffer returnrow = new StringBuffer();
				int fCount = 0;
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int x = 1; x <= rsmd.getColumnCount(); x++) {
					fCount++;
					if (fCount > 1) {
						returnrow.append(",");
					}
					if (rs.getString(x) != null) {
						returnrow.append(rs.getString(x).trim());
					} else {
						returnrow.append("");
					}
				}
				ra.add(returnrow.toString());
			}
			return ra;
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "SQL: " + sql);
			ortus.api.DebugLog(LogLevel.Error, "SQLException: ", e);
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
	public List<Object> executeSQLQueryCache(String sql) {
		Object ci = ortus.cache.cacheEngine.getInstance().getProvider().Get(ortus.api.MD5Sum("Query:" + sql));
		if (ci != null) {
			return (List) ci;
		}

		Connection conn = GetConnection();
		Statement stmt = null;
		ResultSet rs = null;
		List<Object> ra = new ArrayList<Object>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				StringBuffer returnrow = new StringBuffer();
				int fCount = 0;
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int x = 1; x <= rsmd.getColumnCount(); x++) {
					fCount++;
					if (fCount > 1) {
						returnrow.append(",");
					}
					if (rs.getString(x) != null) {
						returnrow.append(rs.getString(x).trim());
					} else {
						returnrow.append("");
					}
				}
				ra.add(returnrow.toString());
			}
			ortus.cache.cacheEngine.getInstance().getProvider().Put(ortus.api.MD5Sum("Query:" + sql), ra);
			return ra;
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "SQL: " + sql);
			ortus.api.DebugLog(LogLevel.Error, "SQLException: ",e);
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
	public List<List> executeSQLQueryArray(String sql) {
		List<List> ra = new ArrayList<List>();
		Connection conn = GetConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
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
			ortus.api.DebugLog(LogLevel.Error, "SQL: " + sql);
			ortus.api.DebugLog(LogLevel.Error, "SQLException: ",e);
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
	public List<List> executeSQLQueryArrayCache(String sql) {
		Object ci = ortus.cache.cacheEngine.getInstance().getProvider().Get(ortus.api.MD5Sum("Array:" + sql));
		if (ci != null) {
			return (List) ci;
		}

		List<List> ra = new ArrayList<List>();
		Connection conn = GetConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
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
			ortus.cache.cacheEngine.getInstance().getProvider().Put(ortus.api.MD5Sum("Array:" + sql), ra);
			return ra;
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "SQL: " + sql);
			ortus.api.DebugLog(LogLevel.Error, "SQLException: ", e);
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
	public List<HashMap> executeSQLQueryHash(String sql) {
		List<HashMap> ra = new ArrayList<HashMap>();
		Connection conn = GetConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
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
			ortus.api.DebugLog(LogLevel.Error, "SQL: " + sql);
			ortus.api.DebugLog(LogLevel.Error, "SQLException: ", e);
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
	public List<HashMap> executeSQLQueryHashCache(String sql) {
		Object ci = ortus.cache.cacheEngine.getInstance().getProvider().Get(ortus.api.MD5Sum("Hash:" + sql));
		if (ci != null) {
			return (List) ci;
		}

		List<HashMap> ra = new ArrayList<HashMap>();
		Connection conn = GetConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
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
			ortus.cache.cacheEngine.getInstance().getProvider().Put(ortus.api.MD5Sum("Hash:" + sql), ra);
			return ra;
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "SQL: " + sql);
			ortus.api.DebugLog(LogLevel.Error, "SQLException: ", e);
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

	public int executeSQLQueryJar(String sqlfile) {
//		List<String> sql = configurationEngine.getInstance().LoadJarFile(sqlfile);
		int totalrows=0;		
		String sqlglob = "";
		try {
			sqlglob = IOUtils.toString(getClass().getResourceAsStream(sqlfile));
		} catch (IOException ex) {
                    ortus.api.DebugLog(LogLevel.Error, "executeSQLQueryJar: Exception: " + ex);
                    return 0;
                }

		String[] sqlstmts = sqlglob.split(";");
		for (String s : sqlstmts) {
			ortus.api.DebugLog(LogLevel.Trace2, "executeSQLQueryJar: executing: " + s);
			totalrows += executeSQL(s);
		}
		return totalrows;
	}

	public int executeSQLQueryFile(String sqlfile) {
		int totalrows = 0;
		String sqlglob="";
		try {
			sqlglob = FileUtils.readFileToString(new File(sqlfile));
		} catch (IOException ex) {}

		String[] sqlstmts = sqlglob.split(";");
		for (String s : sqlstmts) {
			ortus.api.DebugLog(LogLevel.Trace2, "executeSQLQueryFile: executing: " + s);
			totalrows += executeSQL(s);
		}
		return totalrows;
	}

	/**
	 * Return an array of media objects that matches the passed where clause
	 * @param sql where cluase
	 * @return Media Object Array
	 */
	public List<Object> getMediaFilesSQL(String sql) {
		List<Object> mfl = new ArrayList<Object>();
		Connection conn = GetConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				if (rs.getInt("mediaid") > 0) {
					mfl.add(MediaFileAPI.GetMediaFileForID(rs.getInt("mediaid")));
				}
			}

			return mfl;

		} catch (SQLException ex) {
			ortus.api.DebugLog(LogLevel.Error, "SQLException: " ,ex);
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
		return null;
	}

	@OrtusEvent("CreateDB")
	public boolean createDB() {
		int rc = 0;
//       String sqlpath = ortus.api.GetProperty("ortus/basepath","") + java.io.File.separator + "sql" + java.io.File.separator;
		String sqlpath = "/ortus/resources/";
		ortus.api.DebugLog(LogLevel.Info, "ortusDB: building tables using connection pool");
		try {
			rc = executeSQLQueryJar(sqlpath + "createschema.sql");
			rc = executeSQLQueryJar(sqlpath + "createaliases.sql");
			rc = executeSQLQueryJar(sqlpath + "createmedia.sql");
			rc = executeSQLQueryJar(sqlpath + "createtv.sql");
			rc = executeSQLQueryJar(sqlpath + "createuser.sql");
			rc = executeSQLQueryJar(sqlpath + "createscrapperlog.sql");
			rc = executeSQLQueryJar(sqlpath + "createfanart.sql");
			rc = executeSQLQueryJar(sqlpath + "createcache.sql");
			rc = executeSQLQueryJar(sqlpath + "createsyslog.sql");
			rc = executeSQLQueryJar(sqlpath + "createactor.sql");
			rc = executeSQLQueryJar(sqlpath + "createmenu.sql");
			rc = executeSQLQueryJar(sqlpath + "createmusic.sql");
                        rc = executeSQLQueryJar(sqlpath + "createmovies.sql");
			rc = executeSQLQueryJar(sqlpath + "createviews.sql");

		} catch (Exception e2) {
			ortus.api.DebugLog(LogLevel.Error, "ortusDB: create tables : Excetpion: " ,e2);
			return false;
		}

		ortus.api.DebugLog(LogLevel.Info, "ortusDB: create table successful");
		return true;
	}

	@OrtusEvent("BackupDB")
	public boolean backupDB() {

		ortus.api.DebugLog(LogLevel.Info, "backupDB: Performing database backup");
		String currentdate = new java.text.SimpleDateFormat("yyyyMMddhhmm").format(new Date());
		String backuppath = ortus.api.GetProperty("ortus/basepath") + java.io.File.separator + "backups";
		File bp = new File(backuppath);
		if (!bp.exists()) {
			bp.mkdirs();
		}
		String backupfile = "ortusdb-" + currentdate + ".zip";
		ortus.api.DebugLog(LogLevel.Info, "backupDB: Backing up to " + backuppath + java.io.File.separator + backupfile);
		int result = executeSQL("backup to '" + backuppath + java.io.File.separator + backupfile + "'");
		if (result == 0) {
			ortus.api.DebugLog(LogLevel.Info, "Backup of sageDB successful");
			return true;
		} else {
			ortus.api.DebugLog(LogLevel.Info, "Backup of sageDB Failed, result: " + result);
			return false;
		}
	}

	public boolean restoreDB(String backupfile) {

		ortus.api.DebugLog(LogLevel.Info, " ortusDB: Restore starting from backup: " + backupfile);

		ShutdownDatabase();

		String backuppath = ortus.api.GetProperty("ortus/basepath") + java.io.File.separator + "backups";

		if (!connectDB(h2protocol + "sageDB;restoreFrom=" + backuppath + java.io.File.separator + backupfile, "sage", "sage")) {
			ortus.api.DebugLog(LogLevel.Error, "ortusDB: Restore failure");
			return false;
		}

		ShutdownDatabase();

		ortus.api.DebugLog(LogLevel.Info, "ortusDB: Restore successful");

		return true;
	}
}
