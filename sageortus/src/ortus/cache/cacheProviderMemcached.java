/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.io.IOUtils;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import ortus.Ortus;
import ortus.vars;

/**
 *
 * @author jphipps
 */
public class cacheProviderMemcached  extends vars implements IcacheProvider{

	private MemcachedClient cache = null;
        int expire = 60000;
	
	public cacheProviderMemcached() {
		ortus.api.DebugLog(LogLevel.Debug,"cache: Loading Memcached Instance");
		
		try {
			 cache=new MemcachedClient(new InetSocketAddress("127.0.0.1", 11211));

		} catch (Exception ex) {
			ortus.api.DebugLog(LogLevel.Error,"cache: Exception: " + ex);
		}

                ortus.api.DebugLog(LogLevel.Debug, "Status: " + cache.getStats().toString());
		ortus.api.DebugLog(LogLevel.Debug,"cache: Loading Complete");
	}

	public void Put(Object key, Object value) {
//		ortus.api.DebugLog(LogLevel.Trace2,"cache: putting: " + key);
		try {
			cache.add(String.valueOf(key), expire, value);
		} catch (Exception ex) {
			ortus.api.DebugLog(LogLevel.Error,"cache: Put Exception",ex);
		}
	}

	public boolean IsKey(Object key) {
		if ( cache.get(String.valueOf(key)) == null)
			return false;
		else
			return true;
	}

	public Object Get(Object key) {
//		ortus.api.DebugLog(LogLevel.Trace2,"cache: getting: " + key);
		Object x =  cache.get(String.valueOf(key));
		if ( x == null) 
			ortus.api.DebugLog(LogLevel.Trace, "Cache Miss: " + key);
		return x;
	}

	public void Remove(Object key) {
		try {
			cache.delete(String.valueOf(key));
		} catch (Exception ex) {}
	}

	public void Clear() {
		try {
			cache.flush();
		} catch (Exception ex) {
			ortus.api.DebugLog(LogLevel.Error,"cache: Clear Exception: " + ex);
		}
	}

	public String GetStats() {
		return cache.getStats().toString();
	}

    @Override
    public Object[] GetKeys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
