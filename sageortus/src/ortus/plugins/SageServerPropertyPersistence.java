/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.plugins;

import org.apache.commons.lang.ArrayUtils;
import ortus.mq.vars.EvenType;
import ortus.mq.vars.MsgPriority;
import sagex.api.Configuration;
import sagex.plugin.IPropertyPersistence;

/**
 *
 * @author jphipps
 */
public class SageServerPropertyPersistence implements IPropertyPersistence {
	public final String PROP_LOG_LEVEL = "ortus/log/level";
	public final String PROP_FULL_METADATA_SCAN = "ortus/fanart/fullscan";

	private String[] loglevel = new String[] { "Off", "Fatal", "Error","Warning","Info","Debug","Trace" };

	@Override
	public void set(String property, String value) {
		if ( property.equalsIgnoreCase(PROP_LOG_LEVEL)) {
			int level = ArrayUtils.indexOf(loglevel, value);
			Configuration.SetServerProperty(property, String.valueOf(level));
                        ortus.mq.api.fireMQMessage(MsgPriority.High, EvenType.Broadcast, "ChangeLogLevel", new Object[] { level } );
		}  else {
			Configuration.SetServerProperty(property, value);
		}
	}

	@Override
	public String get(String property, String defvalue) {
		if ( property.equalsIgnoreCase(PROP_LOG_LEVEL)) {
			String level = Configuration.GetServerProperty(property, "6");
			return loglevel[Integer.parseInt(level)];
		} else {
			return Configuration.GetServerProperty(property, defvalue);
		}
	}

}
