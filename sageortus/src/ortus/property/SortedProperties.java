/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.property;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class SortedProperties extends Properties {

	public Enumeration keys() {
		Enumeration keysEnum = super.keys();
		Vector<String> keyList = new Vector<String>();
		while (keysEnum.hasMoreElements()) {
			keyList.add((String) keysEnum.nextElement());
		}
		Collections.sort(keyList);
		return keyList.elements();
	}
}

