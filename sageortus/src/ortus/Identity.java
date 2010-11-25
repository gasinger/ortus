/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ortus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import ortus.mq.EventListener;
import ortus.vars.LogLevel;
import ortus.property.IProperty;
import ortus.property.OrtusDBProperty;
import ortus.ui.menu.menuEngine;
import sagex.UIContext;
import sagex.api.FavoriteAPI;
import sagex.api.MediaFileAPI;
import sagex.api.MediaPlayerAPI;

/**
 *
 * @author jphipps
 */
public class Identity extends EventListener {

	String ClientName;
	int CurrentUser;
	private IProperty userprops;
	private menuEngine MenuAPI;

	public Identity(String MAC) {
		super();
		ortus.api.DebugLog(LogLevel.Info, "Ortus Identity: Loading");

		if (ortus.api.GetSageProperty("ortus/clientname", null) == null) {
			Random ran = new Random();
			int rnum = ran.nextInt();
			ClientName = MAC + "-" + String.valueOf(rnum);
			ortus.api.SetSageProperty("ortus/clientname", ClientName);
			ortus.api.DebugLog(LogLevel.Debug, "Ortus: Identity generated client name: " + ClientName);
		} else {
			ClientName = ortus.api.GetSageProperty("ortus/clientname", null);
			ortus.api.DebugLog(LogLevel.Debug, "Ortus: Identity found " + ClientName + " in the properties file");
		}

		CurrentUser = Integer.parseInt(ortus.api.GetSageProperty("ortus/user/current", "0"));

		userprops = new OrtusDBProperty(CurrentUser);

		MenuAPI = new menuEngine(configurationEngine.getInstance().getConfigPath() + java.io.File.separator + CurrentUser);

		ortus.api.DebugLog(LogLevel.Info, "Ortus: Identity loaded for " + ClientName);
	}

	public String GetClientName() {
		return ClientName;
	}

	public void SetClientName(String clientname) {
		ClientName = clientname;
		ortus.api.GetSageProperty("ortus/clientname", ClientName);
	}

	public int GetCurrentUser() {
		return CurrentUser;
	}

	public Object GetCurrentUserName() {
			return ortus.api.executeSQLQuery("select username from sage.user where userid = " + CurrentUser).get(0);
	}

	public Object GetUserName(Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		
		return ortus.api.executeSQLQuery("select username from sage.user where userid = " + workuserid).get(0);
	}

	public void SetUserName(Object userid, Object username) {

		int rc = ortus.api.executeSQL("update sage.user set username = '" + username + "' where userid = " + userid);
	}


	public Object GetUserThumb(Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		return ortus.api.executeSQLQuery("select userthumb from sage.user where userid = " + workuserid).get(0);
	}

	public void SetCurrentUser(Object userid) {
		ortus.api.DebugLog(LogLevel.Trace2,"SetCurrentUser: Current: " + CurrentUser + " New User: " + userid);
		if (userid instanceof String) {
			CurrentUser = Integer.parseInt((String)userid);
		}
		if (userid instanceof Integer) {
			CurrentUser = (Integer) userid;
		}

		ortus.api.DebugLog(LogLevel.Trace,"SetCurrentUser: CurrentUser is now: " + CurrentUser);
		ortus.api.SetSageProperty("ortus/user/current", String.valueOf(CurrentUser));
		userprops.Reload(CurrentUser);
		MenuAPI.InitMenu(ortus.api.GetProperty("ortus/configpath") + java.io.File.separator + CurrentUser);
	}

	public IProperty GetUserProperty() {
		return userprops;
	}

	public List<Object> GetUsers() {
		return ortus.api.executeSQLQuery("select userid from sage.user");
	}

	public void AddUser(String username) {
		int userid = 0;
		List<List> result = ortus.api.executeSQLQueryArray("select max(userid) from sage.user");
		if (result.size() > 0) {
			userid = Integer.parseInt((String) result.get(0).get(0));
		}
		userid++;
		int rc = ortus.api.executeSQL("insert into sage.user ( userid, username, userthumb) values ( " + userid + " ,'" + username + "',null)");
	}

	public void RemoveUser(Object userid) {
		int workuser = 0;
		if (userid instanceof String) {
			workuser = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuser = (Integer) userid;
		}

		if (CurrentUser == workuser) {
			return;
		}

		int rc = ortus.api.executeSQL("delete from sage.user where userid = " + workuser);
		rc = ortus.api.executeSQL("delete from sage.properties where userid = " + workuser);
		rc = ortus.api.executeSQL("delete from sage.usermedia where userid = " + workuser);
		rc = ortus.api.executeSQL("delete from sage.userfavorites where userid = " + workuser);
	}

	public void SetUserThumb(Object userid, Object thumb) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		int rc = ortus.api.executeSQL("update sage.user set userthumb = '" + thumb + "' where userid = " + workuserid);
	}

	public void SetUserPin(Object userid, String userpin) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		int rc = ortus.api.executeSQL("update sage.user set userpin = '" + userpin + "' where userid = " + workuserid);
	}

	public void ClearUserPin(Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		int rc = ortus.api.executeSQL("update sage.user set userpin = null where userid = " + workuserid);
	}
	
	public String GetUserPin(Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		List<List> result = ortus.api.executeSQLQueryArray("select userpin from sage.user where userid = " + workuserid);
		if (result.size() > 0) {
			return (String) result.get(0).get(0);
		} else {
			return null;
		}
	}
	
	public menuEngine getMenu() {
		return MenuAPI;
	}

	public void SetUserWatchPosition(Map eventval) {
		ortus.api.DebugLog(LogLevel.Debug, "OrtusEvent: SetUserWatchPosition");
                if ((Integer)eventval.get("MediaFile") == 0) {
                    ortus.api.DebugLog(LogLevel.Info, "SetUserWatchPosition: Not storing, temp mediafile");
                    return;
                }
		UIContext UIC = new UIContext((String)eventval.get("UIContext"));
		Object mediafile = MediaFileAPI.GetMediaFileForID((Integer)eventval.get("MediaFile"));
		String SQL = "update sage.usermedia set LASTWATCHEDTIME = " + eventval.get("MediaTime") + " ,watched = true , lastwatchedtimestamp = current_timestamp";

		if ( MediaFileAPI.IsDVD(mediafile) || MediaFileAPI.IsBluRay(mediafile)) {
			SQL+= " , lastwatchedtitle = " + eventval.get("TitleNum") + ", lastwatchedtrack=" + eventval.get("ChapterNum");
			ortus.api.DebugLog(LogLevel.Trace2, "SetUserWatchPosition: for DVD/Bluray: " + MediaFileAPI.GetMediaFileID(mediafile) + " to Title: " + eventval.get("TitleNum") + " Chapter: " + eventval.get("ChapterNum") + " to position: " + eventval.get("MediaTime"));
		} else
			ortus.api.DebugLog(LogLevel.Trace2, "SetUserWatchPosition: for: " + MediaFileAPI.GetMediaFileID(mediafile) + " to position: " + eventval.get("MediaTime"));

		SQL+= " where userid = " + CurrentUser + " and mediaid = " + MediaFileAPI.GetMediaFileID(mediafile);
		ortus.api.DebugLog(LogLevel.Trace2, "SetUserWatchPosition: SQL: " + SQL);
		int rc = ortus.api.executeSQL(SQL);
		if (rc < 1) {
			SQL = "insert into sage.usermedia (userid, mediaid, LASTWATCHEDTIME, watched, lastwatchedtimestamp";
			if ( MediaFileAPI.IsDVD(mediafile) || MediaFileAPI.IsBluRay(mediafile)) {
				SQL += ",lastwatchedtitle, lastwatchedtrack) values( " + CurrentUser + "," + MediaFileAPI.GetMediaFileID(mediafile) + "," + eventval.get("MediaTime") + ", true, current_timestamp," + eventval.get("TitleNum") + "," + eventval.get("ChpaterNum") + ")";

			} else {
				SQL += ") values( " + CurrentUser + "," + MediaFileAPI.GetMediaFileID(mediafile) + "," + eventval.get("MediaTime") + ", true, current_timestamp)";
			}
			ortus.api.DebugLog(LogLevel.Trace2, "SetUserWatchPosition: SQL: " + SQL);
			rc = ortus.api.executeSQL(SQL);
		}		
	}

	public void SetUserWatchPosition(Object mediafile, long wtime) {
		String SQL = "update sage.usermedia set LASTWATCHEDTIME = " + wtime + " ,watched = true , lastwatchedtimestamp = current_timestamp";

		if ( MediaFileAPI.IsDVD(mediafile) || MediaFileAPI.IsBluRay(mediafile)) {
			SQL+= " , lastwatchedtitle = " + MediaPlayerAPI.GetDVDCurrentTitle() + ", lastwatchedtrack=" + MediaPlayerAPI.GetDVDCurrentChapter();
			ortus.api.DebugLog(LogLevel.Trace2, "SetUserWatchPosition: for DVD/Bluray: " + MediaFileAPI.GetMediaFileID(mediafile) + " to Title: " + MediaPlayerAPI.GetDVDCurrentTitle() + " Chapter: " + MediaPlayerAPI.GetDVDCurrentChapter() + " to position: " + wtime);
		} else
			ortus.api.DebugLog(LogLevel.Trace2, "SetUserWatchPosition: for: " + MediaFileAPI.GetMediaFileID(mediafile) + " to position: " + wtime);

		SQL+= " where userid = " + CurrentUser + " and mediaid = " + MediaFileAPI.GetMediaFileID(mediafile);
		ortus.api.DebugLog(LogLevel.Trace2, "SetUserWatchPosition: SQL: " + SQL);
		int rc = ortus.api.executeSQL(SQL);
		if (rc < 1) {
			SQL = "insert into sage.usermedia (userid, mediaid, LASTWATCHEDTIME, watched, lastwatchedtimestamp";
			if ( MediaFileAPI.IsDVD(mediafile) || MediaFileAPI.IsBluRay(mediafile)) {			
				SQL += ",lastwatchedtitle, lastwatchedtrack) values( " + CurrentUser + "," + MediaFileAPI.GetMediaFileID(mediafile) + "," + wtime + ", true, current_timestamp," + MediaPlayerAPI.GetDVDCurrentTitle() + "," + MediaPlayerAPI.GetDVDCurrentChapter() + ")";

			} else {
				SQL += ") values( " + CurrentUser + "," + MediaFileAPI.GetMediaFileID(mediafile) + "," + wtime + ", true, current_timestamp)";
			}
			ortus.api.DebugLog(LogLevel.Trace2, "SetUserWatchPosition: SQL: " + SQL);
			rc = ortus.api.executeSQL(SQL);
		}
	}

	public void ClearUserWatchPosition(Object mediafile) {
		ClearUserWatchPosition(mediafile,CurrentUser);
	}
	
	public void ClearUserWatchPosition(Object mediafile, Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}

		ortus.api.executeSQL("delete from sage.usermedia where mediaid = " + MediaFileAPI.GetMediaFileID(mediafile) + " and userid = " + workuserid);
	}

	public long GetUserWatchPosition(Object mediafile) {
		return GetUserWatchPosition(mediafile, CurrentUser);
	}

	public long GetUserWatchPosition(Object mediafile, Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		List<List> result = ortus.api.executeSQLQueryArray("select lastwatchedtime from sage.usermedia where userid = " + workuserid + " and mediaid = " + MediaFileAPI.GetMediaFileID(mediafile));
		if (result.size() > 0) {
			ortus.api.DebugLog(LogLevel.Trace2,"GetUserWatchPosition: User: " + workuserid + " position: " +Long.parseLong((String) result.get(0).get(0)));
			return Long.parseLong((String) result.get(0).get(0));
		} else {
			return 0;
		}

	}

	public long GetUserWatchTime(Object mediafile) {
		return GetUserWatchTime(mediafile,CurrentUser);
	}

	public long GetUserWatchTime(Object mediafile, Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		List<List> result = ortus.api.executeSQLQueryArray("select DateToEpoch(lastwatchedtimestamp) from sage.usermedia where userid = " + workuserid + " and mediaid = " + MediaFileAPI.GetMediaFileID(mediafile));
		if (result.size() > 0) {
			ortus.api.DebugLog(LogLevel.Trace2,"GetUserWatchPosition: User: " + workuserid + " date: " +Long.parseLong((String) result.get(0).get(0)));
			return Long.parseLong((String) result.get(0).get(0));
		} else {
			return 0;
		}
	}

	public int GetUserWatchTitle(Object mediafile) {
		return GetUserWatchTitle(mediafile,CurrentUser);
	}

	public int GetUserWatchTitle(Object mediafile, Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		List<List> result = ortus.api.executeSQLQueryArray("select lastwatchedtitle from sage.usermedia where userid = " + workuserid + " and mediaid = " + MediaFileAPI.GetMediaFileID(mediafile));
		if (result.size() > 0) {
			ortus.api.DebugLog(LogLevel.Trace2,"GetUserWatchTitle: User: " + workuserid + " position: " +Long.parseLong((String) result.get(0).get(0)));
			return Integer.parseInt((String) result.get(0).get(0));
		} else {
			return 0;
		}
	}

	public int GetUserWatchChapter(Object mediafile) {
		return GetUserWatchChapter(mediafile,CurrentUser);
	}

	public int GetUserWatchChapter(Object mediafile, Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		List<List> result = ortus.api.executeSQLQueryArray("select lastwatchedtrack from sage.usermedia where userid = " + workuserid + " and mediaid = " + MediaFileAPI.GetMediaFileID(mediafile));
		if (result.size() > 0) {
			ortus.api.DebugLog(LogLevel.Trace2,"GetUserWatchChapter: User: " + workuserid + " position: " +Long.parseLong((String) result.get(0).get(0)));
			return Integer.parseInt((String) result.get(0).get(0));
		} else {
			return 0;
		}
	}

	public boolean IsUserWatched(Object mediafile) {
		return IsUserWatched(mediafile,CurrentUser);
	}

	public boolean IsUserWatched(Object mediafile, Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		List<Object> result = ortus.api.executeSQLQuery("select watched from sage.usermedia where userid = " + workuserid + " and mediaid = " + MediaFileAPI.GetMediaFileID(mediafile));
//		ortus.api.DebugLog(LogLevel.Trace2, "IsUserWatched: User: " + userid + " " + result);
		if( result.size() > 0 ) {
			if (result.get(0).equals("TRUE")) {
				return true;
			} else {
				return false;
			}
		} else
			return false;
	}

	public void ClearUserWatched(Object mediafile) {
		ClearUserWatched(mediafile,CurrentUser);
	}

	public void ClearUserWatched(Object mediafile, Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		int rc = ortus.api.executeSQL("update sage.usermedia set watched = false where userid = " + workuserid + " and mediaid = " + MediaFileAPI.GetMediaFileID(mediafile));
		if ( rc < 1)
			rc = ortus.api.executeSQL("insert into sage.usermedia ( userid, mediaid, watched, lastwatchedtime) values (" + workuserid + "," + MediaFileAPI.GetMediaFileID(mediafile) + ",false,0)");

	}
	public void SetUserWatched(Object mediafile) {
		SetUserWatched(mediafile,CurrentUser);
	}

	public void SetUserWatched(Object mediafile, Object userid) {
		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		int rc = ortus.api.executeSQL("update sage.usermedia set watched = true where userid = " + workuserid + " and mediaid = " + MediaFileAPI.GetMediaFileID(mediafile));
		if ( rc < 1)
			rc = ortus.api.executeSQL("insert into sage.usermedia ( userid, mediaid, watched, lastwatchedtime) values (" + workuserid + "," + MediaFileAPI.GetMediaFileID(mediafile) + ",true,0)");

	}

	public void SetUserFavorite(Object favid) {
		SetUserFavorite(favid,CurrentUser);
	}

	public void SetUserFavorite(Object favid, Object userid) {
		int workuserid = 0;
		int workfavid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}
		if (favid instanceof String) {
			workfavid = Integer.parseInt((String) favid);
		}
		if (favid instanceof Integer) {
			workuserid = (Integer) favid;
		}

		int rc = ortus.api.executeSQL("update sage.userfavorite set userid = " + workuserid + "  where favid = " + workfavid);
		if ( rc < 1)
			rc = ortus.api.executeSQL("insert into sage.userfavorite ( favorite, userid) values (" + workfavid + ","+ workuserid );

	}

	public int GetUserFavorite(Object favid) {
		int workfavid = 0;
		if (favid instanceof String) {
			workfavid = Integer.parseInt((String) favid);
		}
		if (favid instanceof Integer) {
			workfavid = (Integer) favid;
		}
		List<List> result = ortus.api.executeSQLQueryArray("select userid from sage.userfavorite where favorite = " + workfavid);
		if (result.size() > 0) {
			return Integer.parseInt((String) result.get(0).get(0));
		} else {
			return 0;
		}
		
	}

	public Object[] GetUserFavorites(Object userid) {
		List<Object> userfavs = new ArrayList<Object>();

		int workuserid = 0;
		if (userid instanceof String) {
			workuserid = Integer.parseInt((String) userid);
		}
		if (userid instanceof Integer) {
			workuserid = (Integer) userid;
		}

		Object[] favs = FavoriteAPI.GetFavorites();
		for ( Object o : favs ) {
			if ( GetUserFavorite(FavoriteAPI.GetFavoriteID(o)) == workuserid)
				userfavs.add(o);
		}

		return userfavs.toArray();
	}
}

