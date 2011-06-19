/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ortus.daemon.OrtusTask;
import ortus.media.favoriteexcluder.FavoriteExcluder;
import ortus.daemon.taskEngine;
import ortus.daemon.processEngine;
import ortus.daemon.tcpEngine;
import ortus.db.IEngine;
import ortus.db.h2engine;
import ortus.events.LocalEvents;
import ortus.net.downloadEngine;
import ortus.net.httpEngine;
import ortus.property.IProperty;
import ortus.property.OrtusProperty;
import ortus.ui.theme.themeEngine;
import sagex.api.Configuration;
import sagex.api.Global;
import sagex.api.Utility;

/**
 *
 * @author jphipps
 */
public class Ortus extends vars {

    private static Ortus INSTANCE = null;
    private static final Object OrtusLock = new Object();
    // Main Control Processes
    private FavoriteExcluder FavoriteExcluderAPI = null;
    private themeEngine ThemeAPI = null;
    private IEngine DBAPI = null;
    private taskEngine CronServerAPI = null;
    private processEngine ProcessServerAPI = null;
//	private tcpEngine TcpServerAPI = null;
    private downloadEngine DownloadServerAPI = null;
    private httpEngine HTTPAPI = null;
    private LocalEvents me = null;
    // Configuration and client connections
    private String SagePath = System.getProperty("user.dir");
    private String BasePath = System.getProperty("user.dir") + java.io.File.separator + "STVs" + java.io.File.separator + "Ortus";
    private Map<String, OrtusProperty> PropertyMap = Collections.synchronizedMap(new HashMap(5));
    private Map<String, Identity> IdentityMap = Collections.synchronizedMap(new HashMap(5));
    private Map<String, String> MACMap = Collections.synchronizedMap(new HashMap(5));

    public static Ortus getInstance() {
        if (INSTANCE == null) {
            synchronized (Ortus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Ortus();
                }
            }
        }
        return INSTANCE;
    }

    protected Ortus() {

        ortus.api.DebugLog(LogLevel.Info, "Ortus: Plugin Starting");
        ortus.api.DebugLog(LogLevel.Info, "Ortus: Version: " + ortus.api.GetVersionFull());
        ortus.api.DebugLog(LogLevel.Info, "Ortus: Context: " + ((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName()));
        ortus.api.DebugLog(LogLevel.Info, "Ortus: SagePath: " + SagePath);
        ortus.api.DebugLog(LogLevel.Info, "Ortus: BasePath: " + BasePath);
        String fanartfolder = Configuration.GetProperty("ortus/fanart/folder", null);
        if (fanartfolder == null) {
            fanartfolder = BasePath + java.io.File.separator +  "Fanart";
            Configuration.SetProperty("ortus/fanart/folder", fanartfolder);
            File fad = new File(fanartfolder);
            if ( ! fad.exists())
                fad.mkdirs();
        }
        ortus.api.DebugLog(LogLevel.Info, "Ortus: Fanart Folder: " + fanartfolder);
        if (ortus.util.ui.IsClient()) {
            ortus.api.DebugLog(LogLevel.Info, "Ortus: Is a Client");
        }

        if (Global.IsServerUI()) {
            ortus.api.DebugLog(LogLevel.Info, "Ortus: Is a Server");
        }
        
    }

    public void doStartupTasks() {

        ortus.cache.cacheEngine.getInstance();
        DBAPI = new h2engine();
	ProcessServerAPI = new processEngine();
	ProcessServerAPI.start();
        FavoriteExcluderAPI = new FavoriteExcluder();
        ThemeAPI = new themeEngine();
        CronServerAPI = new taskEngine();
//		TcpServerAPI = new tcpEngine();
//		TcpServerAPI.start();
        DownloadServerAPI = new downloadEngine();
        DownloadServerAPI.start();
//                HTTPAPI = new httpEngine();

        me = new LocalEvents();

        CronServerAPI.Schedule("favexcluder", new OrtusTask("ortus.api.RunExcluder"), 1800);

        if (!ortus.util.ui.IsClient()) {
            CronServerAPI.LoadTasks();
        }
        
        if (DBAPI.GetCleanDB()) {
            ortus.mq.api.fireAsyncMessage("QuickScan", new Object[]{});
        } else {
            ortus.mq.api.fireAsyncMessage("CleanDB", new Object[]{});
            ortus.mq.api.fireAsyncMessage("PreloadCache", new Object[]{});
        }

        ortus.mq.api.fireMessage("AnalyzeDB", new Object[]{});

        CronServerAPI.Schedule("propsaver", new OrtusTask("ortus.api.StoreUserProperty"), 300);
    }

    public void Shutdown() {
        ortus.api.StoreUserProperty();
        ortus.mq.api.fireAsyncMessage("Shutdown", new Object[]{});
    }

    public String getSagePath() {
        return SagePath;
    }

    public String getBasePath() {
        return BasePath;
    }

    public String getConfigPath() {
        return (String) getProperty().GetProperty("ConfigPath", BasePath);
    }

    public FavoriteExcluder getFavoriteExcluder() {
        return FavoriteExcluderAPI;
    }

    public themeEngine getTheme() {
        return ThemeAPI;
    }

    public IEngine getDB() {
        return DBAPI;
    }

    public taskEngine getCronServer() {
        return CronServerAPI;
    }

    public processEngine getProcessServer() {
        return ProcessServerAPI;
    }

//	public tcpEngine getTcpServer() {
//		return TcpServerAPI;
//	}
    public downloadEngine getDownloadServer() {
        return DownloadServerAPI;
    }

    public IProperty getProperty() {
        String x = (Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName();
        return getProperty((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName());
    }

    public IProperty getProperty(String UICname) {
        if (UICname == null) {
            UICname = "background";
        }

        if (PropertyMap.get(UICname) == null) {
            loadConnection(UICname);
        }
        return PropertyMap.get(UICname);
    }

    public Identity getIdentity() {
        return getIdentity((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName());
    }

    public Identity getIdentity(String UICname) {
        if (UICname == null) {
            UICname = "background";
        }

        if (IdentityMap.get(UICname) == null) {
            loadConnection(UICname);
        }

        return IdentityMap.get(UICname);
    }

    public Object[] getAllContext() {
        return IdentityMap.keySet().toArray();
    }

    public String getMACAddress() {
        if (MACMap.get((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName()) == null) {
            loadConnection();
        }

        return MACMap.get((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName());
    }

    private void loadConnection() {
        loadConnection((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName());

    }

    private void loadConnection(String UICname) {
        ortus.api.DebugLog(LogLevel.Info, "configurationManager: Loading a new connection for context: " + UICname);
        String MACaddr = null;

        ortus.api.DebugLogTrace("IsSererUI(): " + Global.IsServerUI());
        ortus.api.DebugLogTrace("IsClient(): " + Global.IsClient());
        ortus.api.DebugLogTrace("IsDesktopUI(): " + Global.IsDesktopUI());
        ortus.api.DebugLogTrace("IsRemoteUI() : " + Global.IsRemoteUI());
        ortus.api.DebugLogTrace("Context : " + Global.GetUIContextName());

        if (ortus.util.ui.IsClient() || Global.IsServerUI() || UICname.equalsIgnoreCase("background")) {
            if (ortus.util.ui.IsClient()) {
                ortus.api.DebugLog(LogLevel.Info, "Ortus: Is a Client");
            }
            if (Global.IsServerUI()) {
                ortus.api.DebugLog(LogLevel.Info, "Ortus: Is a Server");
            }
            if (UICname.equalsIgnoreCase("background")) {
                ortus.api.DebugLog(LogLevel.Info, "Ortus: Is Background Service");
            }
            try {
//                                String localip = Utility.GetLocalIPAddress();
                InetAddress address = null;
                NetworkInterface iface = null;
                for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                    iface = (NetworkInterface) ifaces.nextElement();
                    if (iface.getDisplayName().equals("lo")) {
                        continue;
                    }
                    ortus.api.DebugLog(LogLevel.Trace, "Found Interface:" + iface.getDisplayName());
                    InetAddress ia = null;
                    List<InterfaceAddress> ias = iface.getInterfaceAddresses();

                    if (ias.size() > 1) {
                        ia = (InetAddress) ias.get(1).getAddress();
                        if (ia.getHostAddress().equals("127.0.0.1")) {
                            continue;
                        }
                        address = ia;
                        ortus.api.DebugLog(LogLevel.Trace, "Ortus: Found Interface: " + iface.getDisplayName() + " : " + ia.getCanonicalHostName() + " " + ia.getHostAddress());
                        break;

                    }
                }
//				InetAddress address = InetAddress.getLocalHost();
//				ortus.api.DebugLog(LogLevel.Info, "Ortus: net got address: " + address);
                if (address != null) {
                    NetworkInterface ni = NetworkInterface.getByInetAddress(address);
                    byte[] mac = ni.getHardwareAddress();
                    for (int i = 0; i < mac.length; i++) {
                        if (i == 0) {
                            MACaddr = (String.format("%02X", mac[i]));
                        } else {
                            MACaddr = MACaddr + String.format("%02X", mac[i]);
                        }
                    }
                    MACaddr = MACaddr.toLowerCase();
                } else {
                    MACaddr = "Unknown";
                }
            } catch (Exception e) {
                MACaddr = "Unknown";
                ortus.api.DebugLog(LogLevel.Warning, "Ortus: Exception: ", e);
                e.printStackTrace();
            }
        } else {
            ortus.api.DebugLog(LogLevel.Info, "Ortus: Is an Extender/Placeshifter");
            MACaddr = UICname;
        }

        ortus.api.DebugLog(LogLevel.Info, "Ortus: MAC address: " + MACaddr);
        String ConfigPath;
        if (!UICname.equalsIgnoreCase("background")) {
            ConfigPath = BasePath + java.io.File.separator + "Configuration" + java.io.File.separator + MACaddr;
        } else {
            ConfigPath = BasePath + java.io.File.separator + "Configuration";
        }

        ortus.api.DebugLog(LogLevel.Info, "Ortus: ConfigPath: " + ConfigPath);

        ortus.util.file.CreateDirectory(ConfigPath);
        ortus.util.file.CreateDirectory(ConfigPath + java.io.File.separator + "metaprop");

        PropertyMap.put(UICname, new OrtusProperty(ConfigPath));
        PropertyMap.get(UICname).SetProperty("ortus/mac", MACaddr);
        PropertyMap.get(UICname).SetProperty("ortus/sagepath", SagePath);
        PropertyMap.get(UICname).SetProperty("ortus/basepath", BasePath);
        PropertyMap.get(UICname).SetProperty("ortus/configpath", ConfigPath);
        if (PropertyMap.get(UICname).GetProperty("remotehost", null) == null) {
            if (ortus.util.ui.IsClient() || Global.IsServerUI()) {
                PropertyMap.get(UICname).SetProperty("remotehost", Global.GetServerAddress());
            } else {
                PropertyMap.get(UICname).SetProperty("remotehost", "None");
            }
        }

        //	if (getProperty().GetProperty("h2protocol", null) == null) {
//			if (Global.IsClient()) {
       

        MACMap.put(UICname, MACaddr);

        HashMap clnt = new HashMap();
        clnt.put("MACaddr", MACaddr);

        clnt.put("ip", Utility.GetLocalIPAddress());

        if (!UICname.equalsIgnoreCase("background")) {
            IdentityMap.put(UICname, new Identity(MACaddr));
            clnt.put("name", IdentityMap.get(UICname).GetClientName());
            IdentityMap.get(UICname).GetUserProperty().Load();
        } else {
            clnt.put("name", "background");
        }

        ortus.mq.api.fireMQMessage(ortus.mq.vars.EvenType.Server, "ClientLogin", new Object[]{clnt});


        ortus.api.DebugLog(LogLevel.Info, "configurationManager: new connection load completed");
    }

    public void unloadconnection(String UI) {
        ortus.api.StoreUserProperty();
        PropertyMap.remove(UI);
        MACMap.remove(UI);
        IdentityMap.remove(UI);
    }

    public List<String> LoadJarFile(String jarfile) {
        ortus.api.DebugLog(LogLevel.Trace, "LoadJarFile: Loading: " + jarfile);
        List<String> Entries = new ArrayList<String>();
        try {
            InputStream is = getClass().getResourceAsStream(jarfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                Entries.add(strLine);
            }
            br.close();
            is.close();
            return Entries;

        } catch (Exception e) {
            ortus.api.DebugLog(LogLevel.Error, "loadJarFile: " + jarfile + " Exception: ", e);
            return Entries;
        }
    }

    public InputStream getJarStream(String jarfile) {
        ortus.api.DebugLog(LogLevel.Trace, "getJarStream: Loading: " + jarfile);
        try {
            InputStream is = getClass().getResourceAsStream(jarfile);
            return is;

        } catch (Exception e) {
            ortus.api.DebugLog(LogLevel.Error, "getJarStream: " + jarfile + " Exception: ", e);
            return null;
        }
    }
}
