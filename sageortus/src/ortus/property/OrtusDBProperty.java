package ortus.property;

import java.util.Enumeration;
import java.util.List;
import ortus.vars;

/**
 * OrtusProperty Class custom ortus.properties class
 * @author jphipps
 *
 */
public class OrtusDBProperty extends vars implements IProperty {
	private int currentuser;
//	private SortedProperties Props = new SortedProperties();
	/**
	 * Constructor create a new instance of the propertis object ( used by the daemon class )
	 * 	Uses STV path as the default path for the configuration files
	 */
	public OrtusDBProperty(Object userid) {
		Reload(userid);
	}

	/**
	 * Shutdown procedure for the propeties file to store memory properteies to the ortus.properties file
	 */
	public void Shutdown() {
		ortus.api.DebugLog(LogLevel.Trace, "Props: Shutting down" );
	}

	public void Reload(Object userid) {
		if ( userid instanceof String)
			currentuser = Integer.parseInt((String) userid);
		if ( userid instanceof Integer)
			currentuser = (Integer)userid;
	}
	/**
	 * Get the value of a property
	 * @param PropertyName OrtusProperty name
	 * @return Value of property name
	 */
	public String GetProperty(String PropertyName, String defaultvalue) {
		List<List> result = ortus.api.executeSQLQueryArray("select propval from sage.properties where userid = " + currentuser + " and key = '" + PropertyName + "'");
		if ( result.size() < 1)
			return defaultvalue;
		else
			return (String)result.get(0).get(0);
	}

	/**
	 * Remove a property from the ortus.properties file
	 * @param PropertyName OrtusProperty to delete
	 */
	public void RemoveProperty(String PropertyName) {
//		Props.remove(PropertyName);

		int result = ortus.api.executeSQL("delete from sage.properties where userid = " + currentuser + " and key = '" + PropertyName + "'");
	}
	/**
	 * Set the value of a property
	 * @param PropertyKey OrtusProperty to modify
	 * @param PropertyValue OrtusProperty value
	 */
	public void SetProperty(String PropertyKey, String PropertyValue) {
		int result = ortus.api.executeSQL("update sage.properties set propval = '" + PropertyValue + "' where userid = " + currentuser + " and key = '" + PropertyKey + "'");
		if ( result < 1) {
			result = ortus.api.executeSQL("insert into sage.properties (userid, key, propval) values ( " + currentuser + ",'" + PropertyKey + "','" + PropertyValue + "')");
		}
	}
}
