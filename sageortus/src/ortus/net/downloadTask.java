/*
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import ortus.vars.DownloadStatus;
import ortus.vars.LogLevel;
import ortus.vars.UserAgent;

/**
 *
 * @author jphipps
 */
public class downloadTask implements Runnable {
	private String downloadKey;
	private String url;
	private String filename;
	private long downloadsize = 0;
	private long filesize = 0;
	private long starttime = 0;
	private long endtime = 0;
	private String uicontext;
	private DownloadStatus status = DownloadStatus.WaitingToStart;
	private boolean autoclean = false;
	private boolean overwrite = false;
	private String sagetrigger = null;
	private boolean dodownload = true;
        private UserAgent ua = UserAgent.Firefox;

	public String getdownloadKey() {
		return downloadKey;
	}
	public DownloadStatus getStatus() {
		return status;
	}

	public void cancelDownload() {
		dodownload = false;
	}

	public boolean isCompeleted() {
		if ( status == DownloadStatus.Completed ||
		     status == DownloadStatus.Failed ||
		     status == DownloadStatus.UrlNotFound ||
		     status == DownloadStatus.Retrieved)
			return true;
		else
			return false;
	}
	
	public int getPctComplete() {
		if ( filesize == 0)
			return 0;
		else {
			double prcnt = (double) downloadsize * 1.0 / filesize * 100;
			return (int) prcnt;
		}		
	}

	public long getTime() {
		if ( endtime == 0) {
			return System.currentTimeMillis() - getStarttime();
		} else {
			return endtime - getStarttime();
		}
	}

	public long getEstimatedTotal() {
		if ( getTime() == 0)
			return -1;
		long esttotal = getTime()/getPctComplete()*100;
		return esttotal;
	}

	public long getEstimatedTimeLeft() {
		if ( getEstimatedTotal() == -1)
			return -1;
		return getEstimatedTotal() - getTime();
	}

	public downloadTask(String uicontext, String downloadKey, String url, String filename) {
		this.downloadKey = downloadKey;
		this.url = url;
		this.filename = filename;
		this.uicontext = uicontext;
	}
	
	@Override
	public void run() {
		setStatus(DownloadStatus.Running);

		ortus.api.DebugLog(LogLevel.Trace, "downloadTask: Starting UIC: " + getUicontext() + " Key: " + downloadKey + " Url: " + getUrl() + " File: " + getFilename() + " Overwrite: " + overwrite + " AutoClean: " + autoclean);
		int size = 1024;

		OutputStream outStream = null;
		HttpURLConnection uCon = null;
		InputStream is = null;

		starttime = System.currentTimeMillis();

		try {
			URL Url;
			byte[] buf;
			int ByteRead;
			Url = new URL(getUrl());

			File df = new File(getFilename().trim());
			File dd = new File(df.getParent());

			if (!dd.exists()) {
				ortus.api.DebugLog(LogLevel.Trace, "Path: " + getFilename().trim() + " Not Found, Creating");
				dd.mkdirs();
			}

			if (df.exists() && ! overwrite ) {
				ortus.api.DebugLog(LogLevel.Info, "urldownload: file: " + df.getAbsolutePath() + " already exists, skipping download");
				setStatus(DownloadStatus.Completed);
				return;
			}

			if (ortus.api.GetSageProperty("ortus/proxy", null) != null) {
				String[] prox = ortus.api.GetSageProperty("ortus/proxy", null).split(":");
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(prox[0], Integer.parseInt(prox[1])));
				uCon = (HttpURLConnection) Url.openConnection(proxy);
			} else {
				uCon = (HttpURLConnection) Url.openConnection();
			}

			uCon.setInstanceFollowRedirects(true);
                        uCon.setReadTimeout(60000);

                        switch(ua) {
                            case Quicktime:
                                uCon.setRequestProperty("User-Agent","QuickTime/7.6.5 (qtver=7.6.5;os=Windows NT 5.1Service Pack 3)");
                                break;
                            default:
                                uCon.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1");
                        }
			
			uCon.connect();

			if (uCon.getResponseCode() != uCon.HTTP_OK) {
				if (uCon.getResponseCode() == uCon.HTTP_NOT_FOUND) {
					ortus.api.DebugLog(LogLevel.Info, " URL not found");
					setStatus(DownloadStatus.UrlNotFound);
					return;
				}
				ortus.api.DebugLog(LogLevel.Error, " HTTP Response: " + uCon.getResponseCode());
				ortus.api.DebugLog(LogLevel.Error, " HTTP GET Failed");
				setStatus(DownloadStatus.Failed);
				return;
			}
			filesize = uCon.getContentLength();
			is = uCon.getInputStream();
			outStream = new BufferedOutputStream(new FileOutputStream(getFilename().trim()));

			buf = new byte[size];
			ortus.api.DebugLog(LogLevel.Trace2, "Starting download: " + getUrl());

			while ((ByteRead = is.read(buf)) != -1) {
				if ( dodownload == false) {
					break;
				}
				outStream.write(buf, 0, ByteRead);
				downloadsize+=ByteRead;
			}

			endtime = System.currentTimeMillis();
			if ( dodownload)
				ortus.api.DebugLog(LogLevel.Trace, "downloadTask: Key: " + downloadKey + " Successful Download File name:\"" + getFilename() + "\" No ofbytes: " + downloadsize + " Time: " + getTime() + " ms");
			else
				ortus.api.DebugLog(LogLevel.Trace, "downloadTask: Key: " + downloadKey + " was canceled");
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "http download exception occured: " + e);
			try {
				is.close();
				outStream.close();
				setStatus(DownloadStatus.Failed);
				return;
			} catch (IOException e2) {
				ortus.api.DebugLog(LogLevel.Error, "http close exception: " + e2);
				setStatus(DownloadStatus.Failed);
				return;
			}
		} finally {
			try { is.close(); } catch(Exception e) {}
			try { outStream.close(); } catch(Exception e) {}
		}
		if ( dodownload == false) {
			File df = new File(getFilename().trim());
			if ( df.exists())
				df.delete();
		}
		setStatus(DownloadStatus.Completed);
	}

	/**
	 * @return the uicontext
	 */ public String getUicontext() {
		return uicontext;
	}

	/**
	 * @return the sagetrigger
	 */ public String getSagetrigger() {
		return sagetrigger;
	}

	/**
	 * @param sagetrigger the sagetrigger to set
	 */ public void setSagetrigger(String sagetrigger) {
		this.sagetrigger = sagetrigger;
	}

	/**
	 * @return the autoclean
	 */ public boolean isAutoclean() {
		return autoclean;
	}

	/**
	 * @param autoclean the autoclean to set
	 */ public void setAutoclean(boolean autoclean) {
		this.autoclean = autoclean;
	}

	/**
	 * @return the overwrite
	 */ public boolean isOverwrite() {
		return overwrite;
	}

	/**
	 * @param overwrite the overwrite to set
	 */ public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	 public boolean isReadyToRun() {
		 if( status == DownloadStatus.WaitingToStart)
			 return true;
		 else
			 return false;
	 }
	/**
	 * @param status the status to set
	 */ public void setStatus(DownloadStatus status) {
		this.status = status;
	}

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return the starttime
     */
    public long getStarttime() {
        return starttime;
    }

    /**
     * @return the ua
     */
    public UserAgent getUa() {
        return ua;
    }

    /**
     * @param ua the ua to set
     */
    public void setUa(UserAgent ua) {
        this.ua = ua;
    }

}
