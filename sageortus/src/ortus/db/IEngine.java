/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.db;

import java.util.List;
import java.sql.Connection;
import java.util.HashMap;

/**
 *
 * @author jphipps
 */
public interface IEngine {
    public void ShutdownDatabase();
    public Connection GetConnection();
    public boolean GetCleanDB();
    public HashMap GetStatus();
    public boolean createDB();
    public boolean backupDB();
    public boolean clearDB();
    public boolean AnalyzeDB();
    public boolean restoreDB(String backupdir);
    public int executeSQL(String sql);
    public List<Object> executeSQLQuery(String sql);
    public List<Object> executeSQLQueryCache(String sql);
    public List<List> executeSQLQueryArray(String sql);
    public List<List> executeSQLQueryArrayCache(String sql);
    public List<HashMap> executeSQLQueryHash(String sql);
    public List<HashMap> executeSQLQueryHashCache(String sql);
    public List<Object> getMediaFilesSQL(String sql);
}
