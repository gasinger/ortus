/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus;

import java.io.BufferedReader;
import java.io.DataInputStream;
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
import ortus.vars.LogLevel;
import ortus.property.IProperty;
import ortus.property.OrtusProperty;
import sagex.api.Configuration;
import sagex.api.Global;
import sagex.api.Utility;

/**
 *
 * @author jphipps
 */
public class configurationEngine {
	static configurationEngine INSTANCE = null;
	private String SagePath = System.getProperty("user.dir");
	private String BasePath = System.getProperty("user.dir") + java.io.File.separator + "STVs" + java.io.File.separator + "Ortus";
	private Map<String, OrtusProperty> PropertyMap = Collections.synchronizedMap(new HashMap(5));
	private Map<String, Identity> IdentityMap = Collections.synchronizedMap(new HashMap(5));
	private Map<String, String> MACMap = Collections.synchronizedMap(new HashMap(5));

	public static configurationEngine getInstance() {
		if ( INSTANCE == null ) {
		    synchronized(configurationEngine.class) {
			if ( INSTANCE == null )
			   INSTANCE = new configurationEngine();
		    }
		}
		return INSTANCE;
	}

	public configurationEngine() {
		ortus.api.DebugLog(LogLevel.Info, "Ortus: configurationManager: Starting");		
		ortus.api.DebugLog(LogLevel.Info, "Ortus: Version: " + ortus.api.GetVersionFull());
		ortus.api.DebugLog(LogLevel.Info, "Ortus: Context: " + ((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName()));
		ortus.api.DebugLog(LogLevel.Info, "Ortus: SagePath: " + SagePath);
		ortus.api.DebugLog(LogLevel.Info, "Ortus: BasePath: " + BasePath);
                String fanartfolder = Configuration.GetProperty("ortus/fanart/folder", null);
                if ( fanartfolder == null)
                    fanartfolder = BasePath + java.io.File.separator + "fanart";
		ortus.api.DebugLog(LogLevel.Info, "Ortus: Fanart Folder: " + fanartfolder);
		if (Global.IsClient())
			ortus.api.DebugLog(LogLevel.Info,"Ortus: Is a Client");

		if (Global.IsServerUI()) 
			ortus.api.DebugLog(LogLevel.Info, "Ortus: Is a Server");

//		PropertyMap.put("background", new OrtusProperty(BasePath + java.io.File.separator + "Configuration"));
//		PropertyMap.get("background").SetProperty("ortus/sagepath", SagePath);
//		PropertyMap.get("background").SetProperty("ortus/basepath", BasePath);

//		if ( Global.GetUIContextName() == null) {
//			if (getProperty().GetProperty("remotehost", null) == null) {
//				if (Global.IsClient()) {
//					getProperty().SetProperty("remotehost", Global.GetServerAddress());
//				} else {
//					getProperty().SetProperty("remotehost", "None");
//				}
//			}

//			if (getProperty().GetProperty("h2protocol", null) == null) {
//				if (Global.IsClient()) {
//					getProperty().SetProperty("h2protocol", "jdbc:h2:tcp://" + Global.GetServerAddress() + "/Ortus/db/ortusDB");
//				} else {
//					getProperty().SetProperty("h2protocol", "jdbc:h2:Ortus/db/ortusDB;CACHE_SIZE=128000;MULTI_THREADED=1");
//				}
//			}
//		}
		ortus.api.DebugLog(LogLevel.Info, "Ortus: configurationManager: Completed");
	}

	public String getSagePath() {
		return SagePath;
	}
	
	public String getBasePath() {
		return BasePath;
	}

	public String getConfigPath() {
		return getProperty().GetProperty("ConfigPath", BasePath);
	}


	public IProperty getProperty() {
		return getProperty((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName());
	}
	public IProperty getProperty(String UICname) {
		if ( UICname == null)
			UICname = "background";

		if (PropertyMap.get(UICname) == null) {
			loadConnection(UICname);
		}
		return PropertyMap.get(UICname);
	}

	public Identity getIdentity() {
		return getIdentity((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName());
	}

	public Identity getIdentity(String UICname) {
		if ( UICname == null)
			UICname = "background";

		if (IdentityMap.get(UICname) == null) {
			loadConnection(UICname);
		}

		return IdentityMap.get(UICname);
	}

	public Object[] getAllContext() {
		return IdentityMap.keySet().toArray();
	}

	public String getMACAddress() {
		if ( MACMap.get((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName()) == null)
			loadConnection();

		return MACMap.get((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName());
	}

	private void loadConnection() {
		loadConnection((Global.GetUIContextName() == null) ? "background" : Global.GetUIContextName());

	}
	private void loadConnection(String UICname) {
		ortus.api.DebugLog(LogLevel.Info, "configurationManager: Loading a new connection for context: " + UICname);
		String MACaddr = null;
              
		if (Global.IsClient() || Global.IsServerUI() || UICname.equalsIgnoreCase("background")) {
			if (Global.IsClient()) {
				ortus.api.DebugLog(LogLevel.Info, "Ortus: Is a Client");
			}
			if (Global.IsServerUI()) {
				ortus.api.DebugLog(LogLevel.Info, "Ortus: Is a Server");
			}
			if ( UICname.equalsIgnoreCase("background")) {
				ortus.api.DebugLog(LogLevel.Info, "Ortus: Is Background Service");
			}
			try {
//                                String localip = Utility.GetLocalIPAddress();
                                InetAddress address = null;
                                NetworkInterface iface = null;
                                for(Enumeration ifaces =      NetworkInterface.getNetworkInterfaces();ifaces.hasMoreElements();){
                                   iface = (NetworkInterface)ifaces.nextElement();
                                   if ( iface.getDisplayName().equals("lo"))
                                       continue;
//                                   ortus.api.DebugLog(LogLevel.Trace, "Found Interface:"+ iface.getDisplayName());
                                   InetAddress ia = null;
                                   List<InterfaceAddress> ias = iface.getInterfaceAddresses();

                                   if ( ias.size() > 1) {
                                        ia = (InetAddress)ias.get(1).getAddress();
                                        if ( ia.getHostAddress().equals("127.0.0.1"))
                                            continue;
                                        address = ia;
                                        ortus.api.DebugLog(LogLevel.Trace,"Ortus: Found Interface: " + iface.getDisplayName() + " : " + ia.getCanonicalHostName()+" "+    ia.getHostAddress());
                                        break;
                                        
                                   }
                                }
//				InetAddress address = InetAddress.getLocalHost();
//				ortus.api.DebugLog(LogLevel.Info, "Ortus: net got address: " + address);
				NetworkInterface ni = NetworkInterface.getByInetAddress(address);
				byte[] mac = ni.getHardwareAddress();
				for (int i = 0; i < mac.length; i++) {
					if (i == 0) {
						MACaddr = (String.format("%02X", mac[i]));
					} else {
						MACaddr = MACaddr + String.format("%02X", mac[i]);
					}
				}
                                MACaddr=MACaddr.toLowerCase();
			} catch (Exception e) {
				MACaddr = "Unknown";
				ortus.api.DebugLog(LogLevel.Warning, "Ortus: Exception: " , e);
                                e.printStackTrace();
			}
		} else {
			ortus.api.DebugLog(LogLevel.Info, "Ortus: Is an Extender/Placeshifter");
			MACaddr = UICname;
		}

		ortus.api.DebugLog(LogLevel.Info, "Ortus: MAC address: " + MACaddr);
		String ConfigPath;
		if ( ! UICname.equalsIgnoreCase("background"))
			ConfigPath = BasePath + java.io.File.separator + "Configuration" + java.io.File.separator + MACaddr;
		else
			ConfigPath = BasePath + java.io.File.separator + "Configuration";

		ortus.api.DebugLog(LogLevel.Info, "Ortus: ConfigPath: " + ConfigPath);

		ortus.util.file.CreateDirectory(ConfigPath);
		ortus.util.file.CreateDirectory(ConfigPath + java.io.File.separator + "metaprop");

		PropertyMap.put(UICname, new OrtusProperty(ConfigPath));
		PropertyMap.get(UICname).SetProperty("ortus/mac", MACaddr);
		PropertyMap.get(UICname).SetProperty("ortus/sagepath", SagePath);
		PropertyMap.get(UICname).SetProperty("ortus/basepath", BasePath);
		PropertyMap.get(UICname).SetProperty("ortus/configpath", ConfigPath);

		MACMap.put(UICname, MACaddr);
		
		IdentityMap.put(UICname, new Identity(MACaddr));

		if (getProperty().GetProperty("remotehost", null) == null) {
			if (Global.IsClient() || Global.IsServerUI()) {
				getProperty().SetProperty("remotehost", Global.GetServerAddress());
			} else {
				getProperty().SetProperty("remotehost", "None");
			}
		}

	//	if (getProperty().GetProperty("h2protocol", null) == null) {
//			if (Global.IsClient()) {
                if ( Configuration.GetServerProperty("ortus/h2server", "false").equalsIgnoreCase("true") && ortus.util.ui.IsClient() ) {
                        String tcpport = Configuration.GetServerProperty("h2/tcp_port", "9093");
			getProperty().SetProperty("h2protocol", "jdbc:h2:tcp://" + Global.GetServerAddress() + ":" + tcpport + "/STVs/Ortus/db/ortusDB");
                } else {
			getProperty().SetProperty("h2protocol", "jdbc:h2:STVs/Ortus/db/ortusDB;CACHE_SIZE=128000");
		}
		ortus.api.DebugLog(LogLevel.Info, "configurationManager: new connection load completed");
	}

        public void unloadconnection(String UI) {
            PropertyMap.remove(UI);
            MACMap.remove(UI);
            IdentityMap.remove(UI);
        }
        
	public List<String> LoadJarFile(String jarfile)
	{
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
			ortus.api.DebugLog(LogLevel.Error, "loadJarFile: " + jarfile + " Exception: " , e);
			return Entries;
		}
	}

	public InputStream getJarStream(String jarfile)
	{
		ortus.api.DebugLog(LogLevel.Trace, "getJarStream: Loading: " + jarfile);
		try {
			InputStream is = getClass().getResourceAsStream(jarfile);
			return is;

		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "getJarStream: " + jarfile + " Exception: " , e);
			return null;
		}
	}

}
