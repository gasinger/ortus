/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.cache;

/**
 *
 * @author jphipps
 */
public interface IcacheProvider {
	public void Put(Object key, Object value);
	public boolean IsKey(Object key);
	public Object Get(Object key);
	public void Remove(Object key);
	public void Clear();
	public Object[] GetKeys();
	public String GetStats();
}
