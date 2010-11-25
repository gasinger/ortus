/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.metadata.item;

import java.util.HashMap;

/**
 *
 * @author jphipps
 */
public interface IItem {
    public String toString();
    public HashMap toHash();
    public HashMap toHashFull();
    public boolean isValid();
}
