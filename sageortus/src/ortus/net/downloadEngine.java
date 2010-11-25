/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.net;

import java.util.HashMap;
import java.util.Map;
import ortus.daemon.Thread_pool;
import ortus.mq.EventListener;
import ortus.mq.OrtusEvent;
import ortus.vars.DownloadStatus;
import ortus.vars.LogLevel;
import ortus.vars.UserAgent;
import sagex.UIContext;
import sagex.api.Global;

/**
 *
 * @author jphipps
 */
public class downloadEngine extends EventListener {
	private HashMap<String,downloadTask> OrtusTasks = new HashMap<String,downloadTask>();
	private boolean running = true;
	private Thread_pool pool = null;
	
	public downloadEngine() {
		super();
		ortus.api.DebugLog(ortus.vars.LogLevel.Debug, "downloadDaemon: Loading");
		pool = new Thread_pool(5,5);
	}

	public void run() {
		ortus.api.DebugLog(ortus.vars.LogLevel.Debug, "downloadEngine: Running");

		while(running) {
			try {
				for ( Object x : OrtusTasks.keySet().toArray()) {
					if ( OrtusTasks.get((String)x).isAutoclean() && OrtusTasks.get((String)x).isCompeleted()) {
						CleanDownload((String)x);
					}					
				}
				Thread.sleep(1000);
			} catch (Exception ex) {
				ortus.api.DebugLog(ortus.vars.LogLevel.Error, "downloadEngine: exception: " + ex);
			}
		}
		ortus.api.DebugLog(ortus.vars.LogLevel.Debug,"downloadEngine: Stopping");
	}

	@OrtusEvent("Shutdown")
	public void Shutdown() {
		running = false;
	}

	public boolean AddDownloadQueue(String options, String downloadKey, String url, String dirname) {
		return this.AddDownloadQueue(options, downloadKey, url, dirname, null);
	}

	public boolean AddDownloadQueue(String options, String downloadKey, String url, String dirname, String filename) {
		if ( OrtusTasks.get(downloadKey) != null) {
			ortus.api.DebugLog(ortus.vars.LogLevel.Trace,"downloadEngine: Duplicate key: " + downloadKey);
			return false;
		}
	    ortus.api.DebugLog(ortus.vars.LogLevel.Trace,"downloadEngine: Queue: added: " + downloadKey);
	    String completepath = null;
	    if ( filename == null) {
		int slashIndex = url.lastIndexOf('/');
		int periodIndex = url.lastIndexOf('.');
		String fileName = url.substring(slashIndex + 1);
		completepath = dirname + java.io.File.separator + fileName;
	    } else {
		    completepath = dirname + java.io.File.separator + filename;
	    }
	    
	    downloadTask dt = new downloadTask(Global.GetUIContextName(), downloadKey, url, completepath);

		String[] p = options.split(";");

		for ( String x : p ) {
		    ortus.api.DebugLog(LogLevel.Trace2, " Processing: " + x);
		    String[] work = x.split(":");
		    if( work[0].trim().equalsIgnoreCase("autoclean")) {
			dt.setAutoclean(true);
		    }
		    if ( work[0].trim().equalsIgnoreCase("sagetrigger")) {
			    dt.setSagetrigger(work[1].trim());
		    }
		    if ( work[0].trim().equalsIgnoreCase("overwrite")) {
			    dt.setOverwrite(true);
		    }
		    if ( work[0].trim().equalsIgnoreCase("schedule")) {
			    dt.setStatus(DownloadStatus.Scheduled);
		    }

                    if ( work[0].trim().equalsIgnoreCase("ua")) {
                            dt.setUa(UserAgent.valueOf(work[1].trim()));
                    }
                }
	    synchronized(this.getClass()) {
		    OrtusTasks.put(downloadKey,dt);
		    if ( dt.isReadyToRun())
			pool.execute(OrtusTasks.get(downloadKey));
	    }

	    return true;
	}

	public int ReleaseScheduled() {
		int numReleased = 0;
		for ( Object x : OrtusTasks.keySet().toArray()) {
			if ( OrtusTasks.get((String)x).getStatus() == DownloadStatus.Scheduled) {
				OrtusTasks.get((String)x).setStatus(DownloadStatus.WaitingToStart);
				pool.execute(OrtusTasks.get((String)x));
				numReleased++;
			}
		}
		return numReleased;
	}
	
	public void CancelDownload(String downloadKey) {
		if ( OrtusTasks.get(downloadKey) != null) {
			OrtusTasks.get(downloadKey).cancelDownload();
                        synchronized(this.getClass()) {
                            OrtusTasks.remove(downloadKey);
                        }
		}
	}

        public void CancelAlldownloads() {
            for ( Object x : GetDownloads()) {
                CancelDownload((String)x);
            }
        }

	public void CleanDownload(String downloadKey) {
		if ( OrtusTasks.get(downloadKey).getSagetrigger() != null ) {
			ortus.api.DebugLog(LogLevel.Trace,"downloadEngine: Sending SageCommand: " + OrtusTasks.get(downloadKey).getSagetrigger() + " to UIC: " + OrtusTasks.get(downloadKey).getUicontext() + " Key: " + downloadKey);
			Global.SageCommand(new UIContext((String)OrtusTasks.get(downloadKey).getUicontext()),OrtusTasks.get(downloadKey).getSagetrigger());
		}
		ortus.api.DebugLog(LogLevel.Trace,"downloadEngine: Removing key: " + downloadKey);
		synchronized(this.getClass()) {
			OrtusTasks.remove(downloadKey);
		}
	}

	public Object[] GetDownloads() {
		return OrtusTasks.keySet().toArray();
	}

        public Map GetDownloadDetail(String downloadKey) {
                if ( OrtusTasks.get(downloadKey) == null)
                    return null;

                Map result = new HashMap();
                result.put("status", OrtusTasks.get(downloadKey).getStatus().name());
                result.put("file",OrtusTasks.get(downloadKey).getFilename());
                result.put("url", OrtusTasks.get(downloadKey).getUrl());
                result.put("starttime", OrtusTasks.get(downloadKey).getStarttime());

                return result;
        }
        
	public String GetDownloadPoolStatus() {
		StringBuffer sb = new StringBuffer();
		sb.append("Active Threads: " + pool.activeCount());
		sb.append("\n" + pool.toString());
		return sb.toString();
	}
	
	public int getPctComplete(String downloadKey) {
		if ( OrtusTasks.get(downloadKey) == null)
			return -1;
		else {
			DownloadStatus ts = OrtusTasks.get(downloadKey).getStatus();
			switch(ts) {
				case WaitingToStart: return 0;
				case Completed:
				case Running: return OrtusTasks.get(downloadKey).getPctComplete(); 
				case UrlNotFound:
				case Failed: return -1;
				default: return 0;
			}
		}
	}

	public String getStatus(String downloadKey) {
		if ( OrtusTasks.get(downloadKey) == null) {
			return "KeyNotFound";
		} else {
			String retstatus = OrtusTasks.get(downloadKey).getStatus().name();
			if ( OrtusTasks.get(downloadKey).isCompeleted())
				CleanDownload(downloadKey);
			return retstatus;
		}
	}

	public long getTime(String downloadKey) {
		if ( OrtusTasks.get(downloadKey) == null) {
			return -1;
		} else {
			return OrtusTasks.get(downloadKey).getTime();
		}
	}
	public long getEstimatedTotalTime(String downloadKey) {
		if ( OrtusTasks.get(downloadKey) == null) {
			return -1;
		} else {
			return OrtusTasks.get(downloadKey).getEstimatedTotal();
		}
	}
	public long getEstimatedTimeLeft(String downloadKey) {
		if ( OrtusTasks.get(downloadKey) == null) {
			return -1;
		} else {
			return OrtusTasks.get(downloadKey).getEstimatedTimeLeft();
		}
	}

}
