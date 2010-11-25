/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sagex.h2;

import java.util.List;
import java.sql.Connection;
import java.util.HashMap;

/**
 *
 * @author jphipps
 */
public interface IEngine {
    public boolean ConnectDB(String connectinfo, String database, String user, String passwd);
    public void ShutdownDatabase();
    public void StartTcpServer();
    public void StopTcpServer();
    public boolean IsTcpServerRunning();
    public void StartWebServer();
    public void StopWebServer();
    public boolean IsWebServerRunning();
    public void StartPgServer();
    public void StopPgServer();
    public boolean IsPgServerRunning();
    public Connection GetConnection(String dbname);
    public Object[] GetDatabases();
    public List<Object> GetStatus(String dbname);
    public boolean backupDB();
    public boolean restoreDB(String backupdir);
    public int executeSQL(String dbname, String sql);
    public List<List> executeSQLQuery(String dbname, String sql);
    public List<HashMap> executeSQLQueryHash(String dbname, String sql);
}
