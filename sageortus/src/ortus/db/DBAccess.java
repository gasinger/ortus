/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ortus.configurationEngine;
import sagex.api.MediaFileAPI;

/**
 *
 * @author jphipps
 */
public class DBAccess extends ortus.vars {

   public DBAccess() {
   }
     /**
     * Execute DDL or update query such as create, insert, update, delete
     * @param sql SQL to perform
     * @return truefalse for success or failure.  Failed calls will log error message to the debuglog
     */
    public int executeSQL(Connection conn, String sql) {
	    ortus.api.DebugLog(LogLevel.Trace,"executeSQL: Executing: " + sql);
	    Statement stmt = null;
        try {
          stmt = conn.createStatement();
          stmt.execute(sql);
          int UpdateCount = stmt.getUpdateCount();
          return UpdateCount;
        } catch(SQLException e ) {
	    try {  conn.close(); } catch( Exception ex) {}
            ortus.api.DebugLog(LogLevel.Error, "executeSQL: SQL: " + sql);
            ortus.api.DebugLog(LogLevel.Error, "executeSQL: SQLstate: " + e.getSQLState() );
            ortus.api.DebugLog(LogLevel.Error, "executeSQL: SQLException: " , e);
            return -1;
        } finally {
		if ( stmt != null)
			try { stmt.close(); } catch(Exception e) {}
		if ( conn != null)
			try { conn.close(); } catch(Exception e) {}
	}
    }
/**
 * Execute a query to return data such as select
 * @param sql SQL to execute
 * @return An array of results
 */
    public List<Object> executeSQLQuery(Connection conn, String sql) {
        List<Object> ra = new ArrayList<Object>();
	Statement stmt = null;
	ResultSet rs = null;
        try {
          stmt = conn.createStatement();
          rs = stmt.executeQuery(sql);
          while( rs.next()) {
              StringBuffer returnrow = new StringBuffer();
              int fCount = 0;
              ResultSetMetaData rsmd = rs.getMetaData();
              for ( int x = 1; x <= rsmd.getColumnCount();x++) {
                 fCount++;
                  if( fCount > 1)
                      returnrow.append(",");
                  if ( rs.getString(x) != null)
                      returnrow.append(rs.getString(x).trim());
                  else
                      returnrow.append("");
              }
              ra.add(returnrow.toString());
          }
          return ra;
        } catch(Exception e ) {
            ortus.api.DebugLog(LogLevel.Error,"SQL: " + sql);
            ortus.api.DebugLog( LogLevel.Error, "SQLException: " , e);
            return null;
        } finally {
		if ( rs != null)
			try { rs.close(); } catch(Exception e) {}
		if( stmt != null)
			try { stmt.close(); } catch(Exception e) {}
		if ( conn != null)
			try { conn.close(); } catch(Exception e) {}
	}
    }

/**
 * Execute a query to return data such as select
 * @param sql SQL to execute
 * @return An array of results
 */
    public List<Object> executeSQLQueryCache(Connection conn, String sql) {
	Object ci = ortus.cache.cacheEngine.getInstance().getProvider().Get(ortus.api.MD5Sum("Query:" + sql));
	if ( ci != null) {
		try {  conn.close(); } catch( Exception ex) {}
		return (List)ci;
	}

	Statement stmt = null;
	ResultSet rs = null;
        List<Object> ra = new ArrayList<Object>();
        try {
          stmt = conn.createStatement();
          rs = stmt.executeQuery(sql);
          while( rs.next()) {
              StringBuffer returnrow = new StringBuffer();
              int fCount = 0;
              ResultSetMetaData rsmd = rs.getMetaData();
              for ( int x = 1; x <= rsmd.getColumnCount();x++) {
                 fCount++;
                  if( fCount > 1)
                      returnrow.append(",");
                  if ( rs.getString(x) != null)
                      returnrow.append(rs.getString(x).trim());
                  else
                      returnrow.append("");
              }
              ra.add(returnrow.toString());
          }
	  ortus.cache.cacheEngine.getInstance().getProvider().Put(ortus.api.MD5Sum("Query:" + sql), ra);
          return ra;
        } catch(Exception e ) {
            ortus.api.DebugLog(LogLevel.Error,"SQL: " + sql);
            ortus.api.DebugLog( LogLevel.Error, "SQLException: " , e);
            return null;
        } finally {
		if ( rs != null)
			try { rs.close(); } catch(Exception e) {}
		if( stmt != null)
			try { stmt.close(); } catch(Exception e) {}
		if ( conn != null)
			try { conn.close(); } catch(Exception e) {}
	}
    }
/**
 * Execute a query to return data such as select
 * @param sql SQL to execute
 * @return An array of results
 */
    public List<List> executeSQLQueryArray(Connection conn, String sql) {
        List<List> ra = new ArrayList<List>();
	Statement stmt = null;
	ResultSet rs = null;
        try {
          stmt = conn.createStatement();
          rs = stmt.executeQuery(sql);
          while( rs.next()) {
              List recarray = new ArrayList();
              ResultSetMetaData rsmd = rs.getMetaData();
              for ( int x = 1; x <= rsmd.getColumnCount();x++) {
                  recarray.add(rs.getString(x));
              }
              ra.add(recarray);
          }
          return ra;
        } catch(Exception e ) {
            ortus.api.DebugLog(LogLevel.Error,"SQL: " + sql);
            ortus.api.DebugLog( LogLevel.Error, "SQLException: " , e);
            return null;
        } finally {
		if ( rs != null)
			try { rs.close(); } catch(Exception e) {}
		if( stmt != null)
			try { stmt.close(); } catch(Exception e) {}
		if ( conn != null)
			try { conn.close(); } catch(Exception e) {}
	}
    }
/**
 * Execute a query to return data such as select
 * @param sql SQL to execute
 * @return An array of results
 */
    public List<List> executeSQLQueryArrayCache(Connection conn, String sql) {
	Object ci = ortus.cache.cacheEngine.getInstance().getProvider().Get(ortus.api.MD5Sum("Array:" + sql));
	if ( ci != null) {
		try {  conn.close(); } catch( Exception ex) {}
		return (List)ci;
	}

        List<List> ra = new ArrayList<List>();
	Statement stmt = null;
	ResultSet rs = null;
        try {
          stmt = conn.createStatement();
          rs = stmt.executeQuery(sql);
          while( rs.next()) {
              List recarray = new ArrayList();
              ResultSetMetaData rsmd = rs.getMetaData();
              for ( int x = 1; x <= rsmd.getColumnCount();x++) {
                  recarray.add(rs.getString(x));
              }
              ra.add(recarray);
          }
	  ortus.cache.cacheEngine.getInstance().getProvider().Put(ortus.api.MD5Sum("Array:" + sql), ra);
          return ra;
        } catch(Exception e ) {
            ortus.api.DebugLog(LogLevel.Error,"SQL: " + sql);
            ortus.api.DebugLog( LogLevel.Error, "SQLException: " , e);
            return null;
        } finally {
		if ( rs != null)
			try { rs.close(); } catch(Exception e) {}
		if( stmt != null)
			try { stmt.close(); } catch(Exception e) {}
		if ( conn != null)
			try { conn.close(); } catch(Exception e) {}
	}
    }

/**
 * Execute a query to return data such as select
 * @param sql SQL to execute
 * @return An array of results
 */
    public List<HashMap> executeSQLQueryHash(Connection conn, String sql) {
        List<HashMap> ra = new ArrayList<HashMap>();
	Statement stmt = null;
	ResultSet rs = null;
        try {
          stmt = conn.createStatement();
          rs = stmt.executeQuery(sql);
          while( rs.next()) {
              List recarray = new ArrayList();
              ResultSetMetaData rsmd = rs.getMetaData();
	      HashMap rechash = new HashMap();
              for ( int x = 1; x <= rsmd.getColumnCount();x++) {
		      rechash.put(rsmd.getColumnName(x), rs.getString(x));
              }
              ra.add(rechash);
          }
          return ra;
        } catch(Exception e ) {
            ortus.api.DebugLog(LogLevel.Error,"SQL: " + sql);
            ortus.api.DebugLog( LogLevel.Error, "SQLException: " , e);
            return null;
        } finally {
		if ( rs != null)
			try { rs.close(); } catch(Exception e) {}
		if( stmt != null)
			try { stmt.close(); } catch(Exception e) {}
		if ( conn != null)
			try { conn.close(); } catch(Exception e) {}
	}
    }
/**
 * Execute a query to return data such as select
 * @param sql SQL to execute
 * @return An array of results
 */
    public List<HashMap> executeSQLQueryHashCache(Connection conn, String sql) {
	Object ci = ortus.cache.cacheEngine.getInstance().getProvider().Get(ortus.api.MD5Sum("Hash:" + sql));
	if ( ci != null) {
		try {  conn.close(); } catch( Exception ex) {}
		return (List)ci;
	}

        List<HashMap> ra = new ArrayList<HashMap>();
	Statement stmt = null;
	ResultSet rs = null;
        try {
          stmt = conn.createStatement();
          rs = stmt.executeQuery(sql);
          while( rs.next()) {
              List recarray = new ArrayList();
              ResultSetMetaData rsmd = rs.getMetaData();
 	      HashMap rechash = new HashMap();
              for ( int x = 1; x <= rsmd.getColumnCount();x++) {
		      rechash.put(rsmd.getColumnName(x), rs.getString(x));
              }
              ra.add(rechash);
          }
	  ortus.cache.cacheEngine.getInstance().getProvider().Put(ortus.api.MD5Sum("Hash:" + sql), ra);
          return ra;
        } catch(Exception e ) {
            ortus.api.DebugLog(LogLevel.Error,"SQL: " + sql);
            ortus.api.DebugLog( LogLevel.Error, "SQLException: " , e);
            return null;
        } finally {
		if ( rs != null)
			try { rs.close(); } catch(Exception e) {}
		if( stmt != null)
			try { stmt.close(); } catch(Exception e) {}
		if ( conn != null)
			try { conn.close(); } catch(Exception e) {}
	}
    }

    public int executeSQLQueryFile(String sqlfile) {
//        List<String> sql = ortus.util.dump.DumpLogFile(sqlfile);
	List<String> sql = configurationEngine.getInstance().LoadJarFile(sqlfile);
        int totalrows = 0;

        StringBuffer sqlglob = new StringBuffer();
        for ( String x : sql) {
            x = x.trim();
            if ( ! x.isEmpty())
               sqlglob.append(" " + x);
        }

        String[] sqlstmts = sqlglob.toString().split(";");
        for ( String s : sqlstmts) {
        ortus.api.DebugLog(LogLevel.Trace2, "executeSQLQueryFile: executing: " + s);
            totalrows+=executeSQL(ortus.api.GetConnection(), s);
        }
        return totalrows;
    }
     /**
     * Return an array of media objects that matches the passed where clause
     * @param sql where cluase
     * @return Media Object Array
     */
    public List<Object> getMediaFilesSQL(Connection conn, String sql) {
        List<Object> mfl = new ArrayList<Object>();
        Statement stmt = null;
	ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while ( rs.next() ) {
		    if ( rs.getInt("mediaid") > 0)
			mfl.add(MediaFileAPI.GetMediaFileForID(rs.getInt("mediaid")));
            }

            return mfl;

        } catch (SQLException ex) {
            ortus.api.DebugLog(LogLevel.Error,"SQLException: " + ex);
        } finally {
		if ( rs != null)
			try { rs.close(); } catch(Exception e) {}
		if( stmt != null)
			try { stmt.close(); } catch(Exception e) {}
		if ( conn != null)
			try { conn.close(); } catch(Exception e) {}
	}
        return null;
    }
    
    public boolean createDB() {
       int rc = 0;
//       String sqlpath = ortus.api.GetProperty("ortus/basepath","") + java.io.File.separator + "sql" + java.io.File.separator;
       String sqlpath = "/ortus/resources/";
       ortus.api.DebugLog(LogLevel.Info, "ortusDB: building tables using connection pool");
       try {
         rc = executeSQLQueryFile(sqlpath + "createschema.sql");
         rc = executeSQLQueryFile(sqlpath + "createaliases.sql");
         rc = executeSQLQueryFile(sqlpath + "createmedia.sql");
         rc = executeSQLQueryFile(sqlpath + "createmetadata.sql");
         rc = executeSQLQueryFile(sqlpath + "createtv.sql");
         rc = executeSQLQueryFile(sqlpath + "createuser.sql");
         rc = executeSQLQueryFile(sqlpath + "createviews.sql");
         rc = executeSQLQueryFile(sqlpath + "createscrapperlog.sql");
         rc = executeSQLQueryFile(sqlpath + "createfanart.sql");
         rc = executeSQLQueryFile(sqlpath + "createcache.sql");
         rc = executeSQLQueryFile(sqlpath + "createsyslog.sql");
	 rc = executeSQLQueryFile(sqlpath + "createactor.sql");

       } catch ( Exception e2) {
         ortus.api.DebugLog(LogLevel.Error, "ortusDB: create tables : Excetpion: " + e2);
         return false;
       }

       ortus.api.DebugLog(LogLevel.Info, "ortusDB: create table successful");
       return true;
    }
}
