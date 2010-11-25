/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.fanart;

import java.util.HashMap;
import ortus.Ortus;
import ortus.vars.LogLevel;

/**
 *
 * @author jphipps
 */
public class fanartEngine {

    private static fanartEngine INSTANCE;
    public IFanartProvider FAProvider;

    private HashMap<String,IFanartProvider> fanart = new HashMap<String, IFanartProvider>();
    private String currentprovider = "db";
    
    public static fanartEngine getInstance() {
        if ( INSTANCE == null) {
                INSTANCE = new fanartEngine();
        }
        return INSTANCE;
    }

    private fanartEngine()  {
		RegisterFanartProvider("db", new ortus.media.fanart.OrtusFanart());
//		RegisterFanartProvider("phoenix", new ortus.media.fanart.PhoenixFanart());
    }

    public void SetFanartProvider(String providername) {
	    currentprovider = providername;
    }

    public String GetFanartProvider() {
	    return currentprovider;
    }

    public Object[] GetFanartProviders() {
	    return fanart.keySet().toArray();
    }
    
    public void RegisterFanartProvider(String providername, IFanartProvider fap) {
	ortus.api.DebugLog(LogLevel.Info, "fanartEngine: Registering provider: " + providername + " using class: " + fap.getClass().getName());
	    fanart.put(providername, fap);
    }

    public void UnRegisterFanartProvider(String providername) {
	    fanart.remove(providername);
    }
    
    public IFanartProvider getProvider() {
        return fanart.get(currentprovider);
    }

    public IFanartProvider getProvider(String providername) {
	    return fanart.get(providername);
    }

}
