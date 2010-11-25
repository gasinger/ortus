/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sagex.h2;

import org.h2.tools.Server;
import sage.SageTVPlugin;
import sage.SageTVPluginRegistry;
import sagex.api.Global;
import sagex.plugin.AbstractPlugin;
import sagex.plugin.ConfigValueChangeHandler;
import sagex.plugin.IPropertyPersistence;

public class SagexH2Plugin extends AbstractPlugin {

	public static SagexH2Plugin pluginInstance = null;

	private String H2PROTOCOL = "org.h2.Driver";
	private final String PROP_TCP_ENABLE = "h2/tcp_enable";
	private final String PROP_TCP_PORT = "h2/tcp_port";
	private final String PROP_WEB_ENABLE = "h2/web_enable";
	private final String PROP_WEB_PORT = "h2/web_port";
	private final String PROP_PG_ENABLE = "h2/pg_enable";
	private final String PROP_PG_PORT = "h2/pg_port";

	private Server tcpserver = null;
	private Server webserver = null;
	private Server pgserver = null;

       	private IPropertyPersistence ssp = new SageServerPropertyPersistence();

	public static SagexH2Plugin getPluginInstance() {
		return pluginInstance;
	}

	public SagexH2Plugin(SageTVPluginRegistry registry) {
		super(registry);

		addProperty(SageTVPlugin.CONFIG_BOOL, PROP_TCP_ENABLE, "true", "Enable TCP Connections", "Enable TCP access to the H2 Database Instance").setPersistence(ssp);
		addProperty(SageTVPlugin.CONFIG_INTEGER, PROP_TCP_PORT, "9092", "TCP Port", "Port to allow TCP connections to the H2 Database Instance - Port will conflict with Squeezebox, so you need to chnage the port").setPersistence(ssp);
		addProperty(SageTVPlugin.CONFIG_BOOL, PROP_WEB_ENABLE, "true", "Enable Web Connections", "Enable Web access to the H2 Database Instance").setPersistence(ssp);
		addProperty(SageTVPlugin.CONFIG_INTEGER, PROP_WEB_PORT, "8082", "Web Port", "Port to allow Web connections to the H2 Database Instance").setPersistence(ssp);
		addProperty(SageTVPlugin.CONFIG_BOOL, PROP_PG_ENABLE, "false", "Enable Postgres Connections", "Enable Postgres access to the H2 Database Instance").setPersistence(ssp);
		addProperty(SageTVPlugin.CONFIG_INTEGER, PROP_PG_PORT, "5345", "PG Port", "Port to allow Postgres connections to the H2 Database Instance").setPersistence(ssp);
	}

	public void start() {
		super.start();
		
		loadDriver();

		if (getConfigBoolValue(PROP_TCP_ENABLE)) {
			StartTcpServer();
		}

		if (getConfigBoolValue(PROP_WEB_ENABLE)) {
			StartWebServer();
		}

		if (getConfigBoolValue(PROP_PG_ENABLE)) {
			StartPgServer();
		}
	}

	public void stop() {
		super.stop();
		if (tcpserver != null) {
			StopTcpServer();
		}

		if (webserver != null) {
			StopWebServer();
		}

		if (pgserver != null) {
			StopPgServer();
		}
	}

	@ConfigValueChangeHandler(PROP_TCP_ENABLE)
	public void onPROP_TCP_ENABLEdChanged(String setting) {
		log.info("TCP Enabled Flag Changed: " + getConfigBoolValue(setting));
		if (getConfigBoolValue(setting)) {
			StartTcpServer();
		} else {
			StopTcpServer();
		}
	}

	@ConfigValueChangeHandler(PROP_TCP_PORT)
	public void onPROP_TCP_PORTChanged(String setting) {
		log.info("TCP Port Changed: " + setting);
		if (IsTcpServerRunning()) {
			StopTcpServer();
			StartTcpServer();
		}
	}

	@ConfigValueChangeHandler(PROP_WEB_ENABLE)
	public void onPROP_WEB_ENABLEdChanged(String setting) {
		log.info("WEB Enabled Flag Changed: " + getConfigBoolValue(setting));
		if (getConfigBoolValue(setting)) {
			StartWebServer();
		} else {
			StopWebServer();
		}
	}

	@ConfigValueChangeHandler(PROP_WEB_PORT)
	public void onPROP_WEB_PORTChanged(String setting) {
		log.info("WEB Port Changed: " + setting);
		if (IsWebServerRunning()) {
			StopWebServer();
			StartWebServer();
		}
	}

	@ConfigValueChangeHandler(PROP_PG_ENABLE)
	public void onPROP_PG_ENABLEdChanged(String setting) {
		log.info("PG Enabled Flag Changed: " + getConfigBoolValue(setting));
		if (getConfigBoolValue(setting)) {
			StartPgServer();
		} else {
			StopPgServer();
		}
	}

	@ConfigValueChangeHandler(PROP_PG_PORT)
	public void onPROP_PG_PORTChanged(String setting) {
		log.info("PG Port Changed: " + setting);
		if (IsPgServerRunning()) {
			StopPgServer();
			StartPgServer();
		}
	}

	private boolean loadDriver() {
		try {
			Class.forName(H2PROTOCOL);
			Global.DebugLog("H2: Successfully Loaded Embedded Driver");
		} catch (ClassNotFoundException cnfe) {
			Global.DebugLog("H2: Exception: Unable to load the JDBC driver " + H2PROTOCOL);
			Global.DebugLog("H2: Exception: Please check your CLASSPATH.");
			return false;
		}
		return true;
	}

	public void StartTcpServer() {
		if (tcpserver != null) {
			return;
		}

		try {
			Global.DebugLog("H2: Starting Tcp Server on port: " + getConfigValue(PROP_TCP_PORT));
			tcpserver = Server.createTcpServer(new String[]{"-tcpAllowOthers", "-tcpPort", getConfigValue(PROP_TCP_PORT)}).start();
		} catch (Exception e) {
			Global.DebugLog("H2: Tcp server failed to start");
		}
	}

	public void StopTcpServer() {
		if (tcpserver != null) {
			tcpserver.stop();
			tcpserver = null;
		}
	}

	public boolean IsTcpServerRunning() {
		if (tcpserver == null) {
			return false;
		} else {
			return true;
		}
	}

	public void StartWebServer() {
		if (webserver != null) {
			return;
		}

		try {
			Global.DebugLog("H2: Starting Web Server on port: " + getConfigValue(PROP_WEB_PORT));
			webserver = Server.createWebServer(new String[]{"-webAllowOthers", "-webPort", getConfigValue(PROP_WEB_PORT)}).start();
		} catch (Exception e) {
			Global.DebugLog("H2: Web server failed to start");
		}
	}

	public void StopWebServer() {
		if (webserver != null) {
			webserver.stop();
			webserver = null;
		}
	}

	public boolean IsWebServerRunning() {
		if (webserver == null) {
			return false;
		} else {
			return true;
		}
	}

	public void StartPgServer() {
		if (pgserver != null) {
			return;
		}

		try {
			Global.DebugLog("H2: Starting Web Server on port: " + getConfigValue(PROP_PG_PORT));
			pgserver = Server.createPgServer(new String[]{"-pgPort", getConfigValue(PROP_PG_PORT)}).start();
		} catch (Exception e) {
			Global.DebugLog("H2: Pg server failed to start");
		}
	}

	public void StopPgServer() {
		if (pgserver != null) {
			pgserver.stop();
			pgserver = null;
		}
	}

	public boolean IsPgServerRunning() {
		if (pgserver == null) {
			return false;
		} else {
			return true;
		}
	}
}

