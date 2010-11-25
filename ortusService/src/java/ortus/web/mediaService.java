/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ortus.web.dto.MediaItem;
import ortus.web.resources.OrtusAPI;
import sagex.api.Configuration;

/**
 *
 * @author jphipps
 */
public class mediaService {

    Logger log = Logger.getLogger(this.getClass());

    public mediaService() {
        log.trace("mediaService: Loading");
    }

    public void setProperty(String prop, String value) {
        ortus.api.SetProperty(prop, value);
    }

    public String getProperty(String prop) {
        return ortus.api.GetProperty(prop);
    }

    public String getProperty(String prop, String defval) {
        return ortus.api.GetProperty(prop, defval);
    }

    public void setSageProperty(String prop, String value) {
        ortus.api.SetProperty(prop, value);
    }

    public String getSageProperty(String prop) {
        return ortus.api.GetProperty(prop);
    }

    public String getSageProperty(String prop, String defval) {
        return ortus.api.GetProperty(prop, defval);
    }

    public void setSageServerProperty(String prop, String value) {
        Configuration.SetServerProperty(prop, value);
    }

    public String getSageServerProperty(String prop) {
        return Configuration.GetServerProperty(prop,null);
    }

    public String getSageServerProperty(String prop, String defval) {
        return Configuration.GetServerProperty(prop, defval);
    }

    public HashMap GetServerStatus() {
        OrtusAPI oapi = new OrtusAPI();
        return oapi.GetServerStatus();
    }
    
    public Object Search(String filter) {
        return ortus.api.Search(filter);
    }
    public List getMedia(String value) {
        OrtusAPI oapi = new OrtusAPI();
        return oapi.GetMediaFiles(value);
    }

    public Map getMetadataFull(int sageid) {
        OrtusAPI oapi = new OrtusAPI();
        return oapi.GetMediaMetadataFull(sageid);
    }

    public MediaItem getMediaMetadata(int sageid) {
        OrtusAPI oapi = new OrtusAPI();
        return oapi.GetMediaMetadata(sageid);
    }

    public void indexMedia() {
        ortus.api.indexMedia(0, 1);
    }

    public void indexMediaFull() {
        ortus.api.indexMedia(1, 1);
    }

    public HashMap getScrapperDetail() {
        return ortus.api.GetScrapperDetail();
    }

    public List<HashMap> getLiveSearch(String scope, String title) {
        return ortus.api.LiveSearch(scope, title);
    }

    public void storeLiveSearch(HashMap entry) {
        ortus.api.LiveSearchStore(entry);
    }
    
    public void startMetadataScan() {
        ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Server, "FullScan", new Object[] { 1, 1 } );
    }

    public boolean isIndexMediaRunning() {
	return ortus.api.IsindexMediaRunning();
    }

    public void cancelIndexMedia() {
        ortus.api.CancelIndexMedia();
    }

    public void getMissingFanart() {
        ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Server, "FanartScan", new Object[] {} );
    }

}
