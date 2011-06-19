package ortus.ui.theme;

import ortus.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;
import ortus.vars.LogLevel;
import sagex.UIContext;
import sagex.api.Global;

/**
 * Theme class 
 *  	Calls to interface with the theme.properties file
 * @author jphipps
 *
 */
public class themeEngine extends EventListener {
        private Properties DefaultProps = new Properties();
	private Properties ThemeProps = new Properties();
	private String ThemePath;
	private String CurrentTheme;
	
	/**
	 * Constructor for the Theme object
	 * @param themePath Custom path for configuration files
	 */
	public themeEngine() {
		super();
		
		ThemePath = Ortus.getInstance().getBasePath() + java.io.File.separator + "Themes";
		
//		LoadTheme("Default");
	
		return;
	}

	@OrtusEvent("Shutdown")
	public void Shutdown() {
		ortus.api.DebugLog(LogLevel.Info, "themeEngine: Shutdown");
		return;
	}
     	/**
	 * Get the theme value from the theme.properties file of the current theme
	 * @param PropertyName
	 * @return Value of the passed property name
	 */
	public String GetThemePropertyPath(String PropertyName) {

                String worktheme = CurrentTheme;

                String propertyvalue = ThemeProps.getProperty(PropertyName);
                if ( propertyvalue == null) {
                    propertyvalue = DefaultProps.getProperty(PropertyName);
                    ortus.api.DebugLog(LogLevel.Trace, "Loading: " + PropertyName + " from default");
                    worktheme = "Default";
                }

                if ( propertyvalue == null) {
                    ortus.api.DebugLog(LogLevel.Error, "GetThemePropertyImage: " + PropertyName + " not found");
                    return "";
                }

                String propvalue="";

                try {
                    String fs = "";
                    if ( ! java.io.File.separator.equals("/")) {
                        fs = "\\" + java.io.File.separator;
                        propvalue = propertyvalue.replaceAll("/", fs );
                    } else {
                        propvalue = propertyvalue;
                    }
                } catch ( Exception e ) {
                    ortus.api.DebugLog(LogLevel.Error, "GetThemePropertyImage exception: " + e);
                }

                return ThemePath + java.io.File.separator + worktheme + java.io.File.separator + propvalue;
	}

	/**
	 * Get the theme value from the theme.properties file of the current theme
	 * @param PropertyName
	 * @return Value of the passed property name
	 */
	public String GetThemeProperty(String PropertyName) {
		if ( ThemeProps.getProperty(PropertyName) != null )
                    return ThemeProps.getProperty(PropertyName);
                else
                    return DefaultProps.getProperty(PropertyName);
	}
	/**
	 * Remove a theme property from the theme.property file
	 * @param PropertyName Property Name to remove
	 */
	public void RemoveThemeProperty(String PropertyName) {
		ThemeProps.remove(PropertyName);
		try {
			ThemeProps.store(new FileOutputStream(new File( ThemePath + java.io.File.separator + CurrentTheme + java.io.File.separator + "theme.properties")), "comments");
		} catch ( Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "Theme: " + e );
		}
	}
	/**
	 * Set a value in the theme.properites file for the current theme
	 * @param PropertyKey Property Name
	 * @param PropertyValue Property Value
	 */
	public void SetThemeProperty(String PropertyKey, String PropertyValue) {
		ThemeProps.setProperty(PropertyKey,PropertyValue);
		try {
			ThemeProps.store(new FileOutputStream(new File( ThemePath + java.io.File.separator + CurrentTheme + java.io.File.separator + "theme.properties")), "comments");
		} catch ( Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "Theme: " + e );
		}
	}
	/**
	 * Load a new theme 
	 * @param theme Theme name to be loaded
	 */
	public void LoadTheme(String theme) {
		
		CurrentTheme = theme;

                DefaultProps.clear();
                ThemeProps.clear();
		
		try {
			ortus.api.DebugLog(LogLevel.Debug, "Theme: loading defaults" );
			FileInputStream defpropFile = new FileInputStream(new File( ThemePath + java.io.File.separator + "Default" + java.io.File.separator + "theme.properties"));
			DefaultProps.load(defpropFile);

			ortus.api.DebugLog(LogLevel.Debug, "Theme: loading theme: " + CurrentTheme );
			FileInputStream propFile = new FileInputStream(new File( ThemePath + java.io.File.separator + CurrentTheme + java.io.File.separator + "theme.properties"));
			ThemeProps.load(propFile);
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "Theme Exception: " + e );
		}

		UIContext ctx = new UIContext(Global.GetUIContextName());
		for ( Enumeration<Object> x = DefaultProps.keys(); x.hasMoreElements();) {
			String tp = (String)x.nextElement();
			if ( tp.toLowerCase().endsWith("path")) {
				ortus.api.DebugLog(LogLevel.Trace, "Theme: loading property: " + tp + " STV var: " + "g" + tp.replaceAll("/","") + " Value: " + ThemePath + java.io.File.separator + "Default" + java.io.File.separator + ortus.util.file.filterFileSeperator(DefaultProps.getProperty(tp)));
				Global.AddGlobalContext(ctx, "g" + tp.replaceAll("/",""), ThemePath + java.io.File.separator + "Default" + java.io.File.separator + DefaultProps.getProperty(tp));
			} else {
				ortus.api.DebugLog(LogLevel.Trace, "Theme: loading property: " + tp + " STV var: " + "g" + tp.replaceAll("/","") + " Value: " + DefaultProps.getProperty(tp) );
				Global.AddGlobalContext(ctx, "g" + tp.replaceAll("/",""), DefaultProps.getProperty(tp));
			}

		}

		for ( Enumeration<Object> x = ThemeProps.keys(); x.hasMoreElements();) {
			String tp = (String)x.nextElement();
			if ( tp.toLowerCase().endsWith("path")) {
				ortus.api.DebugLog(LogLevel.Trace, "Theme: loading property: " + tp + " STV var: " + "g" + tp.replaceAll("/","") + " Value: " + ThemePath + java.io.File.separator + theme + java.io.File.separator + ortus.util.file.filterFileSeperator(ThemeProps.getProperty(tp)));
				Global.AddGlobalContext(ctx, "g" + tp.replaceAll("/",""), ThemePath + java.io.File.separator + theme + java.io.File.separator + ThemeProps.getProperty(tp));
			} else {
				ortus.api.DebugLog(LogLevel.Trace, "Theme: loading property: " + tp + " STV var: " + "g" + tp.replaceAll("/","") + " Value: " + ThemeProps.getProperty(tp) );
				Global.AddGlobalContext(ctx, "g" + tp.replaceAll("/",""), ThemeProps.getProperty(tp));
			}

		}
		
		return;
	}
	/**
	 * Get the theme description for a certain theme
	 * @param theme Theme to return the description 
	 * @return The description of the requested theme
	 */
	public String GetThemeDesc(String theme) {
		Properties tp = new Properties();
		
		try {
			tp.load(new FileInputStream(new File(ThemePath + java.io.File.separator + theme + java.io.File.separator + "theme.properties")));
		} catch ( Exception e ) {
			ortus.api.DebugLog(LogLevel.Error, "GetThemeDesc: " + e );
			return theme;
		}
		
		return tp.getProperty("description",theme);
	}
	/**
	 * Return a list of installed themes
	 * @return Array of themes
	 */
	public Object[] GetThemes() {
		File ThemeDir = new File(ThemePath);
		File Themes[];
		List<Object> themeList = new ArrayList<Object>();
		
		ortus.api.DebugLog(LogLevel.Trace, "ListTheme for: " + ThemePath );
		
		try {
			Themes = ThemeDir.listFiles();
			for ( int x = 0 ; x < Themes.length; x++) {
				if ( Themes[x].isDirectory() ) {
					themeList.add(Themes[x].getName());
					ortus.api.DebugLog(LogLevel.Trace, "ListTheme: " + Themes[x].getName() );
				}
			}
		} catch(Exception ioe) {
			ortus.api.DebugLog(LogLevel.Error, "ListTheme exception: " + ioe );
		}
		
		return themeList.toArray();
		
	}
	
	public String GetThemeRandomBackground() {
		Random rand = new Random();
		try {
			File BackPath = new File(ThemePath + java.io.File.separator + CurrentTheme + java.io.File.separator + ThemeProps.getProperty("ThemeBackgroundPath"));
		
			File[] bi = BackPath.listFiles();
		
			int i = rand.nextInt(bi.length);
	    
			return bi[i].getAbsolutePath();
		} catch ( Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "GetThemeRandomBackground exception: " + e );
			return null;
		}
	}
}
