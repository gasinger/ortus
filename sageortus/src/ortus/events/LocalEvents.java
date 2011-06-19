/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ortus.events;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ortus.Ortus;
import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;
import ortus.vars.LogLevel;
import sagex.UIContext;
import sagex.api.Configuration;
import sagex.api.Global;
import sagex.api.MediaFileAPI;

/**
 *
 * @author jphipps
 */
public class LocalEvents extends EventListener {

	public LocalEvents() {
		super();
	}

        @OrtusEvent("ChangeLogLevel")
        public void doChangeLogging(int loglevel) {
            ortus.logger.getInstance().SetDebugLevel(loglevel);
        }

        @OrtusEvent("CleanDB")
        public void doCleanDB() {
            ortus.onlinescrapper.api.cleanMedia();
        }
        

	@OrtusEvent("QuickScan")
	public void doQuickScan() {
		Ortus.getInstance().getProcessServer().AddQueue( "ortus.onlinescrapper.api.indexMediaQuick", new Object[] { 1, 1});
	}

	@OrtusEvent("FullScan")
	public void doFullScan(final int metadata, final int fanart) {
		Ortus.getInstance().getProcessServer().AddQueue( "ortus.onlinescrapper.api.indexMediaAll", new Object[] { metadata, fanart});
	}

	@OrtusEvent("FanartScan")
	public void doFanartScan(final int metadata, final int fanart) {
		Ortus.getInstance().getProcessServer().AddQueue( "ortus.onlinescrapper.api.GetMissingFanart", (Object[]) null);
	}

	@OrtusEvent("ScanCountUpdate")
	public void doScanCountUpdate(HashMap<String,Object> result) {
		ortus.onlinescrapper.api.SetScrapperDetail(result);
	}

	@OrtusEvent("STVPanelRefresh")
	public void doSTVPanelRefresh(String panelname) {
		Object[] ctx = Ortus.getInstance().getAllContext();
		for ( Object x : ctx) {
			if (! ((String)x).equalsIgnoreCase("background")) {
				ortus.api.DebugLog(LogLevel.Trace,"doSTVPanelRefresh: Coontext: " + x + " Panel: " + panelname);
				Global.RefreshArea(new UIContext((String)x),panelname);
			}
		}
	}

	@OrtusEvent("CancelScan")
	public void doCancelScan() {
		ortus.onlinescrapper.api.CancelIndexMedia();
	}

	@OrtusEvent("MediaFileImported")
	public void doMediaFileImported(Map eventval) {
                if ( Configuration.GetServerProperty("ortus/metadata/autometadata", "false").equalsIgnoreCase("true"))
                    ortus.onlinescrapper.api.AutoFileMatch(MediaFileAPI.GetMediaFileForID((Integer)eventval.get("MediaFile")));
	}

        @OrtusEvent("RecordingStarted")
	public void doRecordingStarted(Map eventval) {
                if ( Configuration.GetServerProperty("ortus/metadata/autometadata", "false").equalsIgnoreCase("true"))
                    ortus.onlinescrapper.api.AutoFileMatch(MediaFileAPI.GetMediaFileForID((Integer)eventval.get("MediaFile")));
	}
	@OrtusEvent("MediaFileRemoved")
	public void doMediaFileRemoved(Map eventval) {
//                ortus.api.DebugLogTrace("MediaFileRemoved: Recieved by Ortus");
//                for( Object x : eventval.keySet().toArray()) {
//                    ortus.api.DebugLogTrace("Recieved: Key: " + x + " Value: " + eventval.get(x));
//                }
//
//                if( eventval.get("MediaFile") instanceof Integer) {
//                    ortus.api.DebugLogTrace("MediaFile is an int");
//                } else {
//                    ortus.api.DebugLogTrace("MediaFile is a " + eventval.get("MediaFile").getClass().toString());
//                }
		ortus.onlinescrapper.api.cleanMediaObject(eventval.get("MediaFile"));
	}

	@OrtusEvent("PlaybackStopped")
	public void doPlaybackStopped(Map eventval) {
                Pattern pattern = Pattern.compile(".*/.*");
		Matcher matcher = pattern.matcher((String)eventval.get("UIContext"));
		if (matcher.find()) {
                    ortus.api.DebugLogTrace("playback stopped: found server playback event for: " + eventval.get("UIContext") + ", skipping");
                    return;
		}

		ortus.api.DebugLog(LogLevel.Debug, "playback stopped: mediafile : " + eventval.get("MediaFile") + " UI: " + eventval.get("UIContext"));
		ortus.Ortus.getInstance().getIdentity((String)eventval.get("UIContext")).SetUserWatchPosition(eventval);
	}

//        @OrtusEvent("ClientDisconnected")
//	public void doDisconnection(Map eventval) {
//                for ( Object x : eventval.keySet().toArray()) {
//                    ortus.api.DebugLogTrace(" Event: Parm: " + x + " Value: " + eventval.get(x));
//                }
//
//                ortus.api.StoreUserProperty();
//	}

        @OrtusEvent("RemoveClient")
        public void doRemoveClient(String MACaddress) {
                ortus.api.DebugLog(LogLevel.Debug, "ClientDisconnected: " + MACaddress);
                Ortus.getInstance().unloadconnection(MACaddress);
        }

        @OrtusEvent("LoadTasks")
        public void doLoadTasks() {
            ortus.api.DebugLog(LogLevel.Debug, "LoadTasks");
            Ortus.getInstance().getCronServer().LoadTasks();
        }
        @OrtusEvent("CreateTask")
        public void doCreateTask(String taskid, String description, String taskname, String tasktime, long interval, Object[] params) {
            ortus.api.CreateTask(taskid, description, taskname, tasktime, interval, params);
        }

        @OrtusEvent("UserPropertyReload")
        public void doUserPropertyReload(Object userid) {
            ortus.api.ReloadUserProperty(userid);
        }

        @OrtusEvent("UserMenuReload")
        public void doUserMenuReload(Object userid) {
            ortus.api.ReloadUserMenu((Integer)userid);
        }    
}
