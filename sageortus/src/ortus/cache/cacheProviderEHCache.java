/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.cache;

import java.io.File;
import java.io.FileOutputStream;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.apache.commons.io.IOUtils;
import ortus.Ortus;
import ortus.vars;

/**
 *
 * @author jphipps
 */
public class cacheProviderEHCache  extends vars implements IcacheProvider{
	private CacheManager manager = null;
        Cache cache = null;
	
	public cacheProviderEHCache() {
		ortus.api.DebugLog(LogLevel.Debug,"cache: Loading EHCache Instance");

                //Create a CacheManager using defaults
                manager = CacheManager.create();
        int maxElements = 4096;

   //Create a Cache specifying its configuration.

                cache = new Cache(
                 new CacheConfiguration("ortus", maxElements)
                   .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
                   .overflowToDisk(true)
                   .eternal(false)
                   .timeToLiveSeconds(60)
                   .timeToIdleSeconds(30)
                   .diskPersistent(false)
                   .diskExpiryThreadIntervalSeconds(0));
               manager.addCache(cache);

               ortus.api.DebugLog(LogLevel.Debug,"Cache: " + cache.getStatistics().toString());
               ortus.api.DebugLog(LogLevel.Debug,"cache: Loading Complete");
	}

	public void Put(Object key, Object value) {
//		ortus.api.DebugLog(LogLevel.Trace2,"cache: putting: " + key);
		try {
                        Element e = new Element(key,value);
			cache.put(e);
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
		Element x =  cache.get(key);
		if ( x == null) {
			ortus.api.DebugLog(LogLevel.Trace, "Cache Miss: " + key);
                        return null;
                } else {
                        return x.getValue();
                }
	}

	public void Remove(Object key) {
		try {
			cache.remove(key);
		} catch (CacheException ex) {}
	}

	public void Clear() {
		try {
			cache.removeAll();
		} catch (CacheException ex) {
			ortus.api.DebugLog(LogLevel.Error,"cache: Clear Exception: " + ex);
		}
	}

	public Object[] GetKeys() {
		return cache.getKeys().toArray();
	}

	public String GetStats() {
		return cache.getStatistics().toString();
	}
}
