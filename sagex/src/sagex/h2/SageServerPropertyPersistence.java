/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sagex.h2;

import sagex.api.Configuration;
import sagex.plugin.IPropertyPersistence;

/**
 *
 * @author jphipps
 */
class SageServerPropertyPersistence implements IPropertyPersistence {
	@Override
	public void set(String property, String value) {
		Configuration.SetServerProperty(property, value);
	}

	@Override
	public String get(String property, String defvalue) {
		return Configuration.GetServerProperty(property, defvalue);
	}

}
