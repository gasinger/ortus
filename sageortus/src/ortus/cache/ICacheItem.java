/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.cache;

import java.io.Serializable;
import java.util.HashMap;
import ortus.vars.CacheType;

/**
 *
 * @author jphipps
 */
public interface ICacheItem  {
    public String toString();
    public HashMap toHash();
    public CacheType getType();
    public String getTitle();
    public String getDescription();
}
