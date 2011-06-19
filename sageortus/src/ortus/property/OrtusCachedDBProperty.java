/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.property;

import java.util.List;
import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;
import ortus.mq.vars.EvenType;
import ortus.vars.LogLevel;

/**
 *
 * @author jphipps
 */
public class OrtusCachedDBProperty implements IProperty {
	private int currentuser;
	private SortedProperties Props = new SortedProperties();
        private boolean updateOccured = false;
        private boolean triggeredReload = false;
	/**
	 * Constructor create a new instance of the propertis object ( used by the daemon class )
	 * 	Uses STV path as the default path for the configuration files
	 */
	public OrtusCachedDBProperty(Object userid) {
               if ( userid instanceof String)
			currentuser = Integer.parseInt((String) userid);
		if ( userid instanceof Integer)
			currentuser = (Integer)userid;
               ortus.api.DebugLogTrace("OrtusCachedDBProperty: Created for user: " + currentuser);
//		Reload(userid);
	}

	/**
	 * Shutdown procedure for the propeties file to store memory properteies to the ortus.properties file
	 */
	public void Shutdown() {
		ortus.api.DebugLog(LogLevel.Trace, "Props: Shutting down" );
	}

        public void Load() {
            Reload(currentuser);
        }

        public void Load(Object userid) {
            	if ( userid instanceof String)
			currentuser = Integer.parseInt((String) userid);
		if ( userid instanceof Integer)
			currentuser = (Integer)userid;

                ortus.api.DebugLogTrace("OrtusCachedDBProperty: Loading user: " + currentuser);
                Props.clear();
                List<List> result = ortus.api.executeSQLQueryArray("select key, propval from sage.properties where userid = " + currentuser);
                for (List x : result) {
                    Props.put(x.get(0),x.get(1));
                }
        }
        
	public void Reload(Object userid) {
                if ( triggeredReload == true) {
                    triggeredReload = false;
                    return;
                }
                int reloadUserid = 0;

                if ( userid instanceof String)
			reloadUserid = Integer.parseInt((String) userid);
		if ( userid instanceof Integer)
			reloadUserid = (Integer)userid;
                if ( currentuser == reloadUserid ) {
                    Load(currentuser);
                }
	}
        
    	/**
	 * Get the value of a property
	 * @param PropertyName OrtusProperty name
	 * @return Value of property name
	 */
	public String GetProperty(String PropertyName, Object defaultvalue) {
		return Props.getProperty(PropertyName,String.valueOf(defaultvalue));

	}

	/**
	 * Remove a property from the ortus.properties file
	 * @param PropertyName OrtusProperty to delete
	 */
	public void RemoveProperty(String PropertyName) {
		Props.remove(PropertyName);
		return;
	}
	/**
	 * Set the value of a property
	 * @param PropertyKey OrtusProperty to modify
	 * @param PropertyValue OrtusProperty value
	 */
	public void SetProperty(String PropertyKey, String PropertyValue) {
		Props.setProperty(PropertyKey,PropertyValue);
                updateOccured = true;
	}

        public void StoreProperty() {
            if ( updateOccured) {
                int result = ortus.api.executeSQL("delete from sage.properties where userid = " + currentuser);
                for ( Object x : Props.keySet()) {
                    result = ortus.api.executeSQL("insert into sage.properties (userid, key, propval) values ( " + currentuser + ",'" + x + "','" + Props.getProperty((String)x) + "')");
                }
                ortus.mq.api.fireMQMessage(EvenType.Broadcast, "UserPropertyReload", new Object[] { currentuser });
                updateOccured=false;
                triggeredReload=true;
            }

	}

}
