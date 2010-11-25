/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.plugins;

import ortus.vars.LogLevel;
import sage.SageTVPlugin;
import sage.SageTVPluginRegistry;
import sagex.api.Configuration;
import sagex.plugin.AbstractPlugin;
import sagex.plugin.ButtonClickHandler;
import sagex.plugin.ConfigValueChangeHandler;
import sagex.plugin.IPropertyPersistence;

public class ortusPlugin extends AbstractPlugin {

	public static ortusPlugin pluginInstance = null;

	private IPropertyPersistence ssp = new SageServerPropertyPersistence();
	private IPropertyPersistence bp = new ButtonPersistence();

	public final String PROP_FANART_FOLDER = "ortus/fanart/folder";
	public final String PROP_FANART_DOWNLOAD_LIMIT = "ortus/fanart/download_limit";
	public final String PROP_AUTO_METADATA = "ortus/metadata/autometadata";
        public final String PROP_WIZ_UPDATE_METADATA = "ortus/metadata/wizupdate";
        public final String PROP_WIZ_EPISODE = "ortus/metadata/wizepisode";
        public final String PROP_USE_PROPERTY = "ortus/metadata/useproperty";
        public final String PROP_WRITE_PROPERTY = "ortus/metadata/writeproperty";
	public final String PROP_LOG_LEVEL = "ortus/log/level";
	public final String PROP_H2_PROTOCOL = "ortus/h2/protocol";
	public final String PROP_EVENT_TEST = "ortus/event/test";
	public final String PROP_METADATA_SCAN_TARGET = "ortus/metadata/target";
        public final String PROP_METADATA_SCAN_TYPE = "ortus/metadata/scantype";
	public final String PROP_FULL_METADATA_SCAN = "fullscan";

	public static ortusPlugin getPluginInstance() {
		return pluginInstance;
	}

	public ortusPlugin(SageTVPluginRegistry registry) {
		super(registry);

		addProperty(SageTVPlugin.CONFIG_CHOICE, PROP_LOG_LEVEL, "Trace", "Logging Level", "Specify the level of logging to the ortus.log", new String[] { "Off", "Fatal", "Error","Warning","Info","Debug","Trace" }).setPersistence(ssp);
		addProperty(SageTVPlugin.CONFIG_DIRECTORY, PROP_FANART_FOLDER, "Ortus" + java.io.File.separator + "Fanart", "Fanart Folder", "Specify the fanart folder to use for images");
		addProperty(SageTVPlugin.CONFIG_INTEGER, PROP_FANART_DOWNLOAD_LIMIT, "4", "Fanart Download Limit", "Specify the limit for fanart downloads");
		addProperty(SageTVPlugin.CONFIG_BOOL, PROP_AUTO_METADATA, "false", "Enable Automatic Metadata", "Enable automatic metadata population for new media files").setPersistence(ssp);
                addProperty(SageTVPlugin.CONFIG_BOOL, PROP_WIZ_UPDATE_METADATA, "false", "Enable Automatic Sage Update", "Enable automatic metadata population for new media files").setPersistence(ssp);
                addProperty(SageTVPlugin.CONFIG_BOOL, PROP_WIZ_EPISODE, "false", "Import Episodes as TV", "Set imported media identified to be a series as a TV show").setPersistence(ssp);
                addProperty(SageTVPlugin.CONFIG_BOOL, PROP_USE_PROPERTY, "true", "Use Property Files for import", "Use property files for metadata");
                addProperty(SageTVPlugin.CONFIG_BOOL, PROP_WRITE_PROPERTY, "false", "Write Property Files for metadata", "Create property files for each media object");
		addProperty(SageTVPlugin.CONFIG_CHOICE, PROP_METADATA_SCAN_TARGET, "Server", "Metadata Scan Target", "Where to run a metadata scan Server/Client", new String[] { "Server", "Client" });
                addProperty(SageTVPlugin.CONFIG_CHOICE, PROP_METADATA_SCAN_TYPE, "Full", "Metadata Scan Type", "Scan Type: Full, Missing Fanart", new String[] { "Full", "Fanart" });
		addProperty(SageTVPlugin.CONFIG_BUTTON, PROP_FULL_METADATA_SCAN, "Run Scan", "Full Metadata Scan", "Perform full metadata scan, to cancel switch setting to false, or it will switch to false when it is completed").setPersistence(bp);
	}

	public void start() {
		super.start();

                if ( ortus.util.ui.IsServer())
                    Configuration.SetServerProperty("ortus/h2server", "true");

		ortus.Ortus.getInstance().doStartupTasks();
	}

	public void stop() {
		super.stop();

		ortus.Ortus.getInstance().Shutdown();

                if ( ortus.util.ui.IsServer())
                    Configuration.SetServerProperty("ortus/h2server", "false");
                
	}

	@ConfigValueChangeHandler(PROP_AUTO_METADATA)
	public void onPROP_AUTO_METADATAChanged(String setting) {
		log.info("AUTO_METADATA Changed Flag Changed: " + getConfigValue(setting));
	}

	@ButtonClickHandler(PROP_FULL_METADATA_SCAN)
	public void onPROP_FULL_METADATA_SCAN_click(String setting, String value) {
		ortus.api.DebugLog(LogLevel.Trace, "onPROP_FULL_METADATA_SCAN_click: " + setting);
		if ( ortus.onlinescrapper.api.IsindexMediaRunning()) {
			ortus.api.CancelIndexMedia();
		} else {
                        String scantype = "FullScan";
                        if (Configuration.GetProperty("ortus/metadata/scantype","Full").equalsIgnoreCase("Fanart"))
                            scantype = "FanartScan";
			if( Configuration.GetProperty("ortus/metadata/target","Server").equalsIgnoreCase("server"))
				ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Server, scantype, new Object[] { 1, 1 } );
			else
				ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Local, scantype, new Object[] { 1, 1 } );
		}

	}
}
