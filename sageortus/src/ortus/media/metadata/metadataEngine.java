/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.metadata;

import java.util.HashMap;
import ortus.Ortus;
import ortus.vars.LogLevel;

/**
 *
 * @author jphipps
 */
public class metadataEngine {

    private static metadataEngine INSTANCE;
    public IMetadataProvider MDProvider;
    private HashMap<String,IMetadataProvider> metadata = new HashMap<String,IMetadataProvider>();
    private String currentprovider = "db";

    public static metadataEngine getInstance() {
        if ( INSTANCE == null) {
                INSTANCE = new metadataEngine();
        }
        return INSTANCE;
    }

    public static metadataEngine reloadInstance() {
	    INSTANCE=null;
	    return getInstance();
    }
    
    private metadataEngine() {
	    RegisterMetadataProvider("db",new ortus.media.metadata.DBMetadata());
    }

    public void RegisterMetadataProvider(String providername, IMetadataProvider mdp) {
	    ortus.api.DebugLog(LogLevel.Info, "metadataEngine: Registering provider: " + providername + " using class: " + mdp.getClass().getName());
	    metadata.put(providername, mdp);
    }

    public void UnRegisterMetadataProvider(String providername) {
	    metadata.remove(providername);
    }
    
    public void SetMetadataProvider(String providername) {
	    currentprovider = providername.toLowerCase();
    }

    public String GetMetadataProvider() {
	    return currentprovider;
    }

    public Object[] GetMetadataProviders() {
	    return metadata.keySet().toArray();
    }

    public IMetadataProvider getProvider() {
        return metadata.get(currentprovider);
    }

    public IMetadataProvider getProvider(String providername) {
	    return metadata.get(providername.toLowerCase());
    }

}
