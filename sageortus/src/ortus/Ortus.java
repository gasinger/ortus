/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus;

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
import ortus.ui.theme.themeEngine;

/**
 *
 * @author jphipps
 */
public class Ortus extends vars {

	private static Ortus INSTANCE = null;
	private static final Object OrtusLock = new Object();
	private boolean ortus_setup_complete = false;
	private FavoriteExcluder FavoriteExcluderAPI = null;
	private themeEngine ThemeAPI = null;
	private IEngine DBAPI = null;
	private taskEngine CronServerAPI = null;
	private processEngine ProcessServerAPI = null;
//	private tcpEngine TcpServerAPI = null;
	private downloadEngine DownloadServerAPI = null;
        private httpEngine HTTPAPI = null;
	private LocalEvents me = null;

	protected  Ortus() {
		if (ortus_setup_complete) {
			System.out.println("WARNING: Ortus Singlton Intruder");
			return;
		}

		configurationEngine.getInstance();
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
                HTTPAPI = new httpEngine();
                
		me = new LocalEvents();

		ortus_setup_complete = true;
	}

	public static Ortus getInstance() {
		if ( INSTANCE == null) {
			synchronized (Ortus.class) {
				if (INSTANCE == null) {
					INSTANCE = new Ortus();
				}
			}
		}
		return INSTANCE;
	}

	public void doStartupTasks() {
                CronServerAPI.Schedule("favexcluder", new OrtusTask("ortus.api.RunExcluder"), 1800);

                if ( ! ortus.util.ui.IsClient()) {
                    CronServerAPI.LoadTasks();
                }
		if ( DBAPI.GetCleanDB())
			ortus.mq.api.fireAsyncMessage("QuickScan", new Object[] {});
		else {
                 	ortus.mq.api.fireAsyncMessage("CleanDB", new Object[] {});
			ortus.mq.api.fireAsyncMessage("PreloadCache", new Object[] {});
                }
	}

	public void Shutdown() {
		ortus.mq.api.fireAsyncMessage("Shutdown", new Object[] {});
		ortus_setup_complete=false;
		INSTANCE=null;
	}

	public boolean IsOrtusInitialized() {
		return ortus_setup_complete;
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

}
