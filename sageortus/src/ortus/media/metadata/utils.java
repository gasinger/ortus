/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.metadata;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

/**
 *
 * @author jphipps
 */
public class utils {


    public static Object GetMetadataFromDB(String table, String column,Object key) {

        Connection conn= ortus.api.GetConnection();
        QueryRunner qr = new QueryRunner();

        try {
            List<Map<String,Object>> records = qr.query(conn,"select " + column + " from " + table + " where " + key, new MapListHandler());
            if ( records.size() > 0) {
                return records.get(0).get(column);
            } else
                return null;
        } catch ( Exception e) {
            ortus.api.DebugLogError("GetMetadataFromDB:",e);
        } finally {
            try { DbUtils.close(conn); } catch(Exception e) {}
        }
        
        return null;
    }
}
