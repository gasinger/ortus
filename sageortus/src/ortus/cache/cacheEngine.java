/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.cache;

import ortus.media.metadata.item.Series;
import ortus.media.metadata.item.Media;
import ortus.media.metadata.item.Fanart;
import ortus.media.metadata.item.Episode;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import ortus.media.OrtusMedia;
import ortus.media.metadata.item.IItem;
import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;
import ortus.vars.LogLevel;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;

/**
 *
 * @author jphipps
 */
public class cacheEngine extends EventListener {
	static cacheEngine INSTANCE = null;
	IcacheProvider icp = null;

	public static cacheEngine getInstance() {
		if ( INSTANCE == null ) {
		    synchronized(cacheEngine.class) {
			if ( INSTANCE == null )
			   INSTANCE = new cacheEngine();
		    }
		}
		return INSTANCE;
	}

	public cacheEngine() {
		super();
		ortus.api.DebugLog(LogLevel.Debug,"cacheEngine: Loading Instance");

		if ( Configuration.GetServerProperty("ortus/cache", "JCS").equals("JCS"))
			icp = new cacheProviderJCS();
		else if ( Configuration.GetServerProperty("ortus/cache", "JCS").equalsIgnoreCase("memcached"))
                        icp = new cacheProviderMemcached();
		else if ( Configuration.GetServerProperty("ortus/cache", "JCS").equalsIgnoreCase("ehcache"))
                        icp = new cacheProviderEHCache();

                else
			icp = new cacheProviderOrtus();

	}

	public IcacheProvider getProvider() {
		return icp;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Cache Table:\n");
		Object[] keys = icp.GetKeys();
		for ( Object x : keys) {
			sb.append("\nKeys: " + x);
		}
		return sb.toString();
	}
        
	@OrtusEvent("PreloadCache")
	public void PreLoadCache() {
            int total = 0;
            int count = 0;
            try {
                long t0 = System.currentTimeMillis();
		ortus.api.DebugLog(LogLevel.Info,"cacheEngine: Preloading Cache");
		List<List> result = ortus.api.executeSQLQueryArray("select mediaid from allmedia where mediatype in (0,1,2,3,4)");
		if ( result.size() > 0) {
        		for ( int x = 0; x < result.size();x++) {
                            count++;
                            total++;
                            ortus.media.metadata.item.Media im = ortus.media.metadata.metadataEngine.getInstance().getProvider().GetMetadataMedia(result.get(x).get(0));

//                            ortus.api.DebugLogTrace("Cache Loading: <MD" + result.get(x).get(0) +">");
                            icp.Put("MD"+result.get(x).get(0),im);
                            if ( count > 100) {
                                count=0;
                                ortus.api.DebugLog(LogLevel.Debug,"...cache loaded another 100...total: " + total);
                            }
			}
		}
                long t1 = System.currentTimeMillis() - t0;
		ortus.api.DebugLog(LogLevel.Info,"cacheEngine: Preloaded " + result.size() + " items in " + t1 + " ms");
            } catch(Exception e) {
                ortus.api.DebugLog(LogLevel.Error,"PreLoadCache: Exception:",e);
            }
	}

	@OrtusEvent("ClearCache")
	public void ClearCache() {
		icp.Clear();
	}

	@OrtusEvent("ReloadCache")
	public void ReLoadCache() {
		icp.Clear();
		PreLoadCache();
	}

	@OrtusEvent("ReloadMediaCache")
	public void ReLoadCache(Object mediakey) {
		icp.Remove(mediakey);
		GetCache(mediakey);
	}
	
	public Object GetCache(Object mediakey) {
                if ( ((String)mediakey).equals("MD0") || ((String)mediakey).equals("EP999"))
                    return new Media();
//		ortus.api.DebugLog(LogLevel.Trace2,"GetCache: mediaid: " + mediakey);
//			ortus.api.DebugLog(ERROR,"GetCache: zero mediaid");
		
		Object ci = icp.Get(mediakey);
		if ( ci != null)
			return ci;

                IItem ii = null;
                if ( mediakey instanceof String) {
                    if ( ortus.media.metadata.metadataEngine.getInstance().getProvider().IsMetadataKeyMedia((String)mediakey)) {
                        ii = ortus.media.metadata.metadataEngine.getInstance().getProvider().GetMetadataMedia(ortus.media.metadata.metadataEngine.getInstance().getProvider().GetMetadataKeyValue((String)mediakey));
                    } else if ( ortus.media.metadata.metadataEngine.getInstance().getProvider().IsMetadataKeyEpisode((String)mediakey)) {
                        ii = ortus.media.metadata.metadataEngine.getInstance().getProvider().GetMetadataEpisode(ortus.media.metadata.metadataEngine.getInstance().getProvider().GetMetadataKeyValue((String)mediakey));
                    } else if ( ortus.media.metadata.metadataEngine.getInstance().getProvider().IsMetadataKeySeries((String)mediakey)) {
                        ii = ortus.media.metadata.metadataEngine.getInstance().getProvider().GetMetadataSeries(ortus.media.metadata.metadataEngine.getInstance().getProvider().GetMetadataKeyValue((String)mediakey));
                    }
                }

                if ( ii != null)
                    icp.Put(mediakey,ii);

		return ii;
	}
}
