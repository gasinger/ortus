package ortus.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import ortus.vars;

import sagex.api.Utility;
import sagex.api.WidgetAPI;
/**
 * OrtusProperty Class custom ortus.properties class
 * @author jphipps
 *
 */
public class OrtusProperty extends vars implements IProperty {
	private SortedProperties Props = new SortedProperties();
	private String PropsPath;
	/**
	 * Constructor create a new instance of the propertis object ( used by the daemon class )
	 * 	Uses STV path as the default path for the configuration files
	 */
	public OrtusProperty() {
		
		File tp = Utility.GetPathParentDirectory(new File(WidgetAPI.GetCurrentSTVFile()));
		PropsPath = tp.getPath() + java.io.File.separator + "Ortus" + java.io.File.separator + "Configuration" + java.io.File.separator + "ortus.properties";
		
		LoadProps();
		
		return;
	
	}
	/**
	 * Constructor create a new instance of the properties object ( used by the daemon class )
	 * 	Uses a custom path for the configuration files
	 * @param cpath Path for the configuration file to be stores
	 */
	public OrtusProperty(String cpath) {
		Reload(cpath);
	}

	/**
	 * Shutdown procedure for the propeties file to store memory properteies to the ortus.properties file
	 */
	public void Shutdown() {
		ortus.api.DebugLog(LogLevel.Trace, "Props: Shutting down" );
	}

	public void Reload(Object newpath) {
		ortus.api.DebugLog(LogLevel.Trace2,"OrtusProperty: Loading property file: " + newpath);

		File pp = new File((String)newpath);
		if ( ! pp.exists())
			pp.mkdirs();

		PropsPath = newpath + java.io.File.separator + "ortus.properties";
		
		LoadProps();

		return;
	}
	/**
	 * Get the value of a property
	 * @param PropertyName OrtusProperty name
	 * @return Value of property name
	 */
	public String GetProperty(String PropertyName, String defaultvalue) {
		return Props.getProperty(PropertyName,defaultvalue);

	}
	
	/**
	 * Remove a property from the ortus.properties file
	 * @param PropertyName OrtusProperty to delete
	 */
	public void RemoveProperty(String PropertyName) {
		Props.remove(PropertyName);
                try {
			Props.store(new FileOutputStream(new File( PropsPath )), "Ortus");
		} catch ( Exception e) {
			ortus.api.DebugLog(LogLevel.Info, "RemoveProps Exception: " + e );
		}
		return;
	}
	/**
	 * Set the value of a property
	 * @param PropertyKey OrtusProperty to modify
	 * @param PropertyValue OrtusProperty value
	 */
	public void SetProperty(String PropertyKey, String PropertyValue) {
		Props.setProperty(PropertyKey,PropertyValue);
                try {
			Props.store(new FileOutputStream(new File( PropsPath )), "Ortus");
		} catch ( Exception e) {
      			ortus.api.DebugLog(LogLevel.Info, "SetProps Exception: " + e );
		}
	}

	private void LoadProps() {
 		ortus.api.DebugLog(LogLevel.Trace2, "Props: Loading " + PropsPath );
		
		Props.clear();

                File PropFile = new File( PropsPath ) ;
		try {
                        if ( ! PropFile.exists())
                            PropFile.createNewFile();
			FileInputStream propFile = new FileInputStream(PropFile);
			Props.load(propFile);
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Info, "LoadProps Exception: " + e );
			return;
		}
	
        	for ( Enumeration<Object> x = Props.keys(); x.hasMoreElements();) {
                	String tp = (String)x.nextElement();
			ortus.api.DebugLog(LogLevel.Trace2, "Props: loading property: " + tp + " Value: " + Props.getProperty(tp) );
		}
		
		return;
	}
}
