/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.web.resources;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ortus.media.OrtusMedia;
import ortus.vars.LogLevel;
import ortus.vars.OrtusMediaType;
import ortus.web.dto.MediaItem;
import sagex.api.Configuration;
import sagex.api.Global;

/**
 *
 * @author jphipps
 */
public class OrtusAPI {

    Logger log = Logger.getLogger(this.getClass());

    public OrtusAPI() {
        log.trace("OrtusAPI: Loading");
    }
    
    public List GetMediaFiles(String filter) {
        List<MediaItem> MediaList = new ArrayList<MediaItem>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        log.trace("OrtusWeb: GetMediaFiles: filter: " + filter);
        try {
        conn = ortus.api.GetConnection();
        stmt = conn.createStatement();
        String SQL = "select * from allmedia ";
        if ( ! filter.isEmpty())
            SQL+="where " + filter;
        log.trace("OrtusWeb: GetMediaFiles: SQL: " + SQL);
        rs = stmt.executeQuery(SQL);
        while ( rs.next()) {
            log.trace("Webservice: " + rs.getString(1));
            MediaItem mf = new MediaItem();
            mf.setTitle(rs.getString("showtitle"));
            mf.setSageid(rs.getInt("mediaid"));
            MediaList.add(mf);
        }
        } catch ( Exception e) {
            log.error("OrtusWeb: GetMediaFiles: Exception: ", e);
        } finally {
            if ( rs != null) try { rs.close(); } catch(Exception e) {}
            if ( stmt != null) try { stmt.close(); } catch(Exception e) {}
            if ( conn != null) try { conn.close(); } catch(Exception e) {}
        }

        return MediaList;
    }

    public Map GetMediaMetadataFull(int sageid) {
        log.trace("GetMediaMetadataFull: Mediaid: " + sageid);
        MediaItem mi = new MediaItem();
        OrtusMedia om = new OrtusMedia(sageid,OrtusMediaType.MediaFile);
        om.SetMediaID(sageid);
        HashMap md = ortus.api.GetMetadata(om);
        return md;
    }


    public MediaItem GetMediaMetadata(int sageid) {
        log.trace("GetMediaMetadataFull: Mediaid: " + sageid);
        MediaItem mi = new MediaItem();
        OrtusMedia om = new OrtusMedia(sageid,OrtusMediaType.MediaFile);
        om.SetMediaID(sageid);
        HashMap md = ortus.api.GetMetadata(om);

        mi.setSageid(sageid);
        mi.setTitle((String)md.get("title"));
        mi.setDescription((String)md.get("description"));
        mi.setEpisodetitle((String)md.get("episodetitle"));
//        mi.setEpisodeid(Integer.parseInt((String)md.get("episodeid")));
        mi.setMediapath((String)md.get("mediapath"));
        mi.setMediaencoding((String)md.get("mediaencoding"));
//        mi.setMediatype(Integer.parseInt((String)md.get("mediatype")));
//        mi.setMediagroup(Integer.parseInt((String)md.get("mediagroup")));
//        mi.setMediasize(Long.parseLong((String)md.get("mediasize")));
//        mi.setMediaduration(Long.parseLong((String)md.get("mediaduration")));
                               
        return mi;
    }

    public HashMap GetServerStatus() {
        log.trace("GetServerStatus");
        HashMap serverstatus = new HashMap();

        try {
            serverstatus.put("ortusversion",ortus.api.GetVersionFull());
            serverstatus.put("sageversion", Configuration.GetProperty("version", ""));
            serverstatus.put("javaversion",System.getProperty("java.version"));
            serverstatus.put("osversion", System.getProperty("os.name"));
            serverstatus.put("java_total_memory",Runtime.getRuntime().totalMemory());
            serverstatus.put("java_free_memory",Runtime.getRuntime().freeMemory());
            serverstatus.put("sageserver",Global.GetServerAddress());
        } catch ( Exception e) {
            log.error("GetServerStatus Exception", e);
        }

        log.trace("GetServerStatus: Returning: " + serverstatus);
        return serverstatus;
    }
}
