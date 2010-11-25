/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.cache;

import java.util.HashMap;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import ortus.vars;

/**
 *
 * @author jphipps
 */
public class cacheProviderOrtus extends vars implements IcacheProvider {

	HashMap<Object,Object> cache = new HashMap<Object,Object>(4000);
	long total_hit = 0;
	long total_miss = 0;
	long total_put = 0;
	long total_get = 0;
	
	public cacheProviderOrtus() {
		ortus.api.DebugLog(LogLevel.Trace2,"cache: Loading Ortus Instance");
		ortus.api.DebugLog(LogLevel.Trace2,"cache: Loading Complete");
	}

	public void Put(Object key, Object value) {
//		ortus.api.DebugLog(LogLevel.Trace2,"cache: putting: " + key);
			total_put++;
			cache.put(key, value);
	}

	public boolean IsKey(Object key) {
		if ( cache.get(key) == null)
			return false;
		else
			return true;
	}

	public Object Get(Object key) {
//		ortus.api.DebugLog(LogLevel.Trace2,"cache: getting: " + key);
		total_get++;
		Object x =  cache.get(key);
		if ( x == null) {
			total_miss++;
			ortus.api.DebugLog(LogLevel.Info, "Cache Miss: " + key);
		} else
			total_hit++;
		return x;
	}

	public void Remove(Object key) {
			cache.remove(key);
	}

	public void Clear() {
			cache.clear();
	}

	public Object[] GetKeys() {
		return cache.keySet().toArray();
	}

	public String GetStats() {
		StringBuffer sb = new StringBuffer(100);
		sb.append("Ortus Cache Stats");
		sb.append("\nQueue Size: " + cache.size());
		sb.append("\nTotal Put: " + total_put);
		sb.append("\nTotal Get: " + total_get);
		sb.append("\nTotal Hit: " + total_hit);
		sb.append("\nTotal Miss: " + total_miss);
		return sb.toString();
	}
}
