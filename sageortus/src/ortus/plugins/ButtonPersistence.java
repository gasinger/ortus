/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.plugins;

import sagex.plugin.IPropertyPersistence;

/**
 *
 * @author jphipps
 */
public class ButtonPersistence implements IPropertyPersistence {

	@Override
	public void set(String string, String string1) {
	}

	@Override
	public String get(String string, String string1) {
		if ( ortus.onlinescrapper.api.IsindexMediaRunning())
			return "Cancel";
		else
			return "Scan";
	}



}
