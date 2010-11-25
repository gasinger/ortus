package ortus;


import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import ortus.daemon.OrtusTask;
import ortus.media.OrtusMedia;
import ortus.media.fanart.IFanartProvider;
import ortus.media.fanart.fanartEngine;
import ortus.media.metadata.IMetadataProvider;

import sagex.UIContext;
import sagex.api.MediaFileAPI;

import ortus.ui.menu.menuEngine;
import ortus.media.metadata.metadataEngine;
import ortus.property.IProperty;
import ortus.property.OrtusDBProperty;
import sagex.api.Configuration;
import sagex.api.Global;

/**
 *
 * @author TeamORTUS
 */
public class api extends vars {

	public static String GetVersion() {
		return "1.0.15.000";
	}

	public static String GetVersionFull() {
		return "1.0.15.000;09/02/2010;jphipps";
	}

        public static String GetOrtusBasePath() {
            return configurationEngine.getInstance().getBasePath();
        }

	/**
	 *
	 * @return
	 */
	public static boolean IsOrtusInitialized() {
		return Ortus.getInstance().IsOrtusInitialized();
	}

/**
 * Setup Ortus Environment
 * @param context Context from the STV
 * @param activatemodules Modules to activate.  Currently:
 *	DB - H2 Database Engine
 *	Theme - Ortus Theme Engine
 *	Menu - Ortus Menu Engine
 *	FavoriteExcluder - Favorite Excluder Engine
 */
	/**
	 *
	 * @return
	 */
	public static String GetUIContextName() {
		return ortus.util.UIC.GetUIContextName();
	}

	/**
	 *
	 * @return
	 */
	public static UIContext GetUIContext() {
		return ortus.util.UIC.GetUIContext();
	}

	/**
	 *
	 * @param UIC_Str
	 */
	public static void StoreUIContextName(String UIC_Str) {
		ortus.util.UIC.StoreUIContextName(UIC_Str);
		return;
	}

	public static Object GetMediaObject(Object o1, Object o2) {
		return ortus.util.ui.GetMediaObject(o1, o2);
	}

	public static boolean IsClient() {
		return ortus.util.ui.IsClient();
	}

	public static boolean IsExtender() {
		return ortus.util.ui.IsExtender();
	}

	/**
	 *
	 * @return
	 */
	public static String GetMACAddress() {
		return configurationEngine.getInstance().getMACAddress();
	}

// Debug Logging
	/*
	 * ortus api DebugLog.  Prints message to the sage debug log.
	 * debug_logging=TRUE
	 * configure with .properties item ortus/debug_log_level
	 * All messages with a MsgLevel less then or equal to debug_log_level are output
	 *
	 * ALL = 7
	 * LogLevel.Trace = 6
	 * DEBUG = 5
	 * LogLevel.Info = 4
	 * WARNING = 3
	 * LogLevel.Error = 2
	 * FATAL = 1
	 * OFF = 0
	 *
	 * @param MsgLevel Message Level String
	 * @param MsgString Message String
	 *
	 */
	public static void SetLoggerLog4jAll() {
		ortus.logger.getInstance().SetLog4jAll();
	}
	public static void SetLoggerLog4jTrace() {
		ortus.logger.getInstance().SetLog4jTrace();
	}
	public static void SetLoggerLog4jDebug() {
		ortus.logger.getInstance().SetLog4jDebug();
	}
	public static void SetLoggerLog4jInfo() {
		ortus.logger.getInstance().SetLog4jInfo();
	}
	public static void SetLoggerLog4jWarn() {
		ortus.logger.getInstance().SetLog4jWarn();
	}
	public static void SetLoggerLog4jError() {
		ortus.logger.getInstance().SetLog4jError();
	}
	public static void SetLoggerLog4jFatal() {
		ortus.logger.getInstance().SetLog4jFatal();
	}
	public static void SetLoggerLog4jOff() {
		ortus.logger.getInstance().SetLog4jOff();
	}
	/**
	 *
	 * @param MsgLevel
	 * @param MsgString
	 */
	public static void DebugLog(Object MsgLevel, Object MsgString) {
		ortus.logger.getInstance().DebugLog(MsgLevel, MsgString);
	}
	public static void DebugLog(Object MsgLevel, Object MsgString, Throwable stacktrace) {
		ortus.logger.getInstance().DebugLog(MsgLevel, MsgString, stacktrace);
	}

        public static void DebugLogFatal(Object... Msg) {
            ortus.logger.getInstance().DebugLogFatal(Msg);
        }
        public static void DebugLogError(Object... Msg) {
            ortus.logger.getInstance().DebugLogError(Msg);
        }
        public static void DebugLogWarning(Object... Msg) {
            ortus.logger.getInstance().DebugLogWarning(Msg);
        }
        public static void DebugLogInfo(Object... Msg) {
            ortus.logger.getInstance().DebugLogInfo(Msg);
        }
        public static void DebugLogDebug(Object... Msg) {
            ortus.logger.getInstance().DebugLogDebug(Msg);
        }
        public static void DebugLogTrace(Object... Msg) {
            ortus.logger.getInstance().DebugLogTrace(Msg);
        }
	/**
	 *
	 * @param MsgLevel
	 * @param MsgString
	 */
	@SuppressWarnings("static-access")
	public static void DebugLogDB(Object MsgLevel, String MsgString) {
		ortus.logger.getInstance().DebugLogDB(MsgLevel, MsgString);
	}
	/**
	 *
	 * @param MsgLevel
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static List<Object> GetDebugLogDB(int MsgLevel) {
		return ortus.logger.getInstance().GetDebugLogDB(MsgLevel);
	}

	public static boolean AddDownloadQueue(String options, String downloadKey, String url, String dirname) {
		return Ortus.getInstance().getDownloadServer().AddDownloadQueue(options, downloadKey, url, dirname, null);
	}

	public static boolean AddDownloadQueue(String options, String downloadKey, String url, String dirname, String filename) {
		return Ortus.getInstance().getDownloadServer().AddDownloadQueue(options, downloadKey, url, dirname, filename);
	}

	public static int ReleaseScheduled() {
		return Ortus.getInstance().getDownloadServer().ReleaseScheduled();
	}
	
	public static void CancelDownload(String downloadKey) {
		Ortus.getInstance().getDownloadServer().CancelDownload(downloadKey);
	}

        public static void CancelAllDownloads() {
                Ortus.getInstance().getDownloadServer().CancelAlldownloads();
        }
	
	public static void CleanDownload(String downloadKey) {
		Ortus.getInstance().getDownloadServer().CleanDownload(downloadKey);
	}

	public static String GetDownloadStatus(String downloadKey) {
		return Ortus.getInstance().getDownloadServer().getStatus(downloadKey);
	}

	public static int GetDownloadPctComplete(String downloadKey) {
		return Ortus.getInstance().getDownloadServer().getPctComplete(downloadKey);
	}

	public static long GetDownloadTime(String downloadKey) {
		return Ortus.getInstance().getDownloadServer().getTime(downloadKey);
	}
	public static long GetDownloadEstimatedTime(String downloadKey) {
		return Ortus.getInstance().getDownloadServer().getEstimatedTotalTime(downloadKey);
	}
	public static long GetDownloadEstimatedTimeLeft(String downloadKey) {
		return Ortus.getInstance().getDownloadServer().getEstimatedTimeLeft(downloadKey);
	}	

	public static Object[] GetDownloads() {
		return Ortus.getInstance().getDownloadServer().GetDownloads();
	}

        public static Map GetDownloadDetail(String downloadKey) {
                return Ortus.getInstance().getDownloadServer().GetDownloadDetail(downloadKey);
        }

	public static String GetDownloadPoolStatus() {
		return Ortus.getInstance().getDownloadServer().GetDownloadPoolStatus();
	}
	public static boolean AddServerDownloadQueue(String options, String downloadKey, String url, String dirname) {
		return (Boolean)ortus.process.executeRemote("ortus.api.AddDownloadQueue", new Object[] { options, downloadKey, url, dirname });
	}

	public static boolean AddServerDownloadQueue(String options, String downloadKey, String url, String dirname, String filename) {
		return (Boolean)ortus.process.executeRemote("ortus.api.AddDownloadQueue", new Object[] { options, downloadKey, url, dirname, filename });
	}

	public static int ReleaseServerScheduled() {
		return (Integer)ortus.process.executeRemote("ortus.api.ReleaseScheduled", null);
	}

	public static void CancelServerDownload(String downloadKey) {
		ortus.process.executeRemote("ortus.api.CancelDownload", new Object[] { downloadKey});
	}

	public static void CleanServerDownload(String downloadKey) {
		ortus.process.executeRemote("ortus.api.CleanDownload", new Object[] {downloadKey});
	}

	public static String GetServerDownloadStatus(String downloadKey) {
		return (String)ortus.process.executeRemote("ortus.api.GetDownloadStatus", new Object[] { downloadKey});
	}

	public static int GetServerDownloadPctComplete(String downloadKey) {
		return (Integer)ortus.process.executeRemote("ortus.api.GetDownloadPctComplete", new Object[] { downloadKey} );
	}

	public static long GetServerDownloadTime(String downloadKey) {
		return (Long)ortus.process.executeRemote("ortus.api.GetDownloadTime", new Object[] { downloadKey });
	}
	public static long GetServerDownloadEstimatedTime(String downloadKey) {
		return (Long)ortus.process.executeRemote("ortus.api.GetDownloadEstimatedTime", new Object[] { downloadKey } );
	}
	public static long GetServerDownloadEstimatedTimeLeft(String downloadKey) {
		return (Long)ortus.process.executeRemote("ortus.api.GetDownloadEstimatedTimeLeft", new Object[] { downloadKey} );
	}

	public static Object[] GetServerDownloads() {
		return (Object[])ortus.process.executeRemote("ortus.api.GetDownloads", null);
	}

        public static Map GetServerDownloadDetail(String downloadKey) {
                return (Map)ortus.process.executeRemote("ortus.api.GetDownloadDetail", new Object[] { downloadKey } );
        }

	public static String GetServerDownloadPoolStatus() {
		return (String)ortus.process.executeRemote("ortus.api.GetDownloadPoolStatus",null);
	}

	public static void ClearCache() {
		ortus.cache.cacheEngine.getInstance().ClearCache();
	}
	
	public static void ReLoadCache() {
		ortus.cache.cacheEngine.getInstance().ReLoadCache();
	}

	public static void DownloadTester() {
		ortus.api.DebugLog(LogLevel.Trace, "DownloadTester: starting");

		ortus.api.AddDownloadQueue("autoclean","key1", "http://www.projectortus.com/plugins/jphipps/ortus/ortus_screenshot.jpg", "c:\\jeff");
		ortus.api.AddDownloadQueue("autoclean","key2", "http://www.projectortus.com/plugins/jphipps/ortus/ortus_screenshot.jpg", "c:\\jeff","newfilename.jpg");
		ortus.api.AddDownloadQueue("autoclean;overwrite","key3", "http://www.projectortus.com/plugins/jphipps/ortus/ortus_screenshot.jpg", "c:\\jeff");

		ortus.api.DebugLog(LogLevel.Trace, "DwonloadTester: Pool Sttus: " + GetDownloadPoolStatus());
		Object[] x = GetDownloads();
		for ( Object y : x) {
			ortus.api.DebugLog(LogLevel.Trace, "DwonloadTester: download: " + y);
		}

		try { 	Thread.sleep(20000); } catch(Exception e) {}

		ortus.api.DebugLog(LogLevel.Trace, "DwonloadTester: Pool Sttus: " + GetDownloadPoolStatus());
		x = GetDownloads();
		for ( Object y : x) {
			ortus.api.DebugLog(LogLevel.Trace, "DwonloadTester: download: " + y);
		}

		ortus.api.DebugLog(LogLevel.Trace, "DownloadTester: completed");
	}
// Custom Filters
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static boolean IsGeneralTV(Object MediaObject) {
            return ortus.media.CustomFilters.IsGeneralTV(MediaObject);
        }
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetGenre(Object MediaObject) {
            return ortus.media.CustomFilters.GetGenre(MediaObject);
        }
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetActor(Object MediaObject) {
            return ortus.media.CustomFilters.GetActor(MediaObject);
        }
//        public static String GetDescription(Object MediaObject) {
//            return CustomFilters.GetDescription(MediaObject);
//        }
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetWriter(Object MediaObject) {
            return ortus.media.CustomFilters.GetWriter(MediaObject);
        }
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetDirector(Object MediaObject) {
            return ortus.media.CustomFilters.GetDirector(MediaObject);
        }

//	Ortus Favorite Excluder interface
	/**
	 * Verify a valid Favorite Excluder File
	 *
	 * @return
	 */
	public static boolean ValidExcludeFile() {
		return Ortus.getInstance().getFavoriteExcluder().ValidExcluderFile();
	}
	/**
	 * Run Favorite Excluder Scan
	 */
	public static void RunExcluder() {
		Ortus.getInstance().getFavoriteExcluder().RunExcluder();
		return;
	}
	
	/**
	 *
	 * @return
	 */
	public static String GetClientName() {
            return configurationEngine.getInstance().getIdentity().GetClientName();
        }

	/**
	 *
	 * @param clientname
	 */
	public static void SetClientName(String clientname) {
		configurationEngine.getInstance().getIdentity().SetClientName(clientname);
		return;
	}

	public static int GetCurrentUser() {
		return configurationEngine.getInstance().getIdentity().GetCurrentUser();
	}

	public static Object GetCurrentUserName() {
		return configurationEngine.getInstance().getIdentity().GetCurrentUserName();
	}

	public static Object GetUserName(Object userid) {
		return configurationEngine.getInstance().getIdentity().GetUserName(userid);
	}

	public static void SetUserName(Object userid, Object username) {
		configurationEngine.getInstance().getIdentity().SetUserName(userid, username);
	}
	
	public static Object GetUserThumb(Object userid) {
		return configurationEngine.getInstance().getIdentity().GetUserThumb(userid);
	}

	public static void SetUserThumb(Object userid, Object thumb) {
		configurationEngine.getInstance().getIdentity().SetUserThumb(userid, thumb);
	}
	public static void SetCurrentUser(Object userid) {
		configurationEngine.getInstance().getIdentity().SetCurrentUser(userid);
	}

	public static Object GetUserProperty(String prop, String defval) {
		return ((IProperty)configurationEngine.getInstance().getIdentity().GetUserProperty()).GetProperty(prop, defval);
	}

	public static void SetUserProperty(String prop, Object propval) {
		((IProperty)configurationEngine.getInstance().getIdentity().GetUserProperty()).SetProperty(prop, String.valueOf(propval));
	}

	public static void RemoveUserProperty(String prop) {
		configurationEngine.getInstance().getIdentity().GetUserProperty().RemoveProperty(prop);
	}

	public static void AddUser(String userid) {
		configurationEngine.getInstance().getIdentity().AddUser(userid);
	}

	public static void RemoveUser(Object userid) {
		configurationEngine.getInstance().getIdentity().RemoveUser(userid);
	}
	public static void SetUserPin(Object userid, String userpin) {
		configurationEngine.getInstance().getIdentity().SetUserPin(userid, userpin);
	}
	public static void ClearUserPin(Object userid) {
		configurationEngine.getInstance().getIdentity().ClearUserPin(userid);
	}
	public static String GetUserPin(Object userid) {
		return configurationEngine.getInstance().getIdentity().GetUserPin(userid);
	}
	
	public static List<Object> GetUsers() {
		return configurationEngine.getInstance().getIdentity().GetUsers();
	}

	public static void SetUserWatched(Object mediafile) {
		configurationEngine.getInstance().getIdentity().SetUserWatched(mediafile);
	}
	public static void SetUserWatched(Object mediafile, Object userid) {
		configurationEngine.getInstance().getIdentity().SetUserWatched(mediafile,userid);
	}

	public static void ClearUserWatched(Object mediafile) {
		configurationEngine.getInstance().getIdentity().ClearUserWatched(mediafile);
	}
	public static void ClearUserWatched(Object mediafile, Object userid) {
		configurationEngine.getInstance().getIdentity().ClearUserWatched(mediafile, userid);
	}

	public static boolean IsUserWatched(Object mediafile) {
		return configurationEngine.getInstance().getIdentity().IsUserWatched(mediafile);
	}
	public static boolean IsUserWatched(Object mediafile, Object userid) {
		return configurationEngine.getInstance().getIdentity().IsUserWatched(mediafile, userid);
	}

	public static void SetUserWatchPosition(Object mediafile, long wtime) {
		configurationEngine.getInstance().getIdentity().SetUserWatchPosition(mediafile, wtime);
	}

	public static void ClearUserWatchPosition(Object mediafile) {
		configurationEngine.getInstance().getIdentity().ClearUserWatchPosition(mediafile);
	}
	public static void ClearUserWatchPosition(Object mediafile, Object userid) {
		configurationEngine.getInstance().getIdentity().ClearUserWatchPosition(mediafile, userid);
	}
	public static long GetUserWatchPosition(Object mediafile) {
		return configurationEngine.getInstance().getIdentity().GetUserWatchPosition(mediafile);
	}
	public static long GetUserWatchPosition(Object mediafile, Object userid) {
		return configurationEngine.getInstance().getIdentity().GetUserWatchPosition(mediafile, userid);
	}
	public static long GetUserWatchTime(Object mediafile) {
		return configurationEngine.getInstance().getIdentity().GetUserWatchTime(mediafile);
	}
	public static long GetUserWatchTime(Object mediafile, Object userid) {
		return configurationEngine.getInstance().getIdentity().GetUserWatchTime(mediafile, userid);
	}
	public static int GetUserWatchTitle(Object mediafile) {
		return configurationEngine.getInstance().getIdentity().GetUserWatchTitle(mediafile);
	}
	public static int GetUserWatchTitle(Object mediafile, Object userid) {
		return configurationEngine.getInstance().getIdentity().GetUserWatchTitle(mediafile, userid);
	}
	public static int GetUserWatchChapter(Object mediafile) {
		return configurationEngine.getInstance().getIdentity().GetUserWatchChapter(mediafile);
	}
	public static int GetUserWatchChapter(Object mediafile, Object userid) {
		return configurationEngine.getInstance().getIdentity().GetUserWatchChapter(mediafile, userid);
	}
	public static void SetUserFavorite(Object favid) {
		configurationEngine.getInstance().getIdentity().SetUserFavorite(favid);
	}
	public static void SetUserFavorite(Object favid, Object userid) {
		configurationEngine.getInstance().getIdentity().SetUserFavorite(favid, userid);
	}
	public static int GetUserFavorite(Object favid) {
		return configurationEngine.getInstance().getIdentity().GetUserFavorite(favid);
	}
	public static Object[] GetUserFavorites(Object userid) {
		return configurationEngine.getInstance().getIdentity().GetUserFavorites(userid);
	}

	public static void SetFavoriteExcluder(String favrule) {
		Ortus.getInstance().getFavoriteExcluder().AddFavoriteExcluder(favrule);
	}

	public static void RemoveFavoriteExcluder(String favrule) {
		Ortus.getInstance().getFavoriteExcluder().RemoveFavoriteExcluder(favrule);
	}

	public static List GetFavoriteExcluder() {
		return Ortus.getInstance().getFavoriteExcluder().GetFavoriteExcluder();
	}
	
	public static void RunFavoriteExcluder() {
		Ortus.getInstance().getFavoriteExcluder().RunExcluder();
	}
	/**
	 *
	 * @param PropName
	 * @return
	 */
	public static String GetProperty(String PropName) {
		return configurationEngine.getInstance().getProperty().GetProperty(PropName, null);
	}
//	Ortus Property interface

	/**
	 * Get a property value
	 * @param PropName Property Name
	 * @param defaultvalue
	 * @return Property Value
	 */
	public static String GetProperty(String PropName, String defaultvalue) {
		return configurationEngine.getInstance().getProperty().GetProperty(PropName, defaultvalue);
	}

	/**
	 * Set a property value
	 * @param PropName Property Name
	 * @param PropValue Property Value
	 */
	public static void SetProperty(String PropName, Object PropValue) {
		api.DebugLog(api.LogLevel.Trace, "SetProperty: propname: " + PropName + " value: " + PropValue);
		configurationEngine.getInstance().getProperty().SetProperty(PropName, PropValue.toString());
		return;
	}

	/**
	 * Delete a property
	 * @param PropName Property Name
	 */
	public static void RemoveProperty(String PropName) {
		configurationEngine.getInstance().getProperty().RemoveProperty(PropName);
		return;
	}

//SageProperty.java
	/**
	 *
	 * @param PropName
	 * @param DefValue
	 * @return
	 */
	public static String GetSageProperty(String PropName, String DefValue) {
		return ortus.property.SageProperty.GetSageProperty(PropName, DefValue);
	}

	/**
	 *
	 * @param PropName
	 * @param Value
	 */
	public static void SetSageProperty(String PropName, String Value) {
		ortus.property.SageProperty.SetSageProperty(PropName, Value);
		return;
	}

	/**
	 *
	 * @param PropName
	 */
	public static void RemoveSageProperty(String PropName) {
		ortus.property.SageProperty.RemoveSageProperty(PropName);
		return;
	}

	/**
	 *
	 * @param PropertyName
	 * @param DefaultValue
	 * @param Element
	 * @return
	 */
	public static String SetSagePropertyElement(String PropertyName, String DefaultValue, String Element) {
		return ortus.property.SageProperty.SetSagePropertyElement(PropertyName, DefaultValue, Element);
	}

	public static String SetSagePropertyElement(String PropertyName, String Element) {
		return ortus.property.SageProperty.SetSagePropertyElement(PropertyName, "", Element);
	}

	/**
	 *
	 * @param PropertyName
	 * @param DefaultValue
	 * @param Element
	 * @return
	 */
	public static String RemoveSagePropertyElement(String PropertyName, String DefaultValue, String Element) {
		return ortus.property.SageProperty.RemoveSagePropertyElement(PropertyName, DefaultValue, Element);
	}
	
	public static String RemoveSagePropertyElement(String PropertyName, String Element) {
		return ortus.property.SageProperty.RemoveSagePropertyElement(PropertyName, "", Element);
	}

	public static boolean HasSagePropertyElement(String PropertyName, String DefaultValue, String Element) {
		return ortus.property.SageProperty.HasSagePropertyElement(PropertyName, DefaultValue, Element);
	}
	public static boolean HasSagePropertyElement(String PropertyName, String Element) {
		return ortus.property.SageProperty.HasSagePropertyElement(PropertyName, "", Element);
	}

        public static List GetArrayLimit(Object[] x, int limit) {
            return ortus.util.array.GetArrayLimit(x, limit);
        }
	/**
	 *
	 * @param PropertyName
	 * @param DefaultValue
	 * @return
	 */
	public static String[] GetSagePropertyArray(String PropertyName, String DefaultValue) {
		return ortus.property.SageProperty.GetSagePropertyArray(PropertyName, DefaultValue);
	}

	/**
	 *
	 * @param filename
	 */
	public static void LoadSagePropertyFile(String filename){
		ortus.property.SageProperty.LoadSagePropertyFile(filename);
		return;
	}

	/**
	 *
	 * @param property
	 * @return
	 */
	public static String[] GetSageSubpropertiesThatAreBranches(String property){
		return ortus.property.SageProperty.GetSageSubpropertiesThatAreBranches(property);
	}

	/**
	 *
	 * @param property
	 * @return
	 */
	public static String[] GetSageSubpropertiesThatAreLeaves(String property){
		return ortus.property.SageProperty.GetSageSubpropertiesThatAreLeaves(property);
	}

	/**
	 *
	 * @param parentprop
	 * @return
	 */
	public static ArrayList GetSagePropertyAndChildren(String parentprop){
		return ortus.property.SageProperty.GetGetPropertyAndChildren(parentprop);
	}

	/**
	 *
	 * @param filename
	 * @param parentprop
	 * @param DoNotSaveProps
	 * @return
	 */
	public static boolean SaveSagePropertyFile(String filename, String parentprop, String DoNotSaveProps){
		return ortus.property.SageProperty.SaveSagePropertyFile(filename, parentprop, DoNotSaveProps);
	}

	/**
	 *
	 * @param filename
	 * @param parentprop
	 * @return
	 */
	public static boolean SaveSagePropertyFile(String filename, String parentprop){
		return ortus.property.SageProperty.SaveSagePropertyFile(filename, parentprop, "");
	}

// SageServerProperty.java
		/**
	 *
	 * @param PropertyName
	 * @param DefaultValue
	 * @param Element
	 * @return
	 */
	public static String SetSageServerPropertyElement(String PropertyName, String DefaultValue, String Element) {
		return ortus.property.SageServerProperty.SetSageServerPropertyElement(PropertyName, DefaultValue, Element);
	}

	public static String SetSageServerPropertyElement(String PropertyName, String Element) {
		return ortus.property.SageServerProperty.SetSageServerPropertyElement(PropertyName, "", Element);
	}

	/**
	 *
	 * @param PropertyName
	 * @param DefaultValue
	 * @param Element
	 * @return
	 */
	public static String RemoveSageServerPropertyElement(String PropertyName, String DefaultValue, String Element) {
		return ortus.property.SageServerProperty.RemoveSageServerPropertyElement(PropertyName, DefaultValue, Element);
	}

	public static String RemoveSageServerPropertyElement(String PropertyName, String Element) {
		return ortus.property.SageServerProperty.RemoveSageServerPropertyElement(PropertyName, "", Element);
	}

	public static boolean HasSageServerPropertyElement(String PropertyName, String DefaultValue, String Element) {
		return ortus.property.SageServerProperty.HasSageServerPropertyElement(PropertyName, DefaultValue, Element);
	}
	public static boolean HasSageServerPropertyElement(String PropertyName, String Element) {
		return ortus.property.SageServerProperty.HasSageServerPropertyElement(PropertyName, "", Element);
	}
 
//Below is all calls related to Tag.java
	
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String[] GetMediaTags(Object MediaObject)
	{return ortus.media.Tag.GetMediaTags(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @param Tag
	 * @return
	 */
	public static boolean HasMediaTag(Object MediaObject,String Tag)
	{return ortus.media.Tag.HasMediaTag(MediaObject, Tag);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static boolean HasMediaTags(Object MediaObject)
	{return ortus.media.Tag.HasMediaTags(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @param Tag
	 * @return
	 */
	public static String RemoveMediaTag(Object MediaObject,String Tag)
	{return ortus.media.Tag.RemoveMediaTag(MediaObject, Tag);}
	
	/**
	 *
	 * @param MediaObject
	 * @param Tag
	 * @return
	 */
	public static String  SetMediaTag(Object MediaObject,String Tag)
	{return ortus.media.Tag.SetMediaTag(MediaObject, Tag);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String ClearMediaTags(Object MediaObject)
	{return ortus.media.Tag.ClearMediaTags(MediaObject);}
	
	
//ortus.util.scrubString

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String ScrubGetShowCategory(Object MediaObject)
	{return ortus.util.scrubString.GetShowCategory(MediaObject);}
		
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String ScrubGetShowSubCategory(Object MediaObject)
	{return ortus.util.scrubString.GetShowSubCategory(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String ScrubGetShowCombCategories(Object MediaObject)
	{return ortus.util.scrubString.GetShowCombCategories(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String[] GetShowCategories(Object MediaObject)
	{return ortus.util.scrubString.GetShowCategories(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String ScrubGetShowDescription(Object MediaObject)
	{return ortus.util.scrubString.GetShowDescription(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String ScrubGetShowTagLine(Object MediaObject)
	{return ortus.util.scrubString.GetShowTagLine(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String[] ScrubGetPeopleInShowRoleActor(Object MediaObject)
	{return ortus.util.scrubString.GetPeopleInShowRoleActor(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String[] ScrubGetPeopleInShowRoleDirector(Object MediaObject)
	{return ortus.util.scrubString.GetPeopleInShowRoleDirector(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String[] ScrubGetPeopleInShowRoleWriter(Object MediaObject)
	{return ortus.util.scrubString.GetPeopleInShowRoleWriter(MediaObject);}
	
	/**
	 *
	 * @param Actor
	 * @return
	 */
	public static String ScrubGetActorName(String Actor)
	{return ortus.util.scrubString.GetActorName(Actor);}
	
	/**
	 *
	 * @param Actor
	 * @return
	 */
	public static String ScrubGetActorRole(String Actor)
	{return ortus.util.scrubString.GetActorRole(Actor);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String ScrubGetUserRating(Object MediaObject)
	{return ortus.util.scrubString.GetUserRating(MediaObject);}
	
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static int ScrubGetShowDuration(Object MediaObject)
	{return ortus.util.scrubString.GetShowDuration(MediaObject);}
        
        public static String ScrubFileName(String filename) {
            return ortus.util.scrubString.ScrubFileName(filename);
        }
	
	
//ortus.util.array

	/**
	 *
	 * @param Array
	 * @return
	 */
	public static Object[] toArray(Object Array)	{
		return ortus.util.array.toArray(Array);
	}

	/**
	 * 
	 * @param m
	 * @return
	 */
	public static List GetMapKeysAsList(Map m) {
		return ortus.util.array.GetMapKeysAsList(m);
	}

	/**
	 *
	 * @param l
	 * @param element
	 * @param index
	 * @return
	 */
	public static List MoveElementToIndex(List l, Object element, int index){
		return ortus.util.array.MoveElementToIndex(l, element, index);
	}

	/**
	 *
	 * @param MediaObjects
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static Object[] AddArrayElements(Object MediaObjects, int i1, int i2)	{
		return ortus.util.array.AddArrayElements(MediaObjects, i1, i2);
	}

	public static List RemoveElements(Object superset, Object subset){
		return ortus.util.array.RemoveElements(superset, subset);
	}


//
	/**
	 *
	 * @param logfile
	 * @return
	 */
	public static List<String> DumpLogFile(String logfile)	{
		return ortus.util.dump.DumpLogFile(logfile);
	}

	public static String DumpVideo() {
		return ortus.util.dump.DumpVideo();
	}
	
	public static String DumpVideo(String filename) {
		return ortus.util.dump.DumpVideo(filename);
	}

	public static boolean IsFileExist(String filename) {
		return ortus.util.file.IsFileExist(filename);
	}
	/**
	 *
	 * @param folder
	 * @return
	 */
	public static List GetFolderDetail(String folder)	{
		return ortus.util.file.GetFolderDetail(folder);
	}

	/**
	 *
	 * @param Array
	 * @return
	 */
	public static List GetFoldersDetails(Object Array)	{
		return ortus.util.file.GetFoldersDetails(Array);
	}

	/**
	 *
	 * @param A
	 * @param wrt0
	 * @return
	 */
	public static double GetVerticalPositionWithOverscan(double A, boolean wrt0) {
		 return ortus.util.ui.GetVerticalPositionWithOverscan(A, wrt0);
	 }

	/**
	 *
	 * @param A
	 * @param wrt0
	 * @return
	 */
	public static double GetHorizontalPositionWithOverscan(double A, boolean wrt0) {
		 return ortus.util.ui.GetHorizontalPositionWithOverscan(A, wrt0);
	 }

	 /**
	  *
	  * @param filepath
	  * @return
	  */
	 public static boolean IsFilePath(String filepath){
		 return ortus.util.file.IsFilePath(filepath);
	 }

	 public static String GetFilePathForSegment(Object mf, int segment){
		 return ortus.util.file.GetFilePathForSegment(mf, segment);
	 }
	 /**
	  *
	  * @param str
	  * @return
	  */
	 public static String MD5Sum(String str) {
		 return ortus.util.math.MD5Sum(str);
	 }

         public static long GetCurrentEpoch() {
             return ortus.util.date.GetCurrentEpoch();
         }
	 /**
	  *
	  * @param timestamp
	  * @return
	  */
	 public static long GetEpochFromDate(String timestamp) {
		 return ortus.util.date.GetEpochFromDate(timestamp);
	 }

         public static String GetFormatDate(String current_date, String output_format) {
                return ortus.util.date.GetFormatDate(current_date, output_format);
         }
//Search.java
	
	 /**
	  *
	  * @param SearchString
	  * @return
	  */
	 public static Object search(String SearchString){
		return ortus.media.Search.search(MediaFileAPI.GetMediaFiles(), SearchString, "");
	}

	 /**
	  *
	  * @param Source
	  * @param SearchString
	  * @return
	  */
	 public static Object search(Object Source, String SearchString){
		return ortus.media.Search.search(api.toArray(Source), SearchString, "");
	}

	/**
	 *
	 * @param SearchString
	 * @param SortMethod
	 * @return
	 */
	public static Object search(String SearchString, String SortMethod){
		return ortus.media.Search.search(MediaFileAPI.GetMediaFiles(), SearchString, SortMethod);
	}

	/**
	 *
	 * @param Source
	 * @param SearchString
	 * @param SortMethod
	 * @return
	 */
	public static Object search(Object Source, String SearchString, String SortMethod){
		return ortus.media.Search.search(api.toArray(Source), SearchString, SortMethod);
	}

	/**
	 *
	 * @param SearchString
	 * @param SortMethod
	 * @param GroupByMethod
	 * @return
	 */
	public static Map search(String SearchString, String SortMethod, String GroupByMethod){
		return ortus.media.Search.search(MediaFileAPI.GetMediaFiles(), SearchString, SortMethod, GroupByMethod);
	}

	/**
	 *
	 * @param Source
	 * @param SearchString
	 * @param SortMethod
	 * @param GroupByMethod
	 * @return
	 */
	public static Map search(Object Source, String SearchString, String SortMethod, String GroupByMethod){
		return ortus.media.Search.search(api.toArray(Source), SearchString, SortMethod, GroupByMethod);
	}
	
	

// database.java calls
	/**
	 *
	 * @param MediaFiles
	 * @param filepos
	 * @return
	 */
	public static HashMap<String,List<Object>>GroupByPath(List<Object> MediaFiles, String filepos) {
            return ortus.util.file.GroupByPath(MediaFiles, filepos);
        }

	public static Object GroupByGenre(List<Object>mediafiles, String filepos) {
		return ortus.util.file.GroupByGenre(mediafiles, filepos);
	}

	/**
	 *
	 * @param SearchString
	 * @param MediaType
	 * @param size
	 * @return
	 */
	public static Object[] GetLastMediaAdded(String SearchString,String MediaType,int size){
            return ortus.media.util.GetLastMediaAdded(SearchString, MediaType, size);
        }

	/**
	 *
	 * @param SearchString
	 * @param MediaType
	 * @return
	 */
	public static Object[] GetLastMediaAdded(String SearchString,String MediaType){
            return ortus.media.util.GetLastMediaAdded(SearchString, MediaType, 10);
        }

	public static void RegisterFanartProvider(String providername, IFanartProvider provider) {
		fanartEngine.getInstance().RegisterFanartProvider(providername, provider);
	}


	public static void UnRegisterFanartProvider(String providername) {
		fanartEngine.getInstance().UnRegisterFanartProvider(providername);
	}

	public static void SetFanartProvider(String providername) {
		fanartEngine.getInstance().SetFanartProvider(providername);
	}

	public static void ResetFanartProvider() {
		fanartEngine.getInstance().SetFanartProvider("db");
	}

	public static String GetFanartProvider() {
		return fanartEngine.getInstance().GetFanartProvider();
	}

	public static Object[] GetFanartProviders() {
		return fanartEngine.getInstance().GetFanartProviders();
	}

// fanart.java calls
	/**
	 * Set the ortus fanart folder location
	 * @param folder
	 */
        public static void SetFanartFolder(String folder) {
            ortus.media.fanart.fanartEngine.getInstance().getProvider().SetFanartFolder(folder);
            return;
        }
	/**
	 * Get the ortus fanart folder location
	 * @return
	 */
        public static String GetFanartFolder() {
            return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartFolder();
        }
	/**
	 * Get fanart poster for an media object
	 * @param mediafile
	 * @return path of fanart poster
	 */
	public static String GetFanartPoster(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartPoster(mediafile);
	}
	/**
	 * Get fanart poster for an media object
	 * @param mediafile
	 * @return path of fanart poster
	 */
	public static String GetFanartPosterThumb(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartPosterThumb(mediafile);
	}
	/**
	 * Get fanart poster for an media object
	 * @param mediafile
	 * @return path of fanart poster
	 */
	public static String GetFanartPosterCover(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartPosterCover(mediafile);
	}
	/**
	 * Get fanart poster for an media object
	 * @param mediafile
	 * @return path of fanart poster
	 */
	public static String GetFanartPosterHigh(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartPosterHigh(mediafile);
	}

	/**
	 * Get all fanart posters for a media object
	 * @param mediafile
	 * @return an array of all poster fanart
	 */
	public static List<Object> GetFanartPosters(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartPosters(mediafile);
	}
	/**
	 * Get fanart banner for a media object
	 * @param mediafile
	 * @return path of fanart banner
	 */
	public static String GetFanartBanner(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartBanner(mediafile);
	}
	/**
	 * Get all fanart banners for a media object
	 * @param mediafile
	 * @return an array of fanart banners
	 */
	public static List<Object> GetFanartBanners(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartBanners(mediafile);
	}
	/**
	 * Get fanart background for a media object
	 * @param mediafile
	 * @return path of fanart background
	 */
	public static String GetFanartBackground(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartBackground(mediafile);
	}
	/**
	 * Get fanart background for a media object
	 * @param mediafile
	 * @return path of fanart background
	 */
	public static String GetFanartBackgroundThumb(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartBackgroundThumb(mediafile);
	}
	/**
	 * Get fanart background for a media object
	 * @param mediafile
	 * @return path of fanart background
	 */
	public static String GetFanartBackgroundCover(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartBackgroundCover(mediafile);
	}
	/**
	 * Get fanart background for a media object
	 * @param mediafile
	 * @return path of fanart background
	 */
	public static String GetFanartBackgroundHigh(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartBackgroundHigh(mediafile);
	}
	/**
	 * Get fanart background for a media object
	 * @param mediafile
	 * @return path of fanart background
	 */
	public static String GetFanartBackgroundHighRandom() {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartBackgroundHighRandom();
	}

	/**
	 * Get all fanart backgrounds for a media object
	 * @param mediafile
	 * @return an array of fanart backgrounds
	 */
	public static List<Object> GetFanartBackgrounds(Object mediafile) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartBackgrounds(mediafile);
	}
	/**
	 * Randomize an array of fanart
	 * @param fanart array
	 * @return randomized fanart array
	 */
	public static List<Object> GetFanartRandom(List<Object> fanart) {
		return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartRandom(fanart);
	}
	/**
	 *
	 * @param castname
	 * @return
	 */
        public static String GetCastFanartPoster(String castname) {
            return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetCastFanartPoster(castname);
        }
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetSeasonFanartBanner(Object MediaObject) {
            return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetSeasonFanartBanner(MediaObject);
        }
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static List<Object> GetSeasonFanartBanners(Object MediaObject) {
            return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetSeasonFanartBanners(MediaObject);
        }
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetSeasonFanartPoster(Object MediaObject) {
            return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetSeasonFanartPoster(MediaObject);
        }
	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static List<Object> GetSeasonFanartPosters(Object MediaObject) {
            return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetSeasonFanartPosters(MediaObject);
        }
	/**
	 *
	 * @param MenuType
	 * @return
	 */
	public static String GetMenuBackground(String MenuType){
            return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetMenuBackground(MenuType);
       }

	/**
	 *
	 * @param MenuType
	 * @return
	 */
	public static List<String> GetMenuBackgrounds(String MenuType){
           return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetMenuBackgrounds(MenuType);
        }

	/**
	 *
	 * @param MediaObjects
	 * @throws IOException
	 */
	public static void CreateFanartFromJPG(Object[] MediaObjects)throws IOException {
             ortus.media.fanart.fanartEngine.getInstance().getProvider().CreateFanartFromJPG(MediaObjects);
        }

	/**
	 *
	 * @param MediaObjects
	 * @param Type
	 * @return
	 */
	public static Map<String,List> GetFanartCleanupList(Object[] MediaObjects,String Type){
           return ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartCleanupList(MediaObjects, Type);
        }

	/**
	 *
	 * @param MediaObjects
	 * @return
	 */
	public static Map<String,List> GetFanartCleanupList(Object[] MediaObjects){
            Object[] MediaTV = (Object[]) sagex.api.Database.FilterByBoolMethod(MediaObjects, "ortus_api_IsMediaTypeTV", true);
            Object[] MediaMovies = (Object[]) sagex.api.Database.FilterByBoolMethod(MediaObjects, "ortus_api_IsMediaTypeTV", false);
            Map<String,List> TV = ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartCleanupList(MediaTV,"TV");
            Map<String,List> Movies =ortus.media.fanart.fanartEngine.getInstance().getProvider().GetFanartCleanupList(MediaMovies,"Movies");
            TV.putAll(Movies);

            return TV;
        }

	/**
	 *
	 * @param FoldersMap
	 * @param Type
	 */
	public static void FanartCleanupMove(HashMap<String,List> FoldersMap,String Type){
            ortus.media.fanart.fanartEngine.getInstance().getProvider().FanartCleanupMove(FoldersMap, Type);
        }

	/**
	 *
	 * @param FoldersMap
	 */
	public static void FanartCleanupMove(HashMap<String,List> FoldersMap){
            ortus.media.fanart.fanartEngine.getInstance().getProvider().FanartCleanupMove(FoldersMap, "All");
        }

	/**
	 *
	 * @param Folder
	 * @param Type
	 */
	public static void FanartCleanupMove(String Folder,String Type){
            HashMap<String,List> List = new HashMap<String,List>();
            List.put(Folder, null);
            ortus.media.fanart.fanartEngine.getInstance().getProvider().FanartCleanupMove(List,Type);
        }

	/**
	 *
	 * @param Folder
	 */
	public static void FanartCleanupMove(String Folder){
            HashMap<String,List> List = new HashMap<String,List>();
            List.put(Folder, null);
            ortus.media.fanart.fanartEngine.getInstance().getProvider().FanartCleanupMove(List, "ALL");
        }

	/**
	 *
	 * @param FoldersMap
	 */
	public static void FanartCleanupDelete(HashMap<String,List> FoldersMap){
            ortus.media.fanart.fanartEngine.getInstance().getProvider().FanartCleanupDelete(FoldersMap);
        }

	/**
	 *
	 * @param Folder
	 */
	public static void FanartCleanupDelete(String Folder){
            HashMap<String,List> List = new HashMap<String,List>();
            List.put(Folder, null);
            ortus.media.fanart.fanartEngine.getInstance().getProvider().FanartCleanupDelete(List);
        }

       	/**
	 * Get the value of a theme property
	 * @param PropertyName Property Name
	 * @return Property Value
	 */
	public static String GetThemePropertyPath(String PropertyName) {
		return Ortus.getInstance().getTheme().GetThemePropertyPath(PropertyName);
	}

       	/**
	 * Get the value of a theme property
	 * @param PropertyName Property Name
	 * @return Property Value
	 */
	public static String GetThemeProperty(String PropertyName) {
		return Ortus.getInstance().getTheme().GetThemeProperty(PropertyName);
	}
	/**
	 * Set the value of a theme property
	 * @param PropertyKey Property Name
	 * @param PropertyValue Property Value
	 */
	public static void SetThemeProperty(String PropertyKey, String PropertyValue) {
		Ortus.getInstance().getTheme().SetThemeProperty(PropertyKey,PropertyValue);
		return;
	}
	/**
	 *
	 * @param PropertyName
	 */
	public static void RemoveThemeProperty(String PropertyName) {
		Ortus.getInstance().getTheme().RemoveThemeProperty(PropertyName);
		return;
	}
	/**
	 * Load a new ortus theme
	 * @param theme Theme name
	 */
	public static void LoadTheme(String theme) {
		Ortus.getInstance().getTheme().LoadTheme(theme);

		return;
	}
	/**
	 * Get a theme description
	 * @param theme Theme name
	 * @return Theme description
	 */
	public static String GetThemeDesc(String theme) {
		return Ortus.getInstance().getTheme().GetThemeDesc(theme);
	}

	/**
	 * Get an array of all ortus themes
	 * @return Array of themes
	 */
	public static Object[] GetThemes() {
		return Ortus.getInstance().getTheme().GetThemes();
	}

// ortus.menu

	/**
	 * Return a list of Main Menu Items
	 * @return Vector of menu items
	 */
	public static Object[] GetMenu() {
		return configurationEngine.getInstance().getIdentity().getMenu().getMenu();
	}

	/**
	 *
	 * @param menutitle
	 * @return
	 */
	public static String GetMenuType(String menutitle) {
            return configurationEngine.getInstance().getIdentity().getMenu().GetMenuType(menutitle);
        }
	/**
	 *
	 * @param menutitle
	 * @param menutype
	 */
	public static void SetMenuType(String menutitle, String menutype) {
            configurationEngine.getInstance().getIdentity().getMenu().SetMenuType(menutitle, menutype);
            return;
        }

	/**
	 * Return Sage menu to execute for a menu item
	 * @param m Menu Name
	 * @return Sage Menu to execute
	 */
	public static String GetMenuAction(String m) {
		return configurationEngine.getInstance().getIdentity().getMenu().GetMenuAction(m);
	}

	/**
	 * Return Sage menu to execute for a sub menu item
	 * @param m Menu Name
	 * @param s Sub Menu Name
	 * @return Sage menu to execute
	 */
	public static String GetSubMenuAction(String m, String s) {
		return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuAction(m,s);
	}
	/**
	 * Return a list of Static variables to set upon selection of a sub menu item
	 * @param m Menu Name
	 * @param s Sub Menu Name
	 * @return Sage menu to execute
	 */
       public static void addMenuItemStatic(String m, String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addMenuItemStatic(m,Title,var,val);
        }
        public static void addMenuItemStatic(String m, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addMenuItemStatic(m,var,var,val);
        }
        public static void addMenuItemGlobal(String m, String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addMenuItemGlobal(m,Title,var,val);
        }
        public static void addMenuItemImage(String m, String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addMenuItemImage(m,Title,var,val);
        }
        public static void addMenuItemSageCommand(String m, String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addMenuItemSageCommand(m,Title,var,val);
        }
        public static void addMenuItemProperty(String m, String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addMenuItemProperty(m,Title,var,val);
        }

        public static void delMenuItemStatic(String m, String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delMenuItemStatic(m,var);
        }
        public static void delMenuItemGlobal(String m, String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delMenuItemGlobal(m,var);
        }
        public static void delMenuItemImage(String m, String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delMenuItemImage(m,var);
        }
        public static void delMenuItemSageCommand(String m, String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delMenuItemSageCommand(m,var);
        }
        public static void delMenuItemProperty(String m, String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delMenuItemProperty(m,var);
        }

        public static Object getMenuItemStatic(String m,String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemStatic(m,var);
        }
        public static Object getMenuItemGlobal(String m, String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemGlobal(m,var);
        }
        public static Object getMenuItemImage(String m, String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemImage(m,var);
        }
        public static Object getMenuItemSageCommand(String m, String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemSageCommand(m,var);
        }
        public static Object getMenuItemProperty(String m, String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemProperty(m,var);
        }

        public static Object[] getMenuItemStatic(String m) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemStatic(m);
        }
        public static Object[] getMenuItemGlobal(String m) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemGlobal(m);
        }
        public static Object[] getMenuItemImage(String m) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemImage(m);
        }
        public static Object[] getMenuItemSageCommand(String m) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemSageCommand(m);
        }
        public static Object[] getMenuItemProperty(String m) {
            return configurationEngine.getInstance().getIdentity().getMenu().getMenuItemProperty(m);
        }

        public static void dumpMenuItems() {
            configurationEngine.getInstance().getIdentity().getMenu().dumpMenuItems();
        }
	/**
	 * Return a sub menu for a main menu item
	 * @param m Main Menu
	 * @return Sub menu for a main menu
	 */
	public static Object[] GetSubMenu(String m) {
		return configurationEngine.getInstance().getIdentity().getMenu().getSubMenu(m);
	}

	/**
	 * Delete a main menu item
	 * @param m Main Menu Title
	 * @return return code
	 */
	public static int DeleteMenu(String m) {
		return configurationEngine.getInstance().getIdentity().getMenu().deleteMenu(m);
	}

	/**
	 * Add a new Main Menu
	 * @param mt Main Menu Title
	 * @param ma Sage menu to execute
	 */
	public static void AddMenu(String mt, String ma) {
		configurationEngine.getInstance().getIdentity().getMenu().addMenu(mt, ma);
		return;
	}

	/**
	 * Update a Main Menu title
	 * @param mt Main Menu Title
	 * @param nmt New Main Menu Title
	 */
	public static void UpdateMenuTitle(String mt, String nmt) {
		configurationEngine.getInstance().getIdentity().getMenu().updateMenuTitle(mt, nmt);

		return;
	}

	/**
	 * Update a Main Menu Action
	 * @param mt Main Menu Title
	 * @param nma Sage menu name
	 * @return return code
	 */
	public static int UpdateMenuAction(String mt, String nma) {
		return configurationEngine.getInstance().getIdentity().getMenu().updateMenuAction(mt, nma);
	}

        public static int GetPosition(String mt) {
            return configurationEngine.getInstance().getIdentity().getMenu().getPosition(mt);
        }
	/**
	 * Increase the menu position
	 * @param mt Main Menu Title
	 */
	public static void IncPosition(String mt) {
		configurationEngine.getInstance().getIdentity().getMenu().incPosition(mt);
		return;
	}

	/**
	 * Decrease the menu position
	 * @param mt Main Menu Title
	 */
	public static void DecPosition(String mt) {
		configurationEngine.getInstance().getIdentity().getMenu().decPosition(mt);
		return;
	}

	/**
	 * Add a new Sub Menu Item
	 * @param mt Main Menu Title
	 * @param smt Sub Menu Title
	 * @param sma Sage menu for sub menu action
	 * @return return code
	 */
	public static int AddSubMenu(String mt, String smt, String sma) {
		return configurationEngine.getInstance().getIdentity().getMenu().addSubMenu(mt, smt, sma);
	}

	/**
	 * Update Sub Menu Title
	 * 	@param mt Main Menu Title
	 *	@param smt Sub Menu Titile
	 * 	@param nsmt New Sub Menu Title
	 * @return return code
	 */
	public static int UpdateSubMenuTitle(String mt, String smt, String nsmt) {
		return configurationEngine.getInstance().getIdentity().getMenu().updateSubMenuTitle(mt, smt, nsmt);
	}

	/**
	 * Update a Sub Menu Item Action
	 * @param mt Main Menu Title
	 * @param smt Sub Menu Title
	 * @param nsma Sage menu to execute
	 * @return return code
	 */
	public static int UpdateSubMenuAction(String mt, String smt, String nsma) {
		return configurationEngine.getInstance().getIdentity().getMenu().updateSubMenuAction(mt, smt, nsma);
	}

	/**
	 * Delete a Sub Menu Item
	 * @param mt Main Menu Title
	 * @param smt Sub Menu Title
	 * @return return code
	 */
	public static int DeleteSubMenu(String mt, String smt) {
		return configurationEngine.getInstance().getIdentity().getMenu().deleteSubMenu(mt, smt);
	}

        public static void addSubMenuItemStatic(String m, String sm, String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addSubMenuItemStatic(m,sm,Title,var,val);
        }
        public static void addSubMenuItemStatic(String m, String sm,String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addSubMenuItemStatic(m,sm,var,var,val);
        }
        public static void addSubMenuItemGlobal(String m, String sm,String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addSubMenuItemGlobal(m,sm,Title,var,val);
        }
        public static void addSubMenuItemImage(String m, String sm,String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addSubMenuItemImage(m,sm,Title,var,val);
        }
        public static void addSubMenuItemSageCommand(String m, String sm,String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addSubMenuItemSageCommand(m,sm,Title,var,val);
        }
        public static void addSubMenuItemProperty(String m, String sm,String Title, String var, String val) {
            configurationEngine.getInstance().getIdentity().getMenu().addSubMenuItemProperty(m,sm,Title,var,val);
        }

        public static void delSubMenuItemStatic(String m, String sm,String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delSubMenuItemStatic(m,sm,var);
        }
        public static void delSubMenuItemGlobal(String m, String sm,String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delSubMenuItemGlobal(m,sm,var);
        }
        public static void delSubMenuItemImage(String m, String sm,String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delSubMenuItemImage(m,sm,var);
        }
        public static void delSubMenuItemSageCommand(String m, String sm,String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delSubMenuItemSageCommand(m,sm,var);
        }
        public static void delSubMenuItemProperty(String m, String sm,String var) {
            configurationEngine.getInstance().getIdentity().getMenu().delSubMenuItemProperty(m,sm,var);
        }

        public static Object getSubMenuItemStatic(String m,String sm,String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemStatic(m,sm,var);
        }
        public static Object getSubMenuItemGlobal(String m, String sm,String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemGlobal(m,sm,var);
        }
        public static Object getSubMenuItemImage(String m, String sm,String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemImage(m,sm,var);
        }
        public static Object getSubMenuItemSageCommand(String m, String sm,String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemSageCommand(m,sm,var);
        }
        public static Object getSubMenuItemProperty(String m, String sm,String var) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemProperty(m,sm,var);
        }

        public static Object[] getSubMenuItemStatic(String m, String sm) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemStatic(m,sm);
        }
        public static Object[] getSubMenuItemGlobal(String m, String sm) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemGlobal(m,sm);
        }
        public static Object[] getSubMenuItemImage(String m, String sm) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemImage(m,sm);
        }
        public static Object[] getSubMenuItemSageCommand(String m,String sm) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemSageCommand(m,sm);
        }
        public static Object[] getSubMenuItemProperty(String m,String sm) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuItemProperty(m,sm);
        }

        public static int GetSubMenuPosition(String mt, String smt) {
            return configurationEngine.getInstance().getIdentity().getMenu().getSubMenuPosition(mt,smt);
        }

	/**
	 *
	 * @param mt
	 * @param smt
	 */
	public static void IncSubMenuPosition(String mt, String smt) {
		configurationEngine.getInstance().getIdentity().getMenu().incSubMenuPosition(mt, smt);
		return;
	}

	/**
	 *
	 * @param mt
	 * @param smt
	 */
	public static void DecSubMenuPosition(String mt, String smt) {
		configurationEngine.getInstance().getIdentity().getMenu().decSubMenuPosition(mt, smt);
		return;
	}

// ortus.db api
      /**
       *
       * @return
       */
      public static Connection GetConnection() {
          return Ortus.getInstance().getDB().GetConnection();
      }
      /**
       *
       * @param sql
       * @return
       */
      public static int executeSQL(String sql) {
            return Ortus.getInstance().getDB().executeSQL(sql);
        }

      public static boolean BackupDatabase() {
	      return Ortus.getInstance().getDB().backupDB();
      }

      public static String GetAppleTrailerTitle(HashMap thm) {
          return ortus.onlinescrapper.trailers.api.getAppleTrailerTitle(thm);
      }

      public static String GetAppleTrailerPostDate(HashMap thm) {
          return ortus.onlinescrapper.trailers.api.getAppleTrailerPostDate(thm);
      }

      public static String GetAppleTrailerReleaseDate(HashMap thm) {
          return ortus.onlinescrapper.trailers.api.getAppleTrailerReleaseDate(thm);
      }
      public static List<HashMap> GetAppleTrailers() {
              return ortus.onlinescrapper.trailers.api.getAppleTrailers();
      }
      public static List<HashMap> GetAppleTrailers(String filename) {
              return ortus.onlinescrapper.trailers.api.getAppleTrailers(filename);
      }
      
      public static void DownloadAppleTrailiers(String dirname) {
              ortus.onlinescrapper.trailers.api.downloadTrailers(dirname);
      }
      
      public static boolean BackupWiz() {
               return ortus.onlinescrapper.tools.SageMetadata.BackupWiz();
      }

      public static boolean RestoreDatabase(String backupfile) {
	      return Ortus.getInstance().getDB().restoreDB(backupfile);
      }

      public static boolean LoadTasks() {
	      return Ortus.getInstance().getCronServer().LoadTasks();
      }
      public static boolean CreateTask(String taskid, String description, String taskname, String tasktime, long interval, Object[] params) {
	      return Ortus.getInstance().getCronServer().CreateTask(taskid, description, taskname, tasktime, interval, params);
      }    
      public static Object[] GetTaskList() {
          return Ortus.getInstance().getCronServer().GetTaskList();
      }
      public static String GetTaskDescription(String taskid) {
          return Ortus.getInstance().getCronServer().GetTaskDescription(taskid);
      }
      public static void Schedule(String taskid, OrtusTask task, long interval) {
          Ortus.getInstance().getCronServer().Schedule(taskid, task, interval);
      }
      public static void Schedule(String taskid, OrtusTask task, Date startdate) {
          Ortus.getInstance().getCronServer().Schedule(taskid, task, startdate);
      }
      public static void Schedule(String taskid, OrtusTask task, Date startdate, long interval) {
          Ortus.getInstance().getCronServer().Schedule(taskid, task, startdate, interval);
      }
      public static void CancelTask(String taskid) {
          Ortus.getInstance().getCronServer().Cancel(taskid);
      }

     public static boolean LoadServerTasks() {
	      return (Boolean)ortus.process.executeRemote("ortus.api.LoadTasks", null);
      }
      public static boolean CreateServerTask(String taskid, String description, String taskname, String tasktime, long interval, Object[] params) {
	      return (Boolean)ortus.process.executeRemote("ortus.api.CreateTask", new Object[] { taskid, description, taskname, tasktime, interval, params} );
      }
      public static Object[] GetServerTaskList() {
          return (Object[])ortus.process.executeRemote("ortus.api.GetTaskList", null);
      }
      public static String GetServerTaskDescription(String taskid) {
          return (String)ortus.process.executeRemote("ortus.api.GetTaskDescription", new Object[] { taskid } );
      }
      public static void ScheduleServer(String taskid, OrtusTask task, long interval) {
          ortus.process.executeRemote("ortus.api.Schedule", new Object[] { taskid, task, interval});
      }
      public static void ScheduleServer(String taskid, OrtusTask task, Date startdate) {
          ortus.process.executeRemote("ortus.api.Schedule", new Object[] { taskid, task, startdate });
      }
      public static void ScheduleServer(String taskid, OrtusTask task, Date startdate, long interval) {
          ortus.process.executeRemote("ortus.api.Schedule", new Object[] { taskid, task, startdate, interval});
      }
      public static void CancelServerTask(String taskid) {
          ortus.process.executeRemote("ortus.api.CancelTask", new Object[] { taskid } );
      }

        public static List<Object> executeSQLQuery(String sql) {
            return Ortus.getInstance().getDB().executeSQLQuery(sql);
        }
        /**
         * Execute an SQL Query and return rows
         * @param sql SQL to execute
         * @return Array of data
         */
        public static List<Object> executeSQLQueryCache(String sql) {
            return Ortus.getInstance().getDB().executeSQLQueryCache(sql);
        }	
        /**
         * Execute an SQL Query and return rows
         * @param sql SQL to execute
         * @return Array of data
         */
        public static List<List> executeSQLQueryArray(String sql) {
            return Ortus.getInstance().getDB().executeSQLQueryArray(sql);
        }
        /**
         * Execute an SQL Query and return rows
         * @param sql SQL to execute
         * @return Array of data
         */
        public static List<List> executeSQLQueryArrayCache(String sql) {
            return Ortus.getInstance().getDB().executeSQLQueryArrayCache(sql);
        }

	        /**
         * Execute an SQL Query and return rows
         * @param sql SQL to execute
         * @return Array of data
         */
        public static List<HashMap> executeSQLQueryHash(String sql) {
            return Ortus.getInstance().getDB().executeSQLQueryHash(sql);
        }
        /**
         * Execute an SQL Query and return rows
         * @param sql SQL to execute
         * @return Array of data
         */
        public static List<HashMap> executeSQLQueryHashCache(String sql) {
            return Ortus.getInstance().getDB().executeSQLQueryHashCache(sql);
        }


        /**
         * Return MediaObjects based on where clause
         * @param sql Where cluase to pass
         * @return MediaObjects that match the where clause
         */
        public static List<Object> getMediaFilesSQL(String sql) {
            return Ortus.getInstance().getDB().getMediaFilesSQL(sql);
        }
	/**
	 *
	 * @return
	 */
	public static boolean IsRemoteHost() {

		if ( ortus.api.GetProperty("remotehost", "none").equalsIgnoreCase("none"))
			return false;
		else
			return true;
	}
	/**
	 *
	 * @param commandparm
	 * @return
	 */
	public static Object ExecuteRemoteCMD(String methd, Object[] commandparm) {
		if ( ! IsRemoteHost())
			return null;
		return ortus.daemon.api.executecCMD(ortus.api.GetProperty("remotehost", null), methd, commandparm);
	}

	public static void AddProcessQueue(String methd, Object[] o) {
		Ortus.getInstance().getProcessServer().AddQueue(methd, o);
	}
	public static void AddMetadataQueue(String methd, Object[] o) {
		Ortus.getInstance().getProcessServer().AddMetadataQueue(methd, o);
	}

	/**
	 *
	 *
	 * @param mediafile
	 * @return
	 */
	public static List<Object> GetScrapperMatches(Object mediafile) {
            return ortus.onlinescrapper.api.GetScrapperMatches(mediafile);
        }

	/**
	 *
	 * @return
	 */
	public static List<Object> GetScrapperUnmatchedMedia() {
            return ortus.onlinescrapper.api.GetScrapperUnmatchedMedia();
        }

	/**
	 *
	 * @return
	 */
	public static List<Object> GetScrapperUnmatchedMediaStub() {
		List<Object> result = new ArrayList<Object>();
		DebugLog(LogLevel.Trace2,"remote command starting");
		Object remresult = ortus.api.ExecuteRemoteCMD("ortus.api.GetScrapperUnmatchedMediaRemote",null);

		DebugLog(LogLevel.Trace2,"remote command returned: " + remresult);
		try {
			DebugLog(LogLevel.Trace2,"startin");
			DebugLog(LogLevel.Trace2,"size: " + ((List<Object>)remresult).size());
			for ( Object x : ((List<Object>)remresult)) {
				DebugLog(LogLevel.Trace2,"processing: " + x);
				result.add(MediaFileAPI.GetMediaFileForID(Integer.parseInt((String)x)));
			}
		} catch(Exception e) {
			DebugLog(LogLevel.Trace2,"Exception: " , e);
		}
		return result;
		
	}

	/**
	 *
	 * @return
	 */
	public static List<Object> GetScrapperUnmatchedMediaRemote() {
            return ortus.onlinescrapper.api.GetScrapperUnmatchedMediaRemote();
        }

	/**
	 *
	 * @return
	 */
	public static List<Object> GetScrapperMatchedMedia() {
            return ortus.onlinescrapper.api.GetScrapperMatchedMedia();
        }
	/**
	 *
	 * @param mediafile
	 * @param scantype
	 * @return
	 */
	public static List<Object> GetScrapperMatches(Object mediafile, int scantype) {
            return ortus.onlinescrapper.api.GetScrapperMatches(mediafile,scantype);
        }
	/**
	 *
	 * @param scrapperid
	 * @return
	 */
	public static int GetScrapperScanType(Object scrapperid) {
            return ortus.onlinescrapper.api.GetScrapperScanType(scrapperid);
        }
	/**
	 *
	 * @param scrapperid
	 * @return
	 */
	public static String GetScrapperSearchTitle(Object scrapperid) {
            return ortus.onlinescrapper.api.GetScrapperSearchTitle(scrapperid);
        }
	/**
	 *
	 * @param scrapperid
	 * @return
	 */
	public static String GetScrapperFoundTitle(Object scrapperid) {
            return ortus.onlinescrapper.api.GetScrapperFoundTitle(scrapperid);
        }
	/**
	 *
	 * @param scrapperid
	 * @return
	 */
	public static String GetScrapperFoundKey(Object scrapperid) {
            return ortus.onlinescrapper.api.GetScrapperFoundKey(scrapperid);
        }
	/**
	 *
	 * @param scrapperid
	 * @return
	 */
	public static String GetScrapperCacheDescription(Object scrapperid) {
            return ortus.onlinescrapper.api.GetScrapperCacheDescription(scrapperid);
        }
	/**
	 *
	 * @param scrapperid
	 * @return
	 */
	public static Object GetScrapperCacheDate(Object scrapperid) {
            return ortus.onlinescrapper.api.GetScrapperCacheDate(scrapperid);
        }
	/**
	 *
	 * @param scrapperid
	 * @return
	 */
	public static Object GetScrapperCacheMpaa(Object scrapperid) {
            return ortus.onlinescrapper.api.GetScrapperCacheMpaa(scrapperid);
        }

	/**
	 *
	 * @param mediafile
	 * @param title
	 * @return
	 */
	public static int ManualMovieSearch(Object mediafile, String title) {
            return ortus.onlinescrapper.api.ManualMovieSearch(mediafile, title);
        }

        public static List<HashMap> LiveSearch(String scope, String title) {
            return ortus.onlinescrapper.api.LiveSearch(scope, title);
        }

        public static void LiveSearchStore(HashMap entry) {
            ortus.onlinescrapper.api.LiveSearchStore(entry);
        }
	/**
	 *
	 * @param mediafile
	 * @param scrapperid
	 * @param download_fanart
	 * @return
	 */
	public static boolean ManualMovieMatch(Object mediafile, Object scrapperid, Object download_fanart){
            return ortus.onlinescrapper.api.ManualMovieMatch(mediafile, scrapperid, download_fanart);
        }

	/**
	 *
	 * @return
	 */
	public static boolean IsManualScanRunning() {
            return ortus.onlinescrapper.api.manual_index_running;
        }

	/**
	 *
	 * @return
	 */
	public static ScanType GetScanType() {
		return ortus.onlinescrapper.api.GetScanType();
	}
	/**
	 *
	 */
	public static void cleanMedia() {
            ortus.onlinescrapper.api.cleanMedia();
            return;
        }
	/**
	 *
	 */
	public static void cleanMediaBG() {
	    Ortus.getInstance().getProcessServer().AddQueue("ortus.onlinescrapper.api.cleanMedia",null);
            return;
        }


	public static void GetTVDBServerTime() {
		ortus.onlinescrapper.api.GetTVDBServerTime();
	}
	
	public static void GetTVDBUpdates() {
		Ortus.getInstance().getProcessServer().AddQueue("ortus.onlinescrapper.api.GetTVDBUpdates",null);
	}
	/**
	 *
	 */
	public static void indexMediaRecordings() {
            if ( ! ortus.api.IsOrtusInitialized()) {
                DebugLog(LogLevel.Error,"indexMediaRecordings: Jumpted the gun");
                return;
            }

            if ( ! ortus.onlinescrapper.api.index_running )
		    Ortus.getInstance().getProcessServer().AddQueue("ortus.onlinescrapper.api.indexMediaRecordings",null);
//                new Thread(new ortus.daemonWorker("indexrecording"),"daemonWorker").start();
            else
                DebugLog(LogLevel.Error,"indexMedia: Already running");
            return;
        }

	/**
	 *
	 * @param fullscan
	 * @param downloadfanart
	 */
	public static void indexMedia(int fullscan, int downloadfanart) {
	    ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High, ortus.mq.vars.EvenType.Local, "FullScan", new Object[] { fullscan, downloadfanart } );
//	    ortus.Ortus.getInstance().getJMS().fireJMSServerMessage(EvenType.Server, "FullScan", new Object[] { fullscan, downloadfanart } );
            return;
        }
	/**
	 *
	 * @param fullscan
	 * @param downloadfanart
	 */
	public static void indexMediaQuick(int fullscan, int downloadfanart) {
                ortus.onlinescrapper.api.indexMediaQuick(ortus.onlinescrapper.api.METADATA_update, ortus.onlinescrapper.api.FANART_update);
            return;
        }

        /**
         * Build sql index from Wiz.bin data in the background
	 * @param metadata
	 * @param fanart
	 */
        public static void indexMediaBG(int metadata, int fanart) {

		if( Configuration.GetProperty("ortus/metadata/target","Server").equalsIgnoreCase("server"))
			ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High, ortus.mq.vars.EvenType.Server, "FullScan", new Object[] { metadata, fanart } );
		else
			ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High, ortus.mq.vars.EvenType.Local, "FullScan", new Object[] { metadata, fanart } );
//		ortus.Ortus.getInstance().getJMS().fireJMSServerMessage(EvenType.Server, "FullScan", new Object[] { metadata, fanart } );
           return;
        }

	/**
	 *
	 * @param metadata
	 * @param fanart
	 */
	public static void indexMediaLocal(int metadata, int fanart) {
            if ( ! (Boolean)ortus.process.executeRemote("ortus.onlinescrapper.api.IsindexMediaRunning",null) )
		    Ortus.getInstance().getProcessServer().AddQueue( "ortus.onlinescrapper.api.indexMediaAll", new Object[] { metadata, fanart});
           return;
       }

       /**
	*
	*/
       public static void GetMissingFanart() {
           if ( ! ortus.onlinescrapper.api.index_running )
		   Ortus.getInstance().getProcessServer().AddQueue("ortus.onlinescrapper.api.GetMissingFanart",null);
//                new Thread(new ortus.daemonWorker("missingfanart"),"daemonWorker").start();
           else
                DebugLog(LogLevel.Error,"indexMedia: Already running");
            return;
        }

       /**
	*
	*/
       public static void GetScanFanart() {
		Ortus.getInstance().getProcessServer().AddQueue("ortus.onlinescrapper.api.GetScanFanart",null);
//		new Thread(new ortus.daemonWorker("scanfanart"), "daemonWorkder").start();
	}

        /**
         * Check if indexMedia process is running
         * @return
         */
        public static boolean IsindexMediaRunning() {
		return ortus.onlinescrapper.api.IsindexMediaRunning();
        }

        /**
         * Cancel currently running scan
         */
        public static void CancelIndexMedia() {
			ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High, ortus.mq.vars.EvenType.Broadcast, "CancelScan", new Object[] { } );
//			xortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.CancelIndexMedia"});
        }
	/**
	 * Get scapper detail
	 * @return array
	 */
	public static HashMap<String,String> GetScrapperDetail() {
			return (HashMap)ortus.process.executeRemote("ortus.onlinescrapper.api.GetScrapperDetail",null);
	}
        /**
         * Get the queue size of the current media index scan
         * @return queue size
         */
        public static int GetScrapperQueueSize() {
		return ortus.onlinescrapper.api.GetScrapperQueueSize();
//		return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperQueueSize"});
        }
        /**
         * Get the number of media items processed by the background scanning process
         * @return
	*/
        public static int GetScrapperProcessed() {
		return ortus.onlinescrapper.api.GetScrapperProcessed();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperProcessed"});
        }
        /**
         * Return the percentage completed for a background scan
         * @return Percentage
         */
        public static int GetScrapperPercentComplete() {
		return ortus.onlinescrapper.api.GetScrapperPercentComplete();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperPercentComplete"});
	}
	/**
         * Return the percentage of matched media items
         * @return Percentage
         */
        public static int GetScrapperPercentMatched() {
		return ortus.onlinescrapper.api.GetScrapperPercentMatched();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.online.scrapper.api.GetScrapperPercentMatched"});
        }
        /**
         * Return the percentage of matched media items
         * @return Percentage
         */
        public static int GetScrapperPercentMatchedLibrary() {
		return ortus.onlinescrapper.api.GetScrapperPercentMatchedLibrary();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperPercentMatchedLibrary"});
	}

        /**
         * Return the percentage of missed media items
         * @return
         */
       public static int GetScrapperPercentMissed() {
	       return ortus.onlinescrapper.api.GetScrapperPercentMissed();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperPercentMissed"});
        }
        /**
         * Return the percentage of matched media items
         * @return Percentage
         */
        public static int GetScrapperPercentMissedLibrary() {
		return ortus.onlinescrapper.api.GetScrapperPercentMissedLibrary();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperPercentMissedLibrary"});
        }

        /**
         * Get the current media id being processed by the background index scan
         * @return
         */
        public static int GetScrapperCurrent() {
		return ortus.onlinescrapper.api.GetScrapperCurrent();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperCurrent"});
        }
        /**
         * Get the number of matched items in the background index scan
         * @return
         */
        public static int GetScrapperMatched() {
		return ortus.onlinescrapper.api.GetScrapperMatched();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperMatched"});
	}
	/**
	 * Return number of items that matched from a property file
	 *
	 * @return
	 */
	public static int GetScrapperMatchedProperty() {
		return ortus.onlinescrapper.api.GetScrapperMatchedProperty();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperMatchedProperty"});
	}
        /**
         * Return total media items bypassed in background index scan
         * @return
         */
        public static int GetScrapperBypassed() {
		return ortus.onlinescrapper.api.GetScrapperBypassed();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperBypassed"});
        }
        /**
         * Get the number of missed items in the background index scan
         * @return
         */
        public static int GetScrapperMissed() {
		return ortus.onlinescrapper.api.GetScrapperMissed();
//			return (Integer)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperMissed"});
        }
        /**
         * Return the start time of background index scan
         * @return
         */
        public static long GetScrapperStartTime() {
		return ortus.onlinescrapper.api.GetScrapperStartTime();
//			return (Long)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperStartTime"});
        }
        /**
         * Return the end of the last background index scan
         * @return
         */
        public static long GetScrapperEndTime() {
		return ortus.onlinescrapper.api.GetScrapperEndTime();
//			return (Long)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperEndTime"});
        }
        /**
         * Return the number of minutes the background index scan has been running
         * @return
         */
        public static String GetScrapperElapsedMinutes() {
		return ortus.onlinescrapper.api.GetScrapperElapsedMinutes();
//			return (String)ortus.process.executeRemote(new Object[] { "ortus.onlinescrapper.api.GetScrapperElapsedMinutes"});
	}
        /**
         * Create TVDB Series Tables
         * @return
         */
        public static boolean createDB() {
            return Ortus.getInstance().getDB().createDB();
        }
        /**
         * Run backup of database
         * @return
         */
        public static boolean backupDB() {
            return Ortus.getInstance().getDB().backupDB();
        }
	/**
	 *
	 * @return
	 */
	public static List<Object> GetDBStatus() {
		return Ortus.getInstance().getDB().GetStatus();
	}

//ortus.Metadata
	/**
	 *
	 * @return
	 */
	public static String GetMediaCacheToString() {
		return ortus.cache.cacheEngine.getInstance().toString();
	}
	/**
	 *
	 */
	public static void PreLoadCache() {
		ortus.cache.cacheEngine.getInstance().PreLoadCache();
	}
	/**
	 *
	 * @return
	 */
	public static String GetMediaCacheStats() {
		return ortus.cache.cacheEngine.getInstance().getProvider().GetStats();
	}

	public static void RegisterMetadataProvider(String providername, IMetadataProvider provider) {
		metadataEngine.getInstance().RegisterMetadataProvider(providername, provider);
	}


	public static void UnRegisterMetadataProvider(String providername) {
		metadataEngine.getInstance().UnRegisterMetadataProvider(providername);
	}

	public static void SetMetadataProvider(String providername) {
		metadataEngine.getInstance().SetMetadataProvider(providername);
	}

	public static void ResetMetadataProvider() {
		metadataEngine.getInstance().SetMetadataProvider("db");
	}

	public static String GetMetadataProvider() {
		return metadataEngine.getInstance().GetMetadataProvider();
	}

	public static Object[] GetMetadataProviders() {
		return metadataEngine.getInstance().GetMetadataProviders();
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetMediaDescription(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetDescription(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @param description
	 */
	public static void SetMediaDescription(Object mediafile, String description) {
		metadataEngine.getInstance().getProvider().SetDescription(mediafile, description);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetMediaTitle(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetMediaTitle(mediafile);
	}
	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetMediaFileID(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetMediaFileID(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static long GetOriginalAirDate(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetOriginalAirDate(mediafile);
	}

        public static boolean IsOrtusMediaObject(Object mediafile) {
            return metadataEngine.getInstance().getProvider().IsOrtusMediaObject(mediafile);
        }

        public static Object GetMediaForOrtusMedia(Object mediafile) {
            return metadataEngine.getInstance().getProvider().GetMediaForOrtusMedia(mediafile);
        }

        public static long GetShowDuration(Object mediafile) {
            return metadataEngine.getInstance().getProvider().GetShowDuration(mediafile);
        }

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetShowTitle(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetShowTitle(mediafile);
	}

        public static String GetAllmediaData(Object mediafile, String column) {
            return metadataEngine.getInstance().getProvider().GetAllmediaData(mediafile, column);

        }

        public static HashMap GetMetadata(Object mediafile) {
            return metadataEngine.getInstance().getProvider().GetMetadata(mediafile);
        }
        public static HashMap GetMetadataFull(Object mediafile) {
            return metadataEngine.getInstance().getProvider().GetMetadataFull(mediafile);
        }


        public static OrtusMedia GetOrtusMediaSeries(Object mediafile) {
            if ( mediafile instanceof Integer) {
                return new OrtusMedia((Integer)mediafile,OrtusMediaType.Series);
            } else if ( mediafile instanceof OrtusMedia) {
                if (((OrtusMedia)mediafile).IsEpisode()) {
                    return new OrtusMedia(((OrtusMedia)mediafile).GetID(), OrtusMediaType.Series);
                } else
                    return null;
            } else 
                return null;
        }
        
        public static HashMap GetEpisodeInfo(Object mediafile) {
            return metadataEngine.getInstance().getProvider().GetEpisodeInfo(mediafile);
        }
        public static HashMap GetMediaInfo(Object mediafile) {
            return metadataEngine.getInstance().getProvider().GetMediaInfo(mediafile);
        }
        public static HashMap GetSeriesInfo(Object mediafile) {
            return metadataEngine.getInstance().getProvider().GetSeriesInfo(mediafile);
        }
        
        public static String GetSeriesData(Object mediafile, String column) {
            return metadataEngine.getInstance().getProvider().GetSeriesData(mediafile, column);
        }
        public static String GetEpisodeData(Object mediafile, String column) {
            return metadataEngine.getInstance().getProvider().GetEpisodeData(mediafile, column);
        }
	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetEpisodeTitle(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetEpisodeTitle(mediafile);
	}
	public static String GetEpisodeDescription(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetEpisodeDescription(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static int GetSeasonNumber(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetSeasonNumber(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static int GetEpisodeNumber(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetEpisodeNumber(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static int GetDiscNumber(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetDiscNumber(mediafile);
	}
	
	public static List<HashMap> GetMediaCast(Object mediafile, String job) {
		return metadataEngine.getInstance().getProvider().GetCast(mediafile, job);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static boolean IsTV(Object mediafile) {
		return metadataEngine.getInstance().getProvider().IsTV(mediafile);
	}

	/**
	 *
	 * @param mf
	 * @return
	 */
	public static boolean IsTVMovie(Object mf) {
		return metadataEngine.getInstance().getProvider().IsTVMovie(mf);
	}

	/**
	 *
	 * @param mf
	 * @return
	 */
	public static boolean IsImportedTV(Object mf) {
		return metadataEngine.getInstance().getProvider().IsImportedTV(mf);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static boolean IsRecorded(Object mediafile) {
		return metadataEngine.getInstance().getProvider().IsRecorded(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static boolean IsIntelligentRecorded(Object mediafile) {
		return metadataEngine.getInstance().getProvider().IsIntelligentRecorded(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static boolean IsSeries(Object mediafile) {
		return metadataEngine.getInstance().getProvider().IsSeries(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static boolean IsDVD(Object mediafile) {
		return metadataEngine.getInstance().getProvider().IsDVD(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static boolean IsBluRay(Object mediafile) {
		return metadataEngine.getInstance().getProvider().IsBluRay(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static boolean IsMovie(Object mediafile) {
		return metadataEngine.getInstance().getProvider().IsMovie(mediafile);
	}

        public static int GetSeriesID(Object mediafile) {
            return metadataEngine.getInstance().getProvider().GetSeriesID(mediafile);
        }
	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static int GetMediaType(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetMediaType(mediafile);
	}
	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetMediaPath(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetPath(mediafile);
	}

	public static String GetMediaReleaseDate(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetReleaseDate(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetMediaEncoding(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetMediaEncoding(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetMediaVideoEncoding(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetVideoEncoding(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetMediaAudioEncoding(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetAudioEncoding(mediafile);
	}

	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetMediaSubpicEncoding(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetSubpicEncoding(mediafile);
	}
	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static String GetMediaMPAARating(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetMPAARating(mediafile);
	}
	/**
	 *
	 * @param mediafile
	 * @return
	 */
	public static int GetMediaUserRating(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetUserRating(mediafile);
	}

        public static String GetMediaUserRatingString(Object mediaFile) {
                return metadataEngine.getInstance().getProvider().GetUserRatingString(mediaFile);
        }
        
	public static List<String> GetMediaGenre(Object mediafile) {
		return metadataEngine.getInstance().getProvider().GetGenre(mediafile);
	}
	/**
	 *
	 * @param mediafile
	 * @param mediatype
	 */
	public static void SetMediaType(Object mediafile, Object mediatype) {
		metadataEngine.getInstance().getProvider().SetMediaType(mediafile, mediatype);
	}

	public static void SetMediaTitle(Object mediafile, String newtitle) {
		metadataEngine.getInstance().getProvider().SetMediaTitle(mediafile, newtitle);
	}
//	public static void SetMediaDescription(Object mediafile, String newdescription) {
//		MetadataFactory.getInstance().getProvider().SetDescription(mediafile, newdescription);
//	}

	public static void SetMediaMPAARating(Object mediafile, String newmpaarating) {
		metadataEngine.getInstance().getProvider().SetMPAARating(mediafile, newmpaarating);
	}
	public static void SetMediaUserRating(Object mediafile, String newuserrating) {
		metadataEngine.getInstance().getProvider().SetUserRating(mediafile, newuserrating);
	}
	public static void SetMediaReleaseDate(Object mediafile, String newreleasedate) {
		metadataEngine.getInstance().getProvider().SetUserRating(mediafile, newreleasedate);
	}
//ortus.Search

	/**
	 *
	 * @param params
	 * @return
	 */
	public static Object Search(String params) {
            return metadataEngine.getInstance().getProvider().Search(params);
        }

	public static List<HashMap<String,String>> GetMediaSeries(Object seriesid, boolean allepisodes, Object seasonno) {
		return metadataEngine.getInstance().getProvider().GetSeries(seriesid, allepisodes, seasonno);
	}

	public static List<HashMap> GetMusicByArtist(String filter) {
		return metadataEngine.getInstance().getProvider().GetMusicByArtist(filter);
	}

	public static List<HashMap> GetMusicByAlbum(String filter) {
		return metadataEngine.getInstance().getProvider().GetMusicByAlbum(filter);
	}

	public static List<Object> GetMusicBySong(String filter) {
		return metadataEngine.getInstance().getProvider().GetMusicBySong(filter);
	}
//        public List<Object> GetMediaCast(String params) {
//            return MetadataFactory.getInstance().getProvider().GetMediaCast(params);
//        }
//        public List<Object> GetMediaGenre(String params) {
//            return MetadataFactory.getInstance().getProvider().GetMediaGenre(params);
//       }

//ortus.media.TVFavorite
	public static boolean IsFavorteComskipped(Object fav) {
		return ortus.media.TVFavorite.IsFavoriteComskipped(fav);
	}

	public static boolean IsMediaFileComskipped(Object mf) {
		return ortus.media.TVFavorite.IsMediaFileComskipped(mf);
	}

//ortus.media.TV

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetShowTitleClean(Object MediaObject) {
		return ortus.media.TV.GetShowTitleClean(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetShowTitleCleanPostpend(Object MediaObject) {
		return ortus.media.TV.GetShowTitleCleanPostpend(MediaObject);
	}

	/**
	 *
	 * @param mo
	 * @return
	 */
	public static String GetShowTitleOrMovie(Object mo) {
		return ortus.media.TV.GetShowTitleOrMovie(mo);
	}
	

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetEpisodeTitleClean(Object MediaObject) {
		return ortus.media.TV.GetEpisodeTitleClean(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetEpisodeTitleCleanPostpend(Object MediaObject) {
		return ortus.media.TV.GetEpisodeTitleCleanPostpend(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetSeasonNumberStr(Object MediaObject) {
		return ortus.media.TV.GetSeasonNumberStr(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetSeasonNumberPad(Object MediaObject) {
		return ortus.media.TV.GetSeasonNumberPad(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetEpisodeNumberStr(Object MediaObject) {
		return ortus.media.TV.GetEpisodeNumberStr(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetEpisodeNumberPad(Object MediaObject) {
		return ortus.media.TV.GetEpisodeNumberPad(MediaObject);
	}

		/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetDiscNumberStr(Object MediaObject) {
		return ortus.media.TV.GetDiscNumberStr(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetDiscNumberPad(Object MediaObject) {
		return ortus.media.TV.GetDiscNumberPad(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static int GetSeasonEpisodeNumber(Object MediaObject) {
		return ortus.media.TV.GetSeasonEpisodeNumber(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static int GetSeasonDiscNumber(Object MediaObject) {
		return ortus.media.TV.GetSeasonDiscNumber(MediaObject);
	}

        public static String GetSeriesDescription(Object MediaObject){
                return metadataEngine.getInstance().getProvider().GetSeriesDescription(MediaObject);
        }
        public static String GetSeriesTitle(Object MediaObject){
                return metadataEngine.getInstance().getProvider().GetSeriesTitle(MediaObject);
        }
        public static String GetSeriesNetwork(Object MediaObject){
                return metadataEngine.getInstance().getProvider().GetSeriesNetwork(MediaObject);
        }
        public static long GetSeriesFirstAirDate(Object MediaObject){
                return metadataEngine.getInstance().getProvider().GetSeriesFinalAirDate(MediaObject);
        }



	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static boolean IsWatchedPartial(Object MediaObject) {
		return ortus.media.TV.IsWatchedPartial(MediaObject);
	}

	/**
	 *
	 * @param MediaObjects
	 * @return
	 */
	public static Object GetLastWatched(Object MediaObjects) {
		return ortus.media.TV.GetLastWatched(MediaObjects);
	}

	/**
	 *
	 * @param MediaObjects
	 * @return
	 */
	public static Object GetNextShow(Object MediaObjects) {
		return ortus.media.TV.GetNextShow(MediaObjects);
	}

	/**
	 *
	 * @param MediaObjects
	 * @return
	 */
	public static Object[] GetShowsFromLastWatched(Object MediaObjects) {
		return ortus.media.TV.GetShowsFromLastWatched(MediaObjects);
	}

	/**
	 *
	 * @param MediaObjects
	 * @param MediaObject
	 * @return
	 */
	public static Object[] GetShowsFromShow(Object MediaObjects, Object MediaObject) {
		return ortus.media.TV.GetShowsFromShow(MediaObjects, MediaObject);
	}

	public static void SetWatched(Object mfs){
		ortus.media.TV.SetWatched(mfs);
		return;
	}

	public static void ClearWatched(Object mfs){
		ortus.media.TV.ClearWatched(mfs);
		return;
	}

	public static void SetDontLike(Object mfs){
		ortus.media.TV.SetDontLike(mfs);
		return;
	}

	public static void ClearDontLike(Object mfs){
		ortus.media.TV.ClearDontLike(mfs);
		return;
	}

	public static void Archive(Object mfs){
		ortus.media.TV.Archive(mfs);
		return;
	}

	public static void Unarchive(Object mfs){
		ortus.media.TV.Unarchive(mfs);
		return;
	}

	public static int GetWatchedCount(Object mfs){
		return ortus.media.TV.GetWatchedCount(mfs);
	}

	public static int GetDontLikeCount(Object mfs){
		return ortus.media.TV.GetDontLikeCount(mfs);
	}

	public static int GetArchiveCount(Object mfs){
		return ortus.media.TV.GetArchiveCount(mfs);
	}

	public static Object[] GetWatchedMediaFiles(Object mfs){
		return ortus.media.TV.GetWatchedMediaFiles(mfs);
	}

//ortus.media.util

	/**
	 *
	 * @param MediaObjects
	 * @param NewPlaylistName
	 * @return
	 */
	public static Object MakePlaylist(Object MediaObjects, String NewPlaylistName) {
		return ortus.media.util.MakePlaylist(MediaObjects, NewPlaylistName);
	}

	public static void DeleteMediaFiles(Object mfs){
		ortus.media.util.DeleteMediaFiles(mfs);
		return;
	}

//ortus.General

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetMediaTitleClean(Object MediaObject) {
		return ortus.media.General.GetMediaTitleClean(MediaObject);
	}

	/**
	 *
	 * @param MediaObject
	 * @return
	 */
	public static String GetMediaTitleCleanPostpend(Object MediaObject) {
		return ortus.media.General.GetMediaTitleCleanPostpend(MediaObject);
	}



}

