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
public class OrtusPropertyPersistence implements IPropertyPersistence {

	@Override
	public void set(String property, String value) {
		ortus.api.SetProperty(property, value);
	}

	@Override
	public String get(String property, String defvalue) {
		return ortus.api.GetProperty(property, defvalue);
	}

}
