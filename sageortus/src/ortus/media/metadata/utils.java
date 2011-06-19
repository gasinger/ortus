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
import ortus.media.OrtusMedia;
import sagex.api.MediaFileAPI;

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

    public static int GetMediaID(Object mediafile) {
        int mediaid = 0;
        if (mediafile instanceof Integer) {
            mediaid = (Integer) mediafile;
        } else if ( mediafile instanceof String) {
            try { mediaid = Integer.parseInt((String)mediafile); } catch ( Exception e) {}
        } else if (mediafile instanceof OrtusMedia) {
            if (((OrtusMedia) mediafile).IsMediaFile() || ((OrtusMedia) mediafile).IsEpisode()) {
                mediaid = ((OrtusMedia) mediafile).GetMediaID();
            }
        } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
            mediaid = MediaFileAPI.GetMediaFileID(mediafile);
        }

        return mediaid;
    }

    public static int GetEpisodeID(Object mediafile) {
        int episodeid = 0;
        int mediaid = 0;
        if (mediafile instanceof Integer) {
            episodeid = (Integer) mediafile;
        } else if (mediafile instanceof OrtusMedia) {
            if (((OrtusMedia) mediafile).IsMediaFile() || ((OrtusMedia) mediafile).IsEpisode()) {
                mediaid = ((OrtusMedia) mediafile).GetMediaID();
            } else if (((OrtusMedia) mediafile).IsEpisode()) {
                episodeid = ((OrtusMedia) mediafile).GetID();
            }
        } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
            mediaid = MediaFileAPI.GetMediaFileID(mediafile);
        }

        if (mediaid > 0) {
            List qr = ortus.api.executeSQLQuery("select episodeid from sage.episode where mediaid = " + mediaid);
            if (qr.size() > 0) {
                episodeid = Integer.parseInt((String) qr.get(0));
            }
        }

        return episodeid;
    }

    public static int GetSeriesID(Object mediafile) {
        int seriesid = 0;
        int mediaid = 0;
        if (mediafile instanceof Integer) {
            seriesid = (Integer) mediafile;
        } else if (mediafile instanceof OrtusMedia) {
            if (((OrtusMedia) mediafile).IsMediaFile() || ((OrtusMedia) mediafile).IsEpisode()) {
                mediaid = ((OrtusMedia) mediafile).GetMediaID();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                seriesid = ((OrtusMedia) mediafile).GetID();
            }
        } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
            mediaid = MediaFileAPI.GetMediaFileID(mediafile);
        }

        if (mediaid > 0) {
            List qr = ortus.api.executeSQLQuery("select seriesid from sage.episode where mediaid = " + mediaid);
            if (qr.size() > 0) {
                seriesid = Integer.parseInt((String) qr.get(0));
            }
        }

        return seriesid;
    }

}
