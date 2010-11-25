/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.cache;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import ortus.Ortus;
import ortus.configurationEngine;
import ortus.vars;
import ortus.vars;

/**
 *
 * @author jphipps
 */
public class cacheProviderJCS  extends vars implements IcacheProvider{
	private JCS cache = null;
	
	public cacheProviderJCS() {
		ortus.api.DebugLog(LogLevel.Debug,"cache: Loading JCS Instance");

		File cacheccf = new File(configurationEngine.getInstance().getSagePath() + java.io.File.separator + "cache.ccf");

		if ( ! cacheccf.exists() ) {
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(cacheccf);
				IOUtils.copy(getClass().getResourceAsStream("/ortus/resources/cache.ccf"),os);
			} catch (Exception ex) {
				ortus.api.DebugLog(LogLevel.Error, "cacheProviderJCS: Exception: " + ex);
			} finally {
				IOUtils.closeQuietly(os);
			}
		}
		
		try {
//			 JCS.setConfigFilename("cache.ccf");
			 cache = JCS.getInstance("ortuscache");

		} catch (Exception ex) {
			ortus.api.DebugLog(LogLevel.Error,"cache: Exception: " + ex);
		}
		ortus.api.DebugLog(LogLevel.Debug,"cache: Loading Complete");
	}

	public void Put(Object key, Object value) {
//		ortus.api.DebugLog(LogLevel.Trace2,"cache: putting: " + key);
		try {
			cache.put(key, value);
		} catch (CacheException ex) {
			ortus.api.DebugLog(LogLevel.Error,"cache: Put Exception: " + ex);
		}
	}

	public boolean IsKey(Object key) {
		if ( cache.get(key) == null)
			return false;
		else
			return true;
	}

	public Object Get(Object key) {
//		ortus.api.DebugLog(LogLevel.Trace2,"cache: getting: " + key);
		Object x =  cache.get(key);
		if ( x == null) 
			ortus.api.DebugLog(LogLevel.Trace, "Cache Miss: " + key);
		return x;
	}

	public void Remove(Object key) {
		try {
			cache.remove(key);
		} catch (CacheException ex) {}
	}

	public void Clear() {
		try {
			cache.clear();
		} catch (CacheException ex) {
			ortus.api.DebugLog(LogLevel.Error,"cache: Clear Exception: " + ex);
		}
	}

	public Object[] GetKeys() {
		return cache.getGroupKeys("ortuscache").toArray();
	}

	public String GetStats() {
		return cache.getStats();
	}
}
