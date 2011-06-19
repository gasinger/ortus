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
public class OrtusCustomProperty extends vars implements IProperty {
	private SortedProperties Props = new SortedProperties();
        private String PropertyName;
	private String PropsPath;
	/**
	 * Constructor create a new instance of the propertis object ( used by the daemon class )
	 * 	Uses STV path as the default path for the configuration files
	 */
	public OrtusCustomProperty(String propertyname) {

                ortus.api.DebugLog(LogLevel.Trace, "OrtusCustomProperty: Creating/Loading property file: " + propertyname);
                PropertyName = propertyname;
		PropsPath = ortus.api.GetOrtusBasePath() + java.io.File.separator + "Configuration" + java.io.File.separator + propertyname + ".properties";
		
		LoadProps();
		
		return;
	
	}

	public void Reload() {
		ortus.api.DebugLog(LogLevel.Trace2,"OrtusCustomProperty: Reloading property file: " + PropertyName);

		LoadProps();

		return;
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
                try {
			Props.store(new FileOutputStream(new File( PropsPath )), PropertyName);
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
			Props.store(new FileOutputStream(new File( PropsPath )), PropertyName);
		} catch ( Exception e) {
      			ortus.api.DebugLog(LogLevel.Info, "SetProps Exception: " + e );
		}
	}

	private void LoadProps() {
 		ortus.api.DebugLog(LogLevel.Trace, "OrtusCustomProperty: Loading " + PropsPath );
		
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

    @Override
    public void Reload(Object newpath) {
        Reload();
    }

    @Override
    public void Shutdown() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void StoreProperty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void Load() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void Load(Object userid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
