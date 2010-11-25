/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ortus.onlinescrapper;

import java.io.IOException;
import ortus.onlinescrapper.themoviedb.ImageItem;
import ortus.onlinescrapper.tools.database;
import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import ortus.configurationEngine;
import ortus.onlinescrapper.themoviedb.Movie;
import ortus.onlinescrapper.themoviedb.SearchResult;
import ortus.onlinescrapper.themoviedb.TheMovieDB;
import ortus.onlinescrapper.thetvdb.Actor;
import ortus.onlinescrapper.thetvdb.Episode;
import ortus.onlinescrapper.thetvdb.Series;
import ortus.onlinescrapper.thetvdb.TheTVDB;
import ortus.onlinescrapper.tools.SageMetadata;
import sagex.api.Configuration;
import sagex.api.Database;
import sagex.api.MediaFileAPI;

/**
 *
 * @author SBANTA
 */
public class api extends ortus.vars {

	private static Logger log = Logger.getLogger("ortus.onlinescrapper.api");
	public static boolean manual_index_running = false;
	public static boolean index_running = false;
	public static boolean local_index_running = false;
	public static ScanType st = ScanType.None;
	public static boolean cancel_scan = false;
	public static int total_queue = 0;
	public static int total_matches = 0;
	public static int total_music = 0;
	public static int total_pictures = 0;
	public static int total_match_property = 0;
	public static int total_movies_match = 0;
	public static int total_miss = 0;
	public static int total_series_match = 0;
	public static int total_bypass = 0;
	public static int total_processed = 0;
	public static int current_mediaid;
	public static long scan_start_time = 0;
	public static long scan_end_time = 0;
	static final String TYPE_Recording = "1";
	static final String TYPE_Imported = "2";
	public static final int METADATA_none = 0;
	public static final int METADATA_update = 1;
	public static final int METADATA_full = 2;
	public static final int FANART_none = 0;
	public static final int FANART_update = 1;
	public static final int FANART_full = 2;

	private static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * Check if indexMedia process is running
	 * @return
	 */
	public static boolean IsindexMediaRunning() {
		return ortus.onlinescrapper.api.index_running;
	}

	public static void SetIndexMediaRunning(boolean updated_scan_status) {
		ortus.onlinescrapper.api.index_running = updated_scan_status;
	}

	public static ScanType GetScanType() {
		return st;
	}

	/**
	 * Cancel currently running scan
	 */
	public static void CancelIndexMedia() {
		ortus.onlinescrapper.api.cancel_scan = true;
	}

	/**
	 * Get the queue size of the current media index scan
	 * @return queue size
	 */
	public static int GetScrapperQueueSize() {
		return ortus.onlinescrapper.api.total_queue;
	}

	/**
	 * Get the number of media items processed by the background scanning process
	 * @return
	 */
	public static Object GetScrapperProcessedRemote() {
		return ortus.daemon.api.executecCMD("192.168.0.15", "ortus.api.GetScrapperProcessed", null);
	}

	public static int GetScrapperProcessed() {
		return ortus.onlinescrapper.api.total_processed;
	}

	public static void SetScrapperDetail(HashMap<String,Object> result) {
//		DebugLog(LogLevel.Trace,"CronUpdateScanStatus: running");
//		DebugLog(LogLevel.Trace,"result: " + result.toString());
		if( ! local_index_running ) {
			ortus.onlinescrapper.api.index_running = (Boolean)result.get("index_running");
			ortus.onlinescrapper.api.total_queue = (Integer)result.get("total_queue");
			ortus.onlinescrapper.api.total_matches = (Integer)result.get("total_matches");
			ortus.onlinescrapper.api.total_movies_match = (Integer)result.get("total_movies_match");
			ortus.onlinescrapper.api.total_series_match = (Integer)result.get("total_series_match");
			ortus.onlinescrapper.api.total_match_property = (Integer)result.get("total_match_property");
			ortus.onlinescrapper.api.total_miss = (Integer)result.get("total_miss");
			ortus.onlinescrapper.api.total_bypass = (Integer)result.get("total_bypass");
			ortus.onlinescrapper.api.total_processed = (Integer)result.get("total_processed");
			ortus.onlinescrapper.api.current_mediaid = (Integer)result.get("current_mediaid");
			ortus.onlinescrapper.api.scan_start_time = (Long)result.get("scan_start_time");
			ortus.onlinescrapper.api.scan_end_time = (Long)result.get("scan_end_time");
			ortus.onlinescrapper.api.total_music = (Integer)result.get("total_music");
			ortus.onlinescrapper.api.total_pictures = (Integer)result.get("total_pictures");
			ortus.onlinescrapper.api.st = (ScanType)result.get("scantype");
		}
	}

	public static HashMap<String, Object> GetScrapperDetail() {
		HashMap<String, Object> sd = new HashMap<String, Object>();
		sd.put("total_queue", total_queue);
		sd.put("total_match_property", total_match_property);
		sd.put("total_matches", total_matches);
		sd.put("total_movies_match", total_movies_match);
		sd.put("total_miss", total_miss);
		sd.put("total_series_match", total_series_match);
		sd.put("total_bypass", total_bypass);
		sd.put("total_processed", total_processed);
		sd.put("current_mediaid", current_mediaid);
		sd.put("scan_start_time", scan_start_time);
		sd.put("scan_end_time", scan_end_time);
		sd.put("total_music", total_music);
		sd.put("total_pictures",total_pictures);
		sd.put("index_running", index_running);
		sd.put("scantype", st);
		return sd;
	}

	/**
	 * Return the percentage completed for a background scan
	 * @return Percentage
	 */
	public static int GetScrapperPercentComplete() {
		float result = (float) (ortus.onlinescrapper.api.total_processed * 1.0 / ortus.onlinescrapper.api.total_queue * 100);
		return Math.round(result);
	}

	/**
	 * Return the percentage of matched media items
	 * @return Percentage
	 */
	public static int GetScrapperPercentMatched() {
		float result = (float) (GetScrapperMatched() * 1.0 / (ortus.onlinescrapper.api.total_queue - ortus.onlinescrapper.api.total_bypass) * 100);
		return Math.round(result);
	}

	/**
	 * Return the percentage of matched media items
	 * @return Percentage
	 */
	public static int GetScrapperPercentMatchedLibrary() {
		List<List> dbresult = ortus.api.executeSQLQueryArray("select count(*) from sage.media where mediatype != 0");
		int matched = 0;
		if (dbresult.size() == 1) {
			matched = Integer.parseInt((String) dbresult.get(0).get(0));
			float result = (float) (matched * 1.0 / ortus.onlinescrapper.api.total_queue * 100);
			return Math.round(result);
		} else {
			return 0;
		}
	}

	/**
	 * Return the percentage of missed media items
	 * @return
	 */
	public static int GetScrapperPercentMissed() {
		float result = (float) (GetScrapperMissed() * 1.0 / (ortus.onlinescrapper.api.total_queue - ortus.onlinescrapper.api.total_bypass) * 100);
		return Math.round(result);
	}

	/**
	 * Return the percentage of matched media items
	 * @return Percentage
	 */
	public static int GetScrapperPercentMissedLibrary() {
		List<List> dbresult = ortus.api.executeSQLQueryArray("select count(*) from sage.media where mediatype = 0");
		int unmatched = 0;
		if (dbresult.size() == 1) {
			unmatched = Integer.parseInt((String) dbresult.get(0).get(0));
			float result = (float) (unmatched * 1.0 / ortus.onlinescrapper.api.total_queue * 100);
			return Math.round(result);
		} else {
			return 0;
		}
	}

	/**
	 * Get the current media id being processed by the background index scan
	 * @return
	 */
	public static int GetScrapperCurrent() {
		return ortus.onlinescrapper.api.current_mediaid;
	}

	/**
	 * Get the number of matched items in the background index scan
	 * @return
	 */
	public static int GetScrapperMatched() {
		return ortus.onlinescrapper.api.total_movies_match + ortus.onlinescrapper.api.total_series_match;
	}

	/**
	 * Return number of items that matched from a property file
	 */
	public static int GetScrapperMatchedProperty() {
		return ortus.onlinescrapper.api.total_match_property;
	}

	/**
	 * Return total media items bypassed in background index scan
	 * @return
	 */
	public static int GetScrapperBypassed() {
		return ortus.onlinescrapper.api.total_bypass;
	}

	/**
	 * Get the number of missed items in the background index scan
	 * @return
	 */
	public static int GetScrapperMissed() {
		return ortus.onlinescrapper.api.total_miss;
	}

	/**
	 * Return the start time of background index scan
	 * @return
	 */
	public static long GetScrapperStartTime() {
		return ortus.onlinescrapper.api.scan_start_time;
	}

	/**
	 * Return the end of the last background index scan
	 * @return
	 */
	public static long GetScrapperEndTime() {
		return ortus.onlinescrapper.api.scan_end_time;
	}

	/**
	 * Return the number of minutes the background index scan has been running
	 * @return
	 */
	public static String GetScrapperElapsedMinutes() {

		long millis = new Date().getTime() - ortus.onlinescrapper.api.scan_start_time;

		return String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	public static List<Object> GetScrapperUnmatchedMedia() {
		return ortus.api.getMediaFilesSQL("select m.mediaid from sage.media as m, sage.scrapperlog as s where m.mediatype = 0 and m.mediaid = s.mediaid group by m.mediaid order by m.mediatitle");
	}

	public static List<Object> GetScrapperUnmatchedMediaRemote() {
		return ortus.api.executeSQLQuery("select m.mediaid from sage.media as m, sage.scrapperlog as s where m.mediatype = 0 and m.mediaid = s.mediaid group by m.mediaid order by m.mediatitle");
	}

	public static List<Object> GetScrapperMatchedMedia() {
		return ortus.api.getMediaFilesSQL("select m.mediaid from sage.media as m, sage.scrapperlog as s where m.mediatype != 0 and m.mediaid = s.mediaid group by m.mediaid order by m.mediatitle");
	}

	public static List<Object> GetScrapperMatches(Object mediafile) {
		return GetScrapperMatches(mediafile, 9);
	}

	public static List<Object> GetScrapperMatches(Object mediafile, int scantype) {
		String sql = "select scrapperid from sage.scrapperlog where mediaid = " + MediaFileAPI.GetMediaFileID(mediafile);
		if (scantype != 9) {
			sql += " and scantype = " + scantype;
		}

		return ortus.api.executeSQLQuery(sql);
	}

	public static int GetScrapperScanType(Object scrapperid) {
		List<Object> result = ortus.api.executeSQLQuery("select scantype from sage.scrapperlog where scrapperid = " + scrapperid);
		if (result.size() > 0) {
			return Integer.parseInt((String) result.get(0));
		} else {
			return -1;
		}

	}

	public static String GetScrapperSearchTitle(Object scrapperid) {
		List<Object> result = ortus.api.executeSQLQuery("select searchtitle from sage.scrapperlog where scrapperid = " + scrapperid);
		if (result.size() > 0) {
			return (String) result.get(0);
		} else {
			return null;
		}
	}

	public static String GetScrapperFoundTitle(Object scrapperid) {
		List<Object> result = ortus.api.executeSQLQuery("select foundtitle from sage.scrapperlog where scrapperid = " + scrapperid);
		if (result.size() > 0) {
			return (String) result.get(0);
		} else {
			return null;
		}
	}

	public static String GetScrapperFoundKey(Object scrapperid) {
		List<Object> result = ortus.api.executeSQLQuery("select foundkey from sage.scrapperlog where scrapperid = " + scrapperid);
		if (result.size() > 0) {
			return (String) result.get(0);
		} else {
			return null;
		}
	}

	public static void cleanMedia() {
		HashMap<String, String> validmedia = new HashMap<String, String>();
		int totalclean = 0;
		ortus.api.DebugLog(LogLevel.Debug, "cleanMedia: Starting");
		List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles("VDBMP")));

		for (Object o : mfl) {
			validmedia.put(String.valueOf(MediaFileAPI.GetMediaFileID(o)), "valid");
		}

		List<Object> result = ortus.api.executeSQLQuery("select mediaid, mediatype from sage.media");

		for (Object o : result) {
			String[] rs = ((String) o).split(",");

			if (validmedia.get(rs[0]) == null) {
				totalclean++;
				ortus.api.executeSQL("delete from sage.media where mediaid = " + rs[0]);
				ortus.api.executeSQL("delete from sage.usermedia where mediaid = " + rs[0]);
				if (rs[1].equals("3")) {
					ortus.api.executeSQL("delete from sage.episode where episodeid = 999 and mediaid = " + rs[0]);
					ortus.api.executeSQL("update sage.episode set mediaid = null where mediaid = " + rs[0]);
				} else {
					ortus.api.executeSQL("update sage.movies set mediaid = 0 where mediaid = " + rs[0]);
					ortus.cache.cacheEngine.getInstance().getProvider().Remove(rs[0]);
				}
			}
		}

		ortus.api.executeSQL("update sage.media set mediatype = 0 where mediatype = 3 and mediaid not in ( select mediaid from sage.episode where mediaid is not null)");
		ortus.api.DebugLog(LogLevel.Debug, "cleanMedia: Completed, removed: " + totalclean);

		return;
	}

	public static void cleanMediaObject(Object smo) {
		int mediaid = MediaFileAPI.GetMediaFileID(smo);
		ortus.api.DebugLog(LogLevel.Debug, "cleanMedia: Starting for MediaID: " + mediaid);

		ortus.api.executeSQL("delete from sage.media where mediaid = " + mediaid);
		ortus.api.executeSQL("delete from sage.usermedia where mediaid = " + mediaid);
		ortus.api.executeSQL("delete from sage.episode where episodeid = 999 and mediaid = " + mediaid);
		ortus.api.executeSQL("update sage.episode set mediaid = null where mediaid = " + mediaid);
                ortus.api.executeSQL("update sage.movies set mediaid = 0 where mediaid = " + mediaid);
		ortus.api.executeSQL("delete from sage.music where mediaid = " + mediaid);
		ortus.cache.cacheEngine.getInstance().getProvider().Remove(mediaid);

//		ortus.api.executeSQL("update sage.media set mediatype = 0 where mediatype = 3 and mediaid not in ( select mediaid from sage.episode where mediaid is not null)");
		ortus.api.DebugLog(LogLevel.Debug, "cleanMedia: Completed");

		return;
	}

	public static void indexMedia() {
		indexMediaAll(METADATA_update, FANART_update);
	}

	public static void indexMediaRecordings() {
		st = ScanType.RecordingScan;
		List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles("T")));
		Database.FilterByBoolMethod(mfl, "IsTVFile", true);

		indexMedia(false, mfl, METADATA_update, FANART_update);

		cleanMedia();

		st = ScanType.None;
		return;
	}

	public static void indexMediaAll(Integer metadata, Integer fanart) {
		st = ScanType.FullScan;
		List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles("TVDBMP")));

		indexMedia(false, mfl, metadata, fanart);

		cleanMedia();

		Configuration.SetServerProperty("ortus/fanart/fullscan", "false");
		st = ScanType.None;
		return;
	}

	public static void indexMediaQuick(Integer metadata, Integer fanart) {
		st = ScanType.FullScan;
		List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles("TVDBMP")));

		indexMedia(true, mfl, metadata, fanart);

		cleanMedia();

		st = ScanType.None;
		return;
	}

	public static void indexMediaMusic(int metadata, int fanart) {
		st = ScanType.FullScan;
		List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles("ML")));
//    List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles()));
//    Database.FilterByBoolMethod(mfl, "IsTVFile|IsVideoFile|IsDVD|IsBluRay", true);

		indexMedia(true, mfl, metadata, fanart);

		cleanMedia();

		st = ScanType.None;
		return;
	}

	public static void indexMediaPictures(int metadata, int fanart) {
		st = ScanType.FullScan;
		List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles("PL")));
//    List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles()));
//    Database.FilterByBoolMethod(mfl, "IsTVFile|IsVideoFile|IsDVD|IsBluRay", true);

		indexMedia(true, mfl, metadata, fanart);

		cleanMedia();

		st = ScanType.None;
		return;
	}

	public static void GetTVDBServerTime() {
		ortus.api.DebugLog(LogLevel.Trace, "GetTVDBServerTime: Starting");
		TheTVDB tvdb = new TheTVDB();

		tvdb.getServerTime();

		ortus.api.DebugLog(LogLevel.Trace, "GetTVDBServerTime: Completed");
	}

	public static void GetTVDBUpdates() {
		ortus.api.DebugLog(LogLevel.Trace, "GetTVDBUpdates: Starting");

		TheTVDB tvdb = new TheTVDB();

		if (Long.parseLong(Configuration.GetServerProperty("ortus/tvdb/time", "0")) == 0) {
			ortus.api.GetTVDBServerTime();
			return;
		}

		tvdb.getUpdates();

		ortus.api.DebugLog(LogLevel.Trace, "GetTVDBUpdates: Completed");
	}

	public static void indexMedia(boolean quickscan, List<Object> mfl, int metadata, int fanart) {
		if ( index_running ) {
			ortus.api.DebugLog(LogLevel.Warning, "Ortus: indexMedia Already running");
			return;
		}
		index_running = true;
		local_index_running = true;
		cancel_scan = false;
		scan_start_time = new Date().getTime();
		scan_end_time = 0;
		total_queue = mfl.size();
		total_match_property = 0;
		total_movies_match = 0;
		total_miss = 0;
		total_series_match = 0;
		total_bypass = 0;
		total_processed = 0;
		total_music = 0;
		total_pictures = 0;
		int total_exceptions = 0;
		long t0 = System.currentTimeMillis();

		ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High, ortus.mq.vars.EvenType.Clients,"ScanCountUpdate", new Object[] { GetScrapperDetail() } );
		ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"STVPanelRefresh", new Object[] { "IsIndexMediaRunning" } );
		ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Local,"UserMessage", new Object[] { ortus.mq.vars.SystemMsgLevel.Status, "MediaIndexScan Started", "The Ortus media index Scan has started" } );
//		ortus.api.AddCron(1,"ortus.api.FireJMSMessage", new Object[] { ortus.ortusvars.EvenType.Broadcast, "STVPanelRefresh", new Object[] { "IndexMediaTime"} });
		
		ortus.api.DebugLog(LogLevel.Info, "indexMedia: Starting");

		HashMap<String, String> mdb = new HashMap<String, String>();

		List<String> bypassregex = ortus.api.DumpLogFile(ortus.api.GetProperty("ortus/basepath", "") + java.io.File.separator + "Configuration" + java.io.File.separator + "indexbypass.regex");

		List<Object> dbresult = ortus.api.executeSQLQuery("select mediaid from sage.media where mediatype in (1,2,3,4,5,6)");
		if (dbresult.size() > 0) {
			ortus.api.DebugLog(LogLevel.Trace, " Loading " + dbresult.size() + " existing mediaobjects");
		}
		for (Object o : dbresult) {
			mdb.put((String) o, (String) o);
		}

		for (Iterator<Object> iter = mfl.iterator(); iter.hasNext();) {
			try {

				Object obj = iter.next();
				if (cancel_scan) {
					cancel_scan = false;
					break;
				}

				current_mediaid = MediaFileAPI.GetMediaFileID(obj);
				total_processed++;

				MediaObject mo = new MediaObject(obj);
				mo.setMetadata(metadata);
				mo.setFanart(fanart);
				if (quickscan) {
					mo.setScantype(2);
				} else {
					mo.setScantype(0);
				}

				if (mdb.get(String.valueOf(MediaFileAPI.GetMediaFileID(obj))) != null) {
					mo.setMetadatafound(true);
				}

				if (mo.isMetadatafound()
					&& (metadata == METADATA_none || metadata == METADATA_update)
					&& (fanart == FANART_none || fanart == FANART_update)) {
					total_bypass++;
					continue;
				}

				if (mo.getMediagroup() == MediaGroup.Recorded && mo.isMetadatafound()) {
					total_bypass++;
					continue;
				}

				ortus.api.DebugLog(LogLevel.Trace, "indexMedia: Title: " + MediaFileAPI.GetMediaTitle(obj) + " id: " + MediaFileAPI.GetMediaFileID(obj));
				if (MediaFileAPI.GetMediaTitle(obj).isEmpty()) {
					ortus.api.DebugLog(LogLevel.Trace, "indexMedia: media title is empty for: " + obj);
					total_bypass++;
					continue;
				}

				File mf = MediaFileAPI.GetFileForSegment(obj, 0);
				if (mf == null) {
					ortus.api.DebugLog(LogLevel.Error, "indexMedia: media file is null for: " + obj);
					total_bypass++;
					continue;
				}

				if (mf.getAbsoluteFile().toString().contains("Hauppauge") || mf.getAbsolutePath().endsWith("mpgbuf")) {
					ortus.api.DebugLog(LogLevel.Trace, "indexMedia: media file has tuner for: " + obj);
					total_bypass++;
					continue;
				}

				List<Object> result = ortus.api.executeSQLQuery("select mediaid from sage.media where mediaid = " + MediaFileAPI.GetMediaFileID(obj));
				if (result.size() > 0) {
					mo.setMedia_exist(true);
				} else {
					ortus.api.DebugLog(LogLevel.Trace, " media not found for " + MediaFileAPI.GetMediaFileID(obj));
				}

				boolean bypass_media = false;
				for (String rx : bypassregex) {
					Pattern pattern = Pattern.compile(rx);
					Matcher matcher = pattern.matcher(mf.getAbsolutePath());
					if (matcher.matches()) {
						ortus.api.DebugLog(LogLevel.Trace, "BypassRegex: found " + mf.getAbsolutePath());
						if (!mo.isMedia_exist()) {
							ortus.api.DebugLog(LogLevel.Trace, "BypassRegex: adding file to the db");
							mo.setMediatype(MediaType.Home);
							mo.WriteDB();
						}
						bypass_media = true;
					}
					if (bypass_media) {
						break;
					}
				}

				if (bypass_media) {
					total_bypass++;
					continue;
				}

				if ( mo.isMusic())
					total_music++;
				if ( mo.isPicture())
					total_pictures++;

				if (quickscan) {
					mo.WriteDB();
				} else {
					if ( mo.isMusic() || mo.isPicture()) {
						mo.WriteDB();
					} else if ( mo.isVideo()) {
						if (GetMediaMetaData(mo)) {
							if (mo.isMediaTypeSeries()) {
								total_series_match++;
							} else {
								total_movies_match++;
							}
//							mo.WriteProperty();
						} else {
							total_miss++;
						}
					}
                                    ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Clients,"ScanCountUpdate", new Object[] { GetScrapperDetail() } );
                                    ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"STVPanelRefresh", new Object[] { "IndexMediaDetails" } );
				}
			} catch (Exception e) {
				ortus.api.DebugLog(LogLevel.Error, "indexMedia: Exception",e);
				total_exceptions++;
				continue;
			}
		}

		ortus.api.executeSQL("analyze sample_size 0");

		if (quickscan) {
			ortus.cache.cacheEngine.getInstance().ReLoadCache();
		}

		index_running = false;
		local_index_running = false;
		scan_end_time = new Date().getTime();
		if (total_match_property > 0) {
			ortus.api.DebugLog(LogLevel.Info, "indexMedia: Total Matched using Property: " + total_match_property);
		}
		if (total_music > 0) {
			ortus.api.DebugLog(LogLevel.Info, "indexMedia: Total Music Matched: " + total_music);
		}
		if (total_pictures > 0) {
			ortus.api.DebugLog(LogLevel.Info, "indexMedia: Total Pictures Matched: " + total_pictures);
		}		
		if (total_movies_match > 0) {
			ortus.api.DebugLog(LogLevel.Info, "indexMedia: Total Movies Matched: " + total_movies_match);
		}
		if (total_series_match > 0) {
			ortus.api.DebugLog(LogLevel.Info, "indexMedia: Total Series Matched: " + total_series_match);
		}
		if (total_miss > 0) {
			ortus.api.DebugLog(LogLevel.Info, "indexMedia: Total Missed: " + total_miss);
		}
		if (total_bypass > 0) {
			ortus.api.DebugLog(LogLevel.Info, "indexMedia: Total Bypassed: " + total_bypass);
		}
		if (total_processed > 0) {
			ortus.api.DebugLog(LogLevel.Info, "indexMedia: Total Processed: " + total_processed);
		}
		if (total_exceptions > 0) {
			ortus.api.DebugLog(LogLevel.Info, "indexMedia: Total Exceptions: " + total_exceptions);
		}

		long t1 = System.currentTimeMillis() - t0;
		ortus.api.DebugLog(LogLevel.Info, "indexMedia: Completed Time: " + t1 + " ms");

		ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Clients,"ScanCountUpdate", new Object[] { GetScrapperDetail() } );
		ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"STVPanelRefresh", new Object[] { "IsIndexMediaRunning" } );
		ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Local,"UserMessage", new Object[] { ortus.mq.vars.SystemMsgLevel.Status, "MediaIndexScan Completed", "The Ortus media index scan completed processing " + total_processed + " media items.  The matchrate was " + GetScrapperPercentMatched() } );
		
		return;
	}

	public static boolean GetMediaMetaData(MediaObject mo) {

		boolean metadata_found = false;

		ortus.api.DebugLog(LogLevel.Info, "GetMediaMetaData: Starting for: " + MediaFileAPI.GetMediaFileID(mo.getMedia()));

		List<String> regex = null;
		try {
			regex = IOUtils.readLines(configurationEngine.getInstance().getJarStream("/ortus/resources/indexmedia.regex"));
		} catch (IOException ex) {
			ortus.api.DebugLog(LogLevel.Error,"GetMediaMetaData: Exception: " , ex);
		}
		if( regex.size() == 0)
			ortus.api.DebugLog(LogLevel.Trace,"indexmedia regex is emtpy");
		else
			ortus.api.DebugLog(LogLevel.Trace,"indexmedia regex has " + regex.size() + " entries");
		List<String> mediaregex = ortus.api.DumpLogFile(ortus.api.GetProperty("ortus/basepath", "") + java.io.File.separator + "Configuration" + java.io.File.separator + "mediamatch.regex");

		if (MediaFileAPI.GetMediaTitle(mo.getMedia()).isEmpty()) {
			return false;
		}

		File mf = MediaFileAPI.GetFileForSegment(mo.getMedia(), 0);

		if (mf == null) {
			return false;
		}

		if (mf.getAbsoluteFile().toString().contains("Hauppauge")) {
			return false;
		}

		List<Object> result = ortus.api.executeSQLQuery("select mediaid from sage.media where mediaid = " + MediaFileAPI.GetMediaFileID(mo.getMedia()));
		if (result.size() > 0) {
			mo.isMedia_exist();
		} else {
			ortus.api.DebugLog(LogLevel.Trace, " media not found for " + MediaFileAPI.GetMediaFileID(mo.getMedia()));
		}

		if (mo.isMediaTypeRecording()) {
			mo.setLikely_tv(true);
		} else {
			String ShowPath = "";

			if (mf.getName().equals("VIDEO_TS") || mf.getName().equals("BDMV")) {
				String workname = mf.getParent();
				mo.setShowtitle(workname.substring(workname.lastIndexOf(java.io.File.separator) + 1));
			} else {
				if (mf.getName().contains(".")) {
					mo.setShowtitle(mf.getName().substring(0, mf.getName().lastIndexOf(".")));
				} else {
					mo.setShowtitle(mf.getName());
				}
			}
		}

		for (String rx : regex) {
//			ortus.api.DebugLog(LogLevel.Trace,"     Series REGEX matching against: " + rx + " for show: " + mo.getShowtitle());
			Pattern pattern = Pattern.compile(rx);
			Matcher matcher = pattern.matcher(mo.getShowtitle());
			if (matcher.matches()) {
				if (matcher.groupCount() == 3) {
					mo.setLikely_tv(true);
					ortus.api.DebugLog(LogLevel.Trace, "Show: " + matcher.group(1));
					ortus.api.DebugLog(LogLevel.Trace, "Season No: " + matcher.group(2));
					ortus.api.DebugLog(LogLevel.Trace, "Episode No: " + matcher.group(3));
					mo.setShowtitle(matcher.group(1));
					mo.setSeasonno(matcher.group(2));
					mo.setEpisodeno(matcher.group(3));
				} else {
					ortus.api.DebugLog(LogLevel.Trace, "Match found group: " + matcher.groupCount());
				}
			}
		}

                if ( ortus.api.GetSageProperty("ortus/metadata/useproperty","true").equalsIgnoreCase("true")) {
                    if (mo.ReadProperty()) {
                            total_match_property++;
                    }
                }

		if (mo.isLikely_tv() || mo.getTvdbid() != null) {
//		mo.SetShowTitle(CleanName(mo.GetShowTitle()));  /* jeff */
			if (TVSearch(mo, mediaregex)) {
				metadata_found = true;
				ortus.api.DebugLog(LogLevel.Trace, "TV Matched");
			}
		}
		if (!metadata_found) {
			mo.setMetadatasource("Google");
			if (MovieSearch(mo, mediaregex)) {
				metadata_found = true;
				ortus.api.DebugLog(LogLevel.Trace, "Movie Matched");
			}
		}
		if (!metadata_found && !mo.isLikely_tv()) {
//		mo.SetShowTitle(CleanName(mo.GetShowTitle()));
			if (TVSearch(mo, mediaregex)) {
				metadata_found = true;
				ortus.api.DebugLog(LogLevel.Trace, "TV Matched");
			}
		}

		if (!metadata_found) {
			mo.setMediatype(MediaType.Home);
			mo.WriteDB();
                } else {
                        if ( ! MediaFileAPI.IsTVFile(mo.getMedia()))
                             if ( SageMetadata.createShow(mo) )
                                  ortus.api.DebugLog(LogLevel.Info, "GetMediaMetaData: Modified Wiz.bin with metadata");

                }

                String mkey = "MD".concat(String.valueOf(MediaFileAPI.GetMediaFileID(mo.getMedia())));
		ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { mkey } );

		ortus.api.DebugLog(LogLevel.Info, "GetMediaMetaData: Completed");

		return metadata_found;
	}

	public static void AutoFileMatch(Object smo) {
		boolean media_found = false;
		int retry_loop = 0;
		Object mf = null;
		while (retry_loop < 5) {
			retry_loop++;
			if (smo instanceof String) {
				ortus.api.DebugLog(LogLevel.Info, "AutoFileMatch: Processing: File: " + smo);
				mf = MediaFileAPI.GetMediaFileForFilePath(new File((String) smo));
			} else {
				ortus.api.DebugLog(LogLevel.Info, "AutoFileMatch: Processing: MediaID: " + MediaFileAPI.GetMediaFileID(smo));
				mf = smo;
			}
			if (mf != null) {
				if (MediaFileAPI.IsMusicFile(mf)) {
					ortus.api.DebugLog(LogLevel.Info, "AutoFileMatch: type music");
					MediaObject mo = new MediaObject(mf);
					mo.setFanart(1);
					mo.setMetadata(1);
					mo.WriteDB();

				}

				if (MediaFileAPI.IsPictureFile(mf)) {
					ortus.api.DebugLog(LogLevel.Info, "AutoFileMatch: type picture");
					MediaObject mo = new MediaObject(mf);
					mo.setFanart(1);
					mo.setMetadata(1);
					mo.WriteDB();
				}
				if (MediaFileAPI.IsVideoFile(mf)
					|| MediaFileAPI.IsDVD(mf)
					|| MediaFileAPI.IsBluRay(mf)) {
					ortus.api.DebugLog(LogLevel.Info, "AutoFileMatch: type video");
					MediaObject mo = new MediaObject(mf);
					mo.setFanart(1);
					mo.setMetadata(1);
					GetMediaMetaData(mo);
				}
				break;
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
					ortus.api.DebugLog(LogLevel.Error, "AuthFileMatch: Sleep Exception: " , ex);
					return;
				}
			}

		}

		ortus.api.DebugLog("LogLevel.Info", "AutoFileMatch: Completed");

		return;
	}

	public static boolean TVSearch(MediaObject mo, List<String> mediaregex) {
		boolean xmlexist = false;
		ortus.api.DebugLog(LogLevel.Trace, "TVSearch: title " + mo.getShowtitle() + " episode: " + mo.getEpisodetitle());
		if ( ! mo.getSeasonno().isEmpty())
			ortus.api.DebugLog(LogLevel.Trace, " Season: " + mo.getSeasonno() + " Episode: " + mo.getEpisodeno());
		String ortustvdbpath = ortus.api.GetProperty("ortus/basepath", "") + java.io.File.separator + "tvdb" + java.io.File.separator;
		boolean series_found = false;

		TheTVDB tvdb = new TheTVDB();

		if (mo.getTvdbid() == null) {
			HashMap<String, Series> tv = new HashMap<String, Series>();

			File tsf = new File(ortustvdbpath + mo.getShowtitle() + java.io.File.separator + "en.xml");
			if (tsf.exists() && mo.getMetadata() == 0) {
				ortus.api.DebugLog(LogLevel.Trace, "xml file is found, loading: " + tsf.getAbsolutePath());
				tv.put(mo.getShowtitle(), tvdb.getSeries(ortustvdbpath + mo.getShowtitle()));
				xmlexist = true;
			} else {
				if (tsf.exists()) {
					tsf.delete();
				}
//		ortus.api.DebugLog(LogLevel.Trace, "searching TVDB for series");
				tv = tvdb.searchSeries(mo.getShowtitle(), "en");
			}

			Object[] tk = tv.keySet().toArray();
			for (Object tt : tk) {
				if (CompareTitle((String) tt, mo.getShowtitle(), mediaregex)) {
					mo.setTvdbid(tv.get((String) tt).getId());
					mo.setShowtitle((String) tt);
				}
			}

			HashMap<String, SearchResult> logmatch = new HashMap<String, SearchResult>();
			for (Object tt : tk) {
				logmatch.put((String) tt, new SearchResult((String) tt,""));
			}
			if (logmatch.isEmpty()) {
				logmatch.put("Not Found", new SearchResult("Not Found",""));
			}
			database.LogFind(0, MediaFileAPI.GetMediaFileID(mo.getMedia()), mo.getShowtitle(), logmatch);
		}

		if (mo.getTvdbid() != null) {
			mo.setMediatype(MediaType.Series);
			series_found = true;

			if (!xmlexist) {
				tvdb.GetSeriesXML(mo.getTvdbid(), ortustvdbpath + mo.getShowtitleFiltered());
			}

			List<Episode> episodes = tvdb.getEpisodes(ortustvdbpath + mo.getShowtitleFiltered());
			Series current = tvdb.getSeries(ortustvdbpath + mo.getShowtitleFiltered());
			List<Actor> actors = tvdb.getActors(ortustvdbpath + mo.getShowtitleFiltered());
			String si = current.getId();
			if (!si.isEmpty()) {
				List<Object> result = ortus.api.executeSQLQuery("select seriesid from sage.series where seriesid = " + si);
				if (result.size() <= 0) {
					ortus.api.DebugLog(LogLevel.Trace, "Load Series into the db");
					database.WriteSeriestoDB(current, actors);

					for (Episode ce : episodes) {
						database.WriteEpisodetoDB(ce, current.getSeriesName());
					}
				}
			}
			if (mo.getMetadata() == 2 || !mo.isMetadatafound()) {
				ortus.api.DebugLog(LogLevel.Trace, "metadata will be added to the db");
				mo.setShowtitle(current.getSeriesName());
//		database.WriteMediatoDB(mo);
				database.UpdateEpisodeMediaID(mo, current);
				mo.WriteDB();
			}

			if (mo.getFanart() > 0) {
				boolean fo = false;
				if (mo.getFanart() == 1) {
					fo = false;
				}
				if (mo.getFanart() == 2) {
					fo = true;
				}
				tvdb.GetSeriesFanart(mo, ortustvdbpath, mo.getShowtitleFiltered(), fo);
			}
		}

		return series_found;
	}

	public static int ManualMovieSearch(Object mediafile, String title) {
		manual_index_running = true;
		ortus.api.DebugLog(LogLevel.Trace, "Manual Movie Search: title: " + title);

		int total_matches = 0;
		HashMap<String, SearchResult> movies;

		TheMovieDB tmdb = new TheMovieDB();
		movies = tmdb.Search(title);
		total_matches += movies.size();
		database.LogFind(1, MediaFileAPI.GetMediaFileID(mediafile), title, movies);
		Object[] mk = movies.keySet().toArray();
		for (Object mt : mk) {
			tmdb.GetDetail((String) movies.get(mt).getMetadatakey());
		}

		manual_index_running = false;

		return total_matches;
	}

        public static List<HashMap> LiveSearch(String scope, String title) {
		manual_index_running = true;
		ortus.api.DebugLog(LogLevel.Trace, "LiveSearch: scope: " + scope + " title: " + title);

                List<HashMap> results = new ArrayList<HashMap>();
		int total_matches = 0;

                if ( scope.equalsIgnoreCase("movie") || scope.equalsIgnoreCase("both")) {
                    HashMap<String, SearchResult> movies;

                    TheMovieDB tmdb = new TheMovieDB();
                    movies = tmdb.Search(title);
                    Object[] mk = movies.keySet().toArray();
                    for ( Object x : mk) {
                        HashMap entry = new HashMap();
                        entry.put("key", movies.get((String)x).getMetadatakey());
                        entry.put("source","tmdb");
                        entry.put("title", movies.get((String)x).getTitle());
                        entry.put("year", movies.get((String)x).getDate());
                        entry.put("description", movies.get((String)x).getDescription());
                        results.add(entry);
                    }
                    total_matches += movies.size();
                }

                if ( scope.equalsIgnoreCase("tv") || scope.equalsIgnoreCase("both")) {
                    HashMap<String, Series> series;

                    TheTVDB tvdb = new TheTVDB();
                    series = tvdb.searchSeries(title,"en");
                    Object[] mk = series.keySet().toArray();
                    for ( Object x : mk) {
                        HashMap entry = new HashMap();
                        entry.put("key",series.get((String)x).getId());
                        entry.put("source", "tvdb");
                        entry.put("title",series.get((String)x).getSeriesName());
                        entry.put("year", series.get((String)x).getFirstAired());
                        entry.put("description", series.get((String)x).getOverview());
                        results.add(entry);
                    }
                    total_matches += series.size();
                }

		manual_index_running = false;

		return results;
	}

        public static void LiveSearchStore(HashMap entry) {

                ortus.api.DebugLog(LogLevel.Trace,"LiveSearchStore: Storing key: " + entry.get("key"));
                if ( entry.get("key") == null || entry.get("title") == null || ! ((String)entry.get("source")).equalsIgnoreCase("tvdb")) {
                    ortus.api.DebugLog(LogLevel.Error, "LiveSearchStore: Error, key is not valid");
                    return;
                }

                if ( ((String)entry.get("source")).equalsIgnoreCase("tvdb")) {
                    boolean xmlexist = false;
                    ortus.api.DebugLog(LogLevel.Trace, "LiveSearchStore: Key: " + entry.get("key"));
                    String ortustvdbpath = ortus.api.GetProperty("ortus/basepath", "") + java.io.File.separator + "tvdb" + java.io.File.separator;
                    boolean series_found = false;

                    TheTVDB tvdb = new TheTVDB();

                    tvdb.GetSeriesXML((String)entry.get("key"), ortustvdbpath + entry.get("title"));

                    List<Episode> episodes = tvdb.getEpisodes(ortustvdbpath + entry.get("title"));
                    Series current = tvdb.getSeries(ortustvdbpath + entry.get("title"));
                    List<Actor> actors = tvdb.getActors(ortustvdbpath + entry.get("title"));
                    String si = current.getId();
                    if (!si.isEmpty()) {
                            List<Object> result = ortus.api.executeSQLQuery("select seriesid from sage.series where seriesid = " + si);
                            if (result.size() <= 0) {
                                    ortus.api.DebugLog(LogLevel.Trace, "Load Series into the db");
                                    database.WriteSeriestoDB(current, actors);

                                    for (Episode ce : episodes) {
                                            database.WriteEpisodetoDB(ce, current.getSeriesName());
                                    }
                            }
                    }
//                    tvdb.GetSeriesFanart(null, ortustvdbpath, si, xmlexist);
                } else {
                    TheMovieDB tmdb = new TheMovieDB();
                    Movie movie = tmdb.GetDetail((String)entry.get("key"));
                    if ( movie != null) {
                        if ( movie.isMetadatafound()) {
                            movie.WriteDB();
                        }
                    }
                }

                ortus.api.DebugLog(LogLevel.Trace, "LiveSearchStore: Completed");
        }
        
	public static String GetScrapperCacheDescription(Object scrapperid) {
		String scrapper = GetProviderKey(scrapperid);

		if (scrapper == null) {
			return null;
		}

		return new TheMovieDB().GetDetail(scrapper).getOverview();
	}

	public static Object GetScrapperCacheDate(Object scrapperid) {
		String scrapper = GetProviderKey(scrapperid);

		if (scrapper == null) {
			return null;
		}

		Date epoch;
		try {
			epoch = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(new TheMovieDB().GetDetail(scrapper).getReleasedate());
		} catch (ParseException ex) {
			return null;
		}
		return epoch.getTime();
	}

	public static Object GetScrapperCacheMpaa(Object scrapperid) {

		String scrapper = GetProviderKey(scrapperid);

		if (scrapper == null) {
			return null;
		}

		return new TheMovieDB().GetDetail(scrapper).getCertification();
	}

	private static String GetProviderKey(Object scrapperid) {
		String scrapperkey = "";
		List<List> result = ortus.api.executeSQLQueryArray("select foundkey from sage.scrapperlog where scrapperid = " + scrapperid);
		if (result.size() == 1) {
			scrapperkey = (String) result.get(0).get(0);
			if (scrapperkey.contains("Not Found") || scrapperkey.contains("None Found")) {
				return null;
			} else {
				return scrapperkey;
			}
		} else {
			return null;
		}
	}

	public static boolean ManualMovieMatch(Object mediafile, Object scrapperid, Object download_fanart) {
		boolean dwn_fanart = false;
		if (download_fanart instanceof String) {
			dwn_fanart = Boolean.parseBoolean((String) download_fanart);
		}
		if (download_fanart.getClass().getName().equalsIgnoreCase("java.lang.Boolean")) {
			dwn_fanart = (Boolean) download_fanart;
		}

		ortus.api.DebugLog(LogLevel.Trace, "ManualMovieMatch: " + scrapperid + " mediaid: " + MediaFileAPI.GetMediaFileID(mediafile) + " Fanart: " + dwn_fanart);
		String scrapperkey = "";
		List<List> result = ortus.api.executeSQLQueryArray("select foundkey from sage.scrapperlog where scrapperid = " + scrapperid);
		if (result.size() == 1) {
			scrapperkey = (String) result.get(0).get(0);
		} else {
			return false;
		}

		result = ortus.api.executeSQLQueryArray("select mediagroup from allmedia where mediaid = " + MediaFileAPI.GetMediaFileID(mediafile));
		Movie movie = new TheMovieDB().GetDetail(scrapperkey);
		movie.setMediatype(2);
		movie.setMediagroup(Integer.parseInt((String) result.get(0).get(0)));
		movie.setMetadatafound(true);
                movie.setMediaid(MediaFileAPI.GetMediaFileID(mediafile));
		movie.WriteDB();
		if (dwn_fanart) {
			movie.DownloadImages(mediafile, "Movies" + java.io.File.separator + ortus.util.scrubString.ScrubFileName(movie.getName()));
		}
		MediaObject mo = new MediaObject(mediafile);
		mo.setMovie(movie);
		String[] skp = scrapperkey.split(":");
		if (skp[0].equalsIgnoreCase("imdb")) {
			mo.setImdbid(skp[1]);
		} else {
			mo.setTmdbid(skp[1]);
		}
		mo.WriteProperty();
		ortus.cache.cacheEngine.getInstance().ReLoadCache("MD"+ mediafile);

		return true;
	}

	public static boolean MovieSearch(MediaObject mo, List<String> mediaregex) {

		ortus.api.DebugLog(LogLevel.Trace, "MovieSearch: ShowTitle: " + mo.getShowtitle());
		TheMovieDB tmdb = new TheMovieDB();
		boolean movie_found = false;

		if (mo.getTmdbid() == null && mo.getImdbid() == null) {
			tmdb.SetSearchLimit(5);

			HashMap<String, SearchResult> movies = tmdb.Search(mo.getShowtitle());

			database.LogFind(mo.getScantype(), MediaFileAPI.GetMediaFileID(mo.getMedia()), mo.getShowtitle(), movies);

			Object[] mk = movies.keySet().toArray();
			for (Object mt : mk) {
				if (CompareTitle(ortus.onlinescrapper.tools.parser.CleanName((String) mt), ortus.onlinescrapper.tools.parser.CleanName(mo.getShowtitle()), mediaregex)) {
//			ortus.api.DebugLog(LogLevel.Trace,"Title Match");
//			ortus.api.DebugLog(LogLevel.Trace,mo.toString());
					String[] keytype = movies.get(mt).getMetadatakey().split(":");
//			ortus.api.DebugLog(LogLevel.Trace,"keytype: " + keytype[0] + " keyvalue: " + keytype[1]);
					if (keytype[0].equalsIgnoreCase("imdb")) {
						if (mo.getImdbid() == null) {
//					ortus.api.DebugLog(LogLevel.Trace,"imdb key found: " + keytype[1]);
							mo.setImdbid(keytype[1]);
						}
					}
					if (keytype[0].equalsIgnoreCase("themoviedb")) {
						if (mo.getTmdbid() == null) {
//					ortus.api.DebugLog(LogLevel.Trace,"tmdb key found: " + keytype[1]);
							mo.setTmdbid(keytype[1]);
						}
					}
				}
			}
		}

		if (mo.getTmdbid() != null || mo.getImdbid() != null) {
			Movie movie;
			if (mo.getTmdbid() != null) {
				movie = tmdb.GetDetail("themoviedb:" + mo.getTmdbid());
				mo.setMetadatasource("TheMovieDB");
			} else {
				movie = tmdb.GetDetail("imdb:" + mo.getImdbid());
				mo.setMetadatasource("IMDB");
			}
                        
                        if ( movie.isMetadatafound()) {
                            movie_found = true;
                            mo.setMediatype(MediaType.Movie);
                            mo.setMetadatafound(true);
                            mo.setMovie(movie);

                            if (mo.isMediaTypeMovie()) {
                                    mo.WriteDB();
                                    movie.setMediaid(MediaFileAPI.GetMediaFileID(mo.getMedia()));
                                    movie.WriteDB();
                            }
                            if (mo.getFanart() > 0) {
                                    mo.DownloadImages("Movies" + java.io.File.separator + ortus.util.scrubString.ScrubFileName(mo.getShowtitle()));
                                    mo.DownloadCastImages("Cast");
                            }
                        }
		}
//    } else {
//        Movie movie = new Movie();
//	movie.SetMediaType(mo.GetMediaType());
//        movie.SetMediaGroup(mo.GetMediaGroup());
//        movie.WriteDB(mo.GetMedia());
//    }

		return movie_found;
	}

	public static void GetMissingFanart() {
		index_running = true;
		local_index_running = true;
		st = ScanType.MissingFanart;
		cancel_scan = false;
		scan_start_time = new Date().getTime();
		scan_end_time = 0;
		total_movies_match = 0;
		total_bypass = 0;
		total_processed = 0;
		total_match_property = 0;
		total_miss = 0;
		total_series_match = 0;

		ortus.api.DebugLog(LogLevel.Trace, "Fanart: Downloadming missing fanart");
		List<List> result = ortus.api.executeSQLQueryArray("select f.metadataid, f.type, f.url, m.name from sage.fanart as f, sage.movies as m where m.metadataid = f.metadataid and file is null union select f.metadataid, f.type, f.url, s.title from sage.fanart as f, sage.series as s where concat('SR',s.seriesid) = f.metadataid and file is null");

		total_queue = result.size();
		ortus.api.DebugLog(LogLevel.Trace, "Fanart: found " + result.size() + " missing fanart records");
		int fanart_limit = Integer.parseInt(ortus.api.GetSageProperty("ortus/fanart/download_limit", "4"));
		int media_fanart_count = 0;
		String last_mediaid = "";
		for (List fanart : result) {
			total_processed++;
			if (last_mediaid.isEmpty()) {
				last_mediaid = (String) fanart.get(0);
			}

			if (last_mediaid.equalsIgnoreCase((String) fanart.get(0))) {
				media_fanart_count++;
			} else {
				media_fanart_count = 1;
			}
			if (media_fanart_count <= fanart_limit) {
				total_movies_match++;
				String medtype = "Movies";
				if (((String)fanart.get(0)).startsWith("SR")) {
					medtype = "TV";
				}
				if (cancel_scan) {
					break;
				}
				ImageItem ii = new ImageItem((String)fanart.get(0),(String) fanart.get(1), "0", (String) fanart.get(2));
				ii.getImage(medtype + java.io.File.separator + ortus.util.scrubString.ScrubFileName((String) fanart.get(3)));
			} else {
				total_bypass++;
			}
			last_mediaid = (String) fanart.get(0);
		}

                ortus.api.DebugLogTrace("Fanart: Downloading missing fanart completed");
		scan_end_time = new Date().getTime();
		st = ScanType.None;
		index_running = false;
		local_index_running = false;
	}

//	public static void GetScanFanart() {
//		index_running = true;
//		st = ScanType.FanartScan;
//		cancel_scan = false;
//		scan_start_time = new Date().getTime();
//		scan_end_time = 0;
//		total_movies_match = 0;
//		total_bypass = 0;
//		total_processed = 0;
//		total_match_property = 0;
//		total_miss = 0;
//		total_series_match = 0;
//
//		ortus.api.DebugLog(LogLevel.Info, "GetScanFanart: Starting");
//		int total_backgrounds = 0;
//		int total_posters = 0;
//		int total_banners = 0;
//		int total_season_poster = 0;
//		int total_season_banner = 0;
//		int total_cast = 0;
//		List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles("TVDB")));
//
//		total_queue = mfl.size();
//
//		for (Object o : mfl) {
//			total_processed++;
//			String Title = ortus.util.scrubString.ScrubFileName(ortus.api.GetMediaTitle(o));
//			ortus.api.DebugLog(LogLevel.Trace, "Scanning for fanart for: " + Title);
//			File fanartBackgrounds = new File(ortus.api.GetFanartFolder() + java.io.File.separator + "Movies" + java.io.File.separator + Title + java.io.File.separator + "Backgrounds");
//			if (fanartBackgrounds.isDirectory()) {
//				for (File fafile : fanartBackgrounds.listFiles()) {
//					String fafilename = fafile.getAbsolutePath();
//					if (fafilename.endsWith("jpg")) {
//						total_backgrounds++;
//						String faworkname = fafilename.substring(ortus.api.GetFanartFolder().length() + 1);
//						ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Adding Background: " + faworkname);
//						ImageItem ii = new ImageItem("Backgrounds", "0", faworkname);
//						ii.WriteFileImageDB(o);
//					}
//
//				}
//			}
//			fanartBackgrounds = new File(ortus.api.GetFanartFolder() + java.io.File.separator + "TV" + java.io.File.separator + Title + java.io.File.separator + "Backgrounds");
//			if (fanartBackgrounds.isDirectory()) {
//				for (File fafile : fanartBackgrounds.listFiles()) {
//					String fafilename = fafile.getAbsolutePath();
//					if (fafilename.endsWith("jpg") || fafilename.endsWith("png")) {
//						total_backgrounds++;
//						String faworkname = fafilename.substring(ortus.api.GetFanartFolder().length() + 1);
//						ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Adding Background: " + faworkname);
//						ImageItem ii = new ImageItem("Backgrounds", "0", faworkname);
//						ii.WriteFileImageDB(o);
//					}
//
//				}
//			}
//
//			File fanartPosters = new File(ortus.api.GetFanartFolder() + java.io.File.separator + "Movies" + java.io.File.separator + Title + java.io.File.separator + "Posters");
//			if (fanartPosters.isDirectory()) {
//				for (File fafile : fanartPosters.listFiles()) {
//					String fafilename = fafile.getAbsolutePath();
//					if (fafilename.endsWith("jpg") || fafilename.endsWith("png")) {
//						total_posters++;
//						String faworkname = fafilename.substring(ortus.api.GetFanartFolder().length() + 1);
//						ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Adding Poster: " + faworkname);
//						ImageItem ii = new ImageItem("Posters", "0", faworkname);
//						ii.WriteFileImageDB(o);
//					}
//				}
//			}
//			fanartPosters = new File(ortus.api.GetFanartFolder() + java.io.File.separator + "TV" + java.io.File.separator + Title + java.io.File.separator + "Posters");
//			if (fanartPosters.isDirectory()) {
//				for (File fafile : fanartPosters.listFiles()) {
//					String fafilename = fafile.getAbsolutePath();
//					if (fafilename.endsWith("jpg") || fafilename.endsWith("png")) {
//						total_posters++;
//						String faworkname = fafilename.substring(ortus.api.GetFanartFolder().length() + 1);
//						ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Adding Poster: " + faworkname);
//						ImageItem ii = new ImageItem("Posters", "0", faworkname);
//						ii.WriteFileImageDB(o);
//					}
//				}
//			}
//			File fanartBanners = new File(ortus.api.GetFanartFolder() + java.io.File.separator + "TV" + java.io.File.separator + Title + java.io.File.separator + "Banners");
//			if (fanartBanners.isDirectory()) {
//				for (File fafile : fanartBanners.listFiles()) {
//					String fafilename = fafile.getAbsolutePath();
//					if (fafilename.endsWith("jpg") || fafilename.endsWith("png")) {
//						total_banners++;
//						String faworkname = fafilename.substring(ortus.api.GetFanartFolder().length() + 1);
//						ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Adding Banner: " + faworkname);
//						ImageItem ii = new ImageItem("Banners", "0", faworkname);
//						ii.WriteFileImageDB(o);
//					}
//				}
//			}
//
//			File fanartSeason = new File(ortus.api.GetFanartFolder() + java.io.File.separator + "TV" + java.io.File.separator + Title);
//			if (fanartSeason.isDirectory()) {
//				for (File sfile : fanartSeason.listFiles()) {
//					if (sfile.isDirectory() && sfile.getName().startsWith("Season")) {
//						Pattern pattern = Pattern.compile("Season.*(\\d)");
//						Matcher matcher = pattern.matcher(sfile.getName());
//						String seasonno = "0";
//						if (matcher.find()) {
//							seasonno = matcher.group(1);
//						}
//						fanartPosters = new File(sfile.getAbsolutePath() + java.io.File.separator + "Posters");
//						if (fanartPosters.isDirectory()) {
//							for (File fafile : fanartPosters.listFiles()) {
//								String fafilename = fafile.getAbsolutePath();
//								if (fafilename.endsWith("jpg") || fafilename.endsWith("png")) {
//									total_season_poster++;
//									String faworkname = fafilename.substring(ortus.api.GetFanartFolder().length() + 1);
//									ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Adding Poster: " + faworkname);
//									ImageItem ii = new ImageItem("Season-" + seasonno + "-Posters", "0", faworkname);
//									ii.WriteFileImageDB(o);
//								}
//							}
//						}
//						fanartBanners = new File(sfile.getAbsolutePath() + java.io.File.separator + "Banners");
//						if (fanartBanners.isDirectory()) {
//							for (File fafile : fanartBanners.listFiles()) {
//								String fafilename = fafile.getAbsolutePath();
//								if (fafilename.endsWith("jpg") || fafilename.endsWith("png")) {
//									total_season_banner++;
//									String faworkname = fafilename.substring(ortus.api.GetFanartFolder().length() + 1);
//									ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Adding Banner: " + faworkname);
//									ImageItem ii = new ImageItem("Season-" + seasonno + "-Banners", "0", faworkname);
//									ii.WriteFileImageDB(o);
//								}
//							}
//						}
//					}
//				}
//			}
//                        ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { MediaFileAPI.GetMediaFileID(o) } );
////			ortus.cache.cacheEngine.getInstance().ReLoadCache(o);
//                        ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Clients,"ScanCountUpdate", new Object[] { GetScrapperDetail() } );
//		}
//
////	File castPosters = new File(ortus.api.GetFanartFolder() + java.io.File.separator + "Cast" );
////	for ( File castdir : castPosters.listFiles()) {
////		if (castdir.isDirectory()) {
////			for (File castimage : castdir.listFiles()) {
////				String cafilename = castimage.getAbsolutePath();
////				if (cafilename.endsWith("jpg") || cafilename.endsWith("png")) {
////					total_cast++;
////					String caworkname = cafilename.substring(ortus.api.GetFanartFolder().length() + 1);
////					ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Adding Cast: " + caworkname);
////					ImageItem ii = new ImageItem("Cast-"+castdir.getName(), "0", caworkname);
////					ii.WriteFileImageDB();
////				}
////			}
////		}
////	}
//		ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Processed " + total_backgrounds + " Backgrounds");
//		ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Processed " + total_posters + " Posters");
//		ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Processed " + total_banners + " Banners");
//		ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Processed " + total_season_poster + " Season Posters");
//		ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Processed " + total_season_banner + " Season Banners");
//		ortus.api.DebugLog(LogLevel.Trace, "GetScanFanart: Completed");
//		st = ScanType.None;
//		index_running = false;
//	}
//public static IMDBItem IMDBMovieDetail(String imdbid) {
//    ortus.api.DebugLog(LogLevel.Trace,"IMDBProvider: IMDBMovieDetail: " + imdbid);
//    IMetaDataProvider dp = new ortus.onlinescrapper.IMDBProvider();

//    IMDBItem movie = dp.GetItemDetail(imdbid);
///    movie.WriteDB(mo);
//    return dummy;
//}
	public static void indexPictures() {
		int totaladded = 0;
		ortus.api.DebugLog(LogLevel.Info, "indexPictures: Starting");
		List<Object> mfl = new ArrayList<Object>(Arrays.asList(MediaFileAPI.GetMediaFiles("P")));

		for (Object o : mfl) {
			List<Object> result = ortus.api.executeSQLQuery("select mediaid from sage.media where mediaid = " + MediaFileAPI.GetMediaFileID(o));
			if (result.size() > 0) {
				continue;
			}
			String SQL = "insert into sage.media ";



		}

		ortus.api.DebugLog(LogLevel.Info, "indexPictures: Completed");

		return;
	}

	private static boolean CompareTitle(String TMDBtitle, String title, List<String> mediaregex) {
		boolean titlematch = false;

		String cleanTMDBtitle = ortus.util.string.CleanStringExtreme(TMDBtitle);
		String cleantitle = ortus.util.string.CleanStringExtreme(title);

		ortus.api.DebugLog(LogLevel.Trace, "title: <" + title + ">   cleantitle: <" + cleantitle + ">   Provider title: <" + TMDBtitle + ">   clean Provider title: <" + cleanTMDBtitle + ">");
		if (cleanTMDBtitle.equals(cleantitle)) {
			return true;
		} else {
			int matchfound = 0;
			Pattern pattern;
			Matcher matcher;
			for (String mr : mediaregex) {
				String[] reg = mr.split(":");
				pattern = Pattern.compile(reg[0]);
				matcher = pattern.matcher(cleantitle);
				StringBuffer sb3 = new StringBuffer(cleantitle.length());
				while (matcher.find()) {
					matchfound++;
					matcher.appendReplacement(sb3, Matcher.quoteReplacement(reg[1]));
				}

				matcher.appendTail(sb3);
				String newcleantitle = sb3.toString();
				if (matchfound > 0) {
					ortus.api.DebugLog(LogLevel.Trace, "title: <" + title + ">   cleantitle: <" + cleantitle + ">   Provider title: <" + TMDBtitle + ">   clean Provider title: <" + cleanTMDBtitle + ">");

					if (cleanTMDBtitle.equals(newcleantitle)) {
						return true;
					}
				}
				matchfound = 0;
			}

			pattern = Pattern.compile("(\\d+)");
			matcher = pattern.matcher(TMDBtitle);
			StringBuffer sb = new StringBuffer(TMDBtitle.length());

			while (matcher.find()) {
				matcher.appendReplacement(sb, Matcher.quoteReplacement(binaryToRoman(Integer.parseInt(matcher.group(0).trim()))));
			}

			matcher.appendTail(sb);
			cleanTMDBtitle = ortus.util.string.CleanStringExtreme(sb.toString());

			StringBuffer sb2 = new StringBuffer(title.length());
			matcher = pattern.matcher(title);
			while (matcher.find()) {
				matcher.appendReplacement(sb2, Matcher.quoteReplacement(binaryToRoman(Integer.parseInt(matcher.group(0).trim()))));
			}
			matcher.appendTail(sb2);
			cleantitle = ortus.util.string.CleanStringExtreme(sb2.toString());
			ortus.api.DebugLog(LogLevel.Trace, "title: <" + title + ">   cleantitle: <" + cleantitle + ">   Provider title: <" + TMDBtitle + ">   clean Provider title: <" + cleanTMDBtitle + ">");

			if (cleanTMDBtitle.equals(cleantitle)) {
				return true;
			}
		}
		return false;
	}

	public static String binaryToRoman(int binary) {

		String[] RCODE = {"m", "cm", "d", "cd", "c", "xc", "l", "xl", "x", "ix", "v", "iv", "i"};
		int[] BVAL = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

		if (binary <= 0 || binary >= 4000) {
			return "";
		}
		String roman = "";

		for (int i = 0; i < RCODE.length; i++) {
			while (binary >= BVAL[i]) {
				binary -= BVAL[i];
				roman += RCODE[i];
			}
		}
		return roman;
	}

	public static String CleanName(String title) {
		String s1 = title.toLowerCase();
		s1 = s1.replaceAll("\\.", " ");
		s1 = s1.replaceAll("_", " ");
		s1 = s1.replaceAll("-", " ");

		return s1;
	}
}

class DirectoryFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}
}
