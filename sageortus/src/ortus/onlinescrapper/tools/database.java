/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.tools;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringEscapeUtils;
import ortus.onlinescrapper.MediaObject;
import ortus.onlinescrapper.themoviedb.Movie;
import ortus.onlinescrapper.themoviedb.SearchResult;
import ortus.onlinescrapper.thetvdb.Actor;
import ortus.onlinescrapper.thetvdb.Episode;
import ortus.onlinescrapper.thetvdb.Series;
import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

/**
 *
 * @author jphipps
 */
public class database extends ortus.vars {
//    public static void LogFind(int scantype, int mediaid, String searchtitle, HashMap<String,SearchResult> resultTitles) {
//        ortus.api.executeSQL("delete from sage.scrapperlog where scantype = " + scantype + " and mediaid = " + mediaid);
//         Object[] mk = resultTitles.keySet().toArray();
//        for ( Object mt : mk ) {
//            ortus.api.executeSQL("insert into sage.scrapperlog (scantype, mediaid, searchtitle, foundtitle, foundkey, scandate) values( " + scantype + "," + mediaid + ",'" + StringEscapeUtils.escapeSql(searchtitle) + "','" + StringEscapeUtils.escapeSql((String)mt) + "','" + StringEscapeUtils.escapeSql(resultTitles.get(mt).getMetadatakey()) + "',current_timestamp)");
//        }
//
//        if ( mk.length == 0) {
//            ortus.api.executeSQL("insert into sage.scrapperlog (scantype, mediaid, searchtitle, foundtitle, foundkey, scandate) values( " + scantype + "," + mediaid + ",'" + StringEscapeUtils.escapeSql(searchtitle) + "','None Found','None Found',current_timestamp)");
//        }
//    }
//
//    public static Movie GetCacheMetadataIMDB(String imdbid) {
//        return GetCacheMetadata("imdbid",imdbid);
//    }
//
//    public static Movie GetCacheMetadataTMDB(String tmdbid) {
//        return GetCacheMetadata("tmdbid",tmdbid);
//    }
//
    public static HashMap GetCacheMetadata(String source, MediaObject mo) {
        ortus.api.DebugLog(LogLevel.Trace2,"GetCacheMetadata: Searching cache for type: " + source + " title: " + mo.getShowtitle().toLowerCase());
        Connection conn = ortus.api.GetConnection();
        String sql = "select title, id from sage.metadatacache where source = ? and match_title = ?";

        HashMap result = new HashMap();

        try {
          PreparedStatement stmt = conn.prepareStatement(sql);
          stmt.setString(1,source);
          stmt.setString(2,mo.getShowtitle().toLowerCase());
          ResultSet rs = stmt.executeQuery();
          if  ( rs.next() ) {
              result.put("title",rs.getString(1));
              result.put("id",rs.getString(2));
          }
          stmt.close();
	  conn.close();
          if ( result.get("title") != null )
              ortus.api.DebugLog(LogLevel.Trace2," GetMetadataCache: Found: key: " + result.get("id") + " in cache");
          return result;
        } catch(Exception e ) {
	    try { conn.close(); } catch(Exception ex) {}
            ortus.api.DebugLog(LogLevel.Error,"GetCacheMetadata: SQL: " + sql);
            ortus.api.DebugLog( LogLevel.Error, "GetCacheMetadata: SQLException: " , e);
            return result;
        }
    }
    
    public static void cacheMetadata(String source, String match_title, String title, String id) {
	ortus.api.DebugLog(LogLevel.Trace2,"cacheMetadata:  caching source: " + source + " match_title: " + match_title.toLowerCase() + " title: " + title + " id: " + id);
        if ( match_title.equalsIgnoreCase(title))
            return;

        Connection conn = ortus.api.GetConnection();
        String sql = "insert into sage.metadatacache ( match_title, title, source, id ) values(?,?,?,?)";
        try {
          PreparedStatement stmt = conn.prepareStatement(sql);
          stmt.setString(1,match_title.toLowerCase());
          stmt.setString(2,title);
          stmt.setString(3,source);
          stmt.setString(4, id);
          stmt.execute();
          stmt.close();
	  conn.close();
        } catch(Exception e ) {
	    try { conn.close(); } catch(Exception ex) {}
            ortus.api.DebugLog(LogLevel.Error,"cacheMetadata: SQL: " + sql);
            ortus.api.DebugLog( LogLevel.Error, "cacheMetadata: SQLException: " , e);
            return;
        }
        return;
    }

    public static void WriteMediatoDB(MediaObject mo) {

       String name = StringEscapeUtils.escapeSql(mo.getShowtitle());
       int mfid = MediaFileAPI.GetMediaFileID(mo.getMedia());

       String runtime = String.valueOf(MediaFileAPI.GetFileDuration(mo.getMedia()));
       if ( runtime.isEmpty())
           runtime="0";

       String SQL ="UPDATE sage.media SET mediatitle='"+ name +"', episodetitle = '" + StringEscapeUtils.escapeSql(ShowAPI.GetShowEpisode(mo.getMedia())) + "', mediapath='" + StringEscapeUtils.escapeSql(MediaFileAPI.GetFileForSegment(mo.getMedia(),0).getAbsolutePath()) + "', mediaencoding = '" + MediaFileAPI.GetMediaFileFormatDescription(mo.getMedia()).trim() + "', mediasize = " + MediaFileAPI.GetSize(mo.getMedia()) + ", mediaduration = " + runtime + " , lastwatchedtime = " +
                AiringAPI.GetLatestWatchedTime(mo.getMedia()) + " , airingstarttime = " + AiringAPI.GetAiringStartTime(mo.getMedia()) + ", mediatype = " + mo.getMediaTypeInt() + ", mediagroup = " + mo.getMediaGroupInt() + "  WHERE mediaid = " + String.valueOf(mfid);
        int success = ortus.api.executeSQL(SQL);
        if(success < 1){
            SQL =  "INSERT INTO sage.media (mediaid, mediatitle, episodetitle, mediapath, mediaencoding, mediatype, mediagroup, mediasize, mediaduration, lastwatchedtime, airingstarttime, mediaimporttime) " +
                       " VALUES(" + mfid + ", '" + name + "','" + StringEscapeUtils.escapeSql(ShowAPI.GetShowEpisode(mo.getMedia())) + "','" + StringEscapeUtils.escapeSql(MediaFileAPI.GetFileForSegment(mo.getMedia(),0).getAbsolutePath()) + "','" + MediaFileAPI.GetMediaFileFormatDescription(mo.getMedia()).trim() + "'," + mo.getMediaTypeInt() + ", " + mo.getMediaGroupInt() + ", " + MediaFileAPI.GetSize(mo.getMedia()) +
                       ", " + MediaFileAPI.GetFileDuration(mo.getMedia()) + ", " + AiringAPI.GetLatestWatchedTime(mo.getMedia()) + "," + AiringAPI.GetAiringStartTime(mo.getMedia()) + ", current_timestamp)";
            ortus.api.executeSQL(SQL);
        }
    }

    public static void WriteEpisodetoDB(Episode Episode,String SeriesTitle){
        String Description =  StringEscapeUtils.escapeSql(Episode.getOverview());
        String EpisodeName = StringEscapeUtils.escapeSql(Episode.getEpisodeName());
        float workrating = 0;
        try { workrating = Float.parseFloat(Episode.getRating()); } catch(Exception e) {}
        if ( workrating == 0) workrating = 5;

        String SQL ="UPDATE sage.episode SET title ='" +EpisodeName +"',description ='" +
                Description +"',originalairdate=";
	if ( Episode.getFirstAired().isEmpty() || Episode.getFirstAired().equals("0000-00-00"))
		SQL+="'1900-01-01'";
	else
		SQL+="'" + Episode.getFirstAired()+"'";
	SQL+=",userrating='" +Episode.getRating() + "',seasonno='" + Episode.getSeasonNumber() +"',episodeno =" +Episode.getEpisodeNumber() +
                " WHERE seriesid = " + Episode.getSeriesId() + " and seasonid = " + Episode.getSeasonId() + " and episodeid = " + Episode.getId();
        int success = ortus.api.executeSQL(SQL);
        if(success < 1){
            SQL =  "INSERT INTO sage.episode (seriesid, episodeid, seasonid, episodeno,  title,description,originalairdate,userrating,seasonno )  " +
                    " VALUES(" + Episode.getSeriesId() + "," + Episode.getId() + ", "+ Episode.getSeasonId() + " , " + Episode.getEpisodeNumber() + ", '" + EpisodeName +"','" +
                    Description +"',";
	    if ( Episode.getFirstAired().isEmpty() || Episode.getFirstAired().equals("0000-00-00"))
			SQL+="'1900-01-01'";
	    else
		    SQL+="'" + Episode.getFirstAired() +"'";
	    SQL+="," + workrating + "," + Episode.getSeasonNumber() + ")";
	    ortus.api.executeSQL(SQL);
        }

        List<String> directors = Episode.getDirectors();
        for ( String x : directors) {
            ortus.api.executeSQL("insert into sage.seriescast ( seriesid, episodeid, name, job, character) values ( " + Episode.getSeriesId() + "," + Episode.getId() + ",'" + x.replaceAll("'","''") + "','Director','')");
        }
        List<String> writers = Episode.getWriters();
        for ( String x : writers) {
            ortus.api.executeSQL("insert into sage.seriescast ( seriesid, episodeid, name, job, character) values ( " + Episode.getSeriesId() + "," + Episode.getId() + ",'" + x.replaceAll("'","''") + "','Writer','')");
        }
        List<String> guests = Episode.getGuestStars();
        for ( String x : guests) {
            ortus.api.executeSQL("insert into sage.seriescast ( seriesid, episodeid, name, job, character) values ( " + Episode.getSeriesId() + "," + Episode.getId() + ",'" + x.replaceAll("'","''") + "','Guest Star','')");
        }

    }

    public static void UpdateEpisodeDB(Episode Episode){
        String Description =  StringEscapeUtils.escapeSql(Episode.getOverview());
        String EpisodeName = StringEscapeUtils.escapeSql(Episode.getEpisodeName());
        float workrating = 0;
        try { workrating = Float.parseFloat(Episode.getRating()); } catch(Exception e) {}
        if ( workrating == 0) workrating = 5;

        String SQL ="UPDATE sage.episode SET title ='" +EpisodeName +"',description ='" +
                Description +"',originalairdate=";
	if ( Episode.getFirstAired().isEmpty() || Episode.getFirstAired().equals("0000-00-00"))
		SQL+="'1900-01-01'";
	else
		SQL+="'" + Episode.getFirstAired()+"'";
	SQL+=",userrating=" +workrating + ",seasonno='" + Episode.getSeasonNumber() +"',episodeno =" +Episode.getEpisodeNumber() +
                " WHERE seriesid = " + Episode.getSeriesId() + " and seasonid = " + Episode.getSeasonId() + " and episodeid = " + Episode.getId();
        int success = ortus.api.executeSQL(SQL);

	if ( success < 1) {
		List<Object> result = ortus.api.executeSQLQuery("select seriesid from sage.series where seriesid = " + Episode.getSeriesId());

		if ( result.size() < 1) {
			ortus.api.DebugLog(LogLevel.Info, "UpdateEpisodeDB: Episodeid: " + Episode.getId() + " not found");
			return;
		}
	}
    
	if ( success < 1) {
	    ortus.api.DebugLog(LogLevel.Info, "UpdateEpisodeDB: Inserted new Episodeid: " + Episode.getId());

            SQL =  "INSERT INTO sage.episode (seriesid, episodeid, seasonid, episodeno,  title,description,originalairdate,userrating,seasonno )  " +
                    " VALUES(" + Episode.getSeriesId() + "," + Episode.getId() + ", "+ Episode.getSeasonId() + " , " + Episode.getEpisodeNumber() + ", '" + EpisodeName +"','" +
                    Description +"',";
	    if ( Episode.getFirstAired().isEmpty() || Episode.getFirstAired().equals("0000-00-00"))
			SQL+="'1900-01-01'";
	    else
		    SQL+="'" + Episode.getFirstAired() +"'";
	    SQL+="," + workrating + "," + Episode.getSeasonNumber() + ")";
	    ortus.api.executeSQL(SQL);
        } else {
		ortus.api.DebugLog(LogLevel.Info, "UpdateEpisodeDB: Updated Episodeid: " + Episode.getId());
	}

	success = ortus.api.executeSQL("delete from sage.seriescast where seriesid = " + Episode.getSeriesId() + " and episodeid = " + Episode.getId());
        List<String> directors = Episode.getDirectors();
        for ( String x : directors) {
            ortus.api.executeSQL("insert into sage.seriescast ( seriesid, episodeid, name, job, character) values ( " + Episode.getSeriesId() + "," + Episode.getId() + ",'" + StringEscapeUtils.escapeSql(x) + "','Director','')");
        }
        List<String> writers = Episode.getWriters();
        for ( String x : writers) {
            ortus.api.executeSQL("insert into sage.seriescast ( seriesid, episodeid, name, job, character) values ( " + Episode.getSeriesId() + "," + Episode.getId() + ",'" + StringEscapeUtils.escapeSql(x) + "','Writer','')");
        }
        List<String> guests = Episode.getGuestStars();
        for ( String x : guests) {
            ortus.api.executeSQL("insert into sage.seriescast ( seriesid, episodeid, name, job, character) values ( " + Episode.getSeriesId() + "," + Episode.getId() + ",'" + StringEscapeUtils.escapeSql(x) + "','Guest Star','')");
        }

    }
    public static void WriteSeriestoDB(Series Series, List<Actor> actors) {
        //get 's out of description
        String Description =  StringEscapeUtils.escapeSql(Series.getOverview());
        // get ' our of episode title
        String Name = StringEscapeUtils.escapeSql(Series.getSeriesName());
        float workrating = 0;
        try { workrating = Float.parseFloat(Series.getRating()); } catch(Exception e) {}
        if ( workrating == 0) workrating = 5;
        String runtime = Series.getRuntime();
        if ( runtime.isEmpty())
            runtime="0";
        String workfirstair = Series.getFirstAired();
        if( workfirstair.isEmpty() || workfirstair.equals("0000-00-00"))
            workfirstair="1900-01-01";

        String SQL ="UPDATE sage.series SET title ='" +Name +"',firstair='" +
                workfirstair +"',airday='" + Series.getAirsDayOfWeek() +"',status='" +Series.getStatus() +
                "',description='" + Description +"',network='" + StringEscapeUtils.escapeSql(Series.getNetwork()) +"',userrating=" + workrating +
                ",mpaarated='" + Series.getContentRating() +"',runtime=" + runtime+", imdbid = '" + Series.getImdbId() + "', zap2itid = '" + Series.getZap2ItId() + "', airtime = '" + Series.getAirsTime() + "' WHERE seriesid=" +Series.getId();
        int success = ortus.api.executeSQL(SQL);
        if(success < 1){
            SQL =  "INSERT INTO sage.series (seriesid,title,firstair,airday,status,description,network,userrating,mpaarated,runtime, imdbid, zap2itid, airtime) " +
                    "VALUES("+ Series.getId() +",'" +Name +"','" + workfirstair +"','" + Series.getAirsDayOfWeek() +"','" +Series.getStatus() +
                    "','" + Description +"','" + StringEscapeUtils.escapeSql(Series.getNetwork()) +"'," + workrating +
                    ",'" + Series.getContentRating() +"'," + runtime+", '" + Series.getImdbId() + "','" + Series.getZap2ItId() + "','" + Series.getAirsTime() + "')";
            ortus.api.executeSQL(SQL);
        }
        ortus.api.executeSQL("delete from sage.seriesgenre where seriesid = " + Series.getId());
        for ( String g : Series.getGenres()) {
            ortus.api.executeSQL("insert into sage.seriesgenre ( seriesid, name ) values ( " + Series.getId() + ",'" + g + "')");
        }
        ortus.api.executeSQL("delete from sage.seriescast where seriesid = " + Series.getId());
        for ( Actor a : actors) {
            ortus.api.executeSQL("insert into sage.seriescast ( seriesid, episodeid, personid, name, job, character) values ( " + Series.getId() + ", 0,"+ a.getId() +",'" + a.getName().replaceAll("'","''") + "','actor','" + a.getRole().replaceAll("'","''") + "')");
        }
    }

    public static void UpdateSeriesDB(Series Series) {
        //get 's out of description
        String Description =  StringEscapeUtils.escapeSql(Series.getOverview());
        // get ' our of episode title
        String Name = StringEscapeUtils.escapeSql(Series.getSeriesName());
        float workrating = 0;
        try { workrating = Float.parseFloat(Series.getRating()); } catch(Exception e) {}
        if ( workrating == 0) workrating = 5;
        String runtime = Series.getRuntime();
        if ( runtime.isEmpty())
            runtime="0";
        String workfirstair = Series.getFirstAired();
        if( workfirstair.isEmpty())
            workfirstair="1900-01-01";

        String SQL ="UPDATE sage.series SET title ='" +Name +"',firstair='" +
                workfirstair +"',airday='" + Series.getAirsDayOfWeek() +"',status='" +Series.getStatus() +
                "',description='" + Description +"',network='" +Series.getNetwork() +"',userrating=" + workrating +
                ",mpaarated='" + Series.getContentRating() +"',runtime=" + runtime+", imdbid = '" + Series.getImdbId() + "', zap2itid = '" + Series.getZap2ItId() + "', airtime = '" + Series.getAirsTime() + "' WHERE seriesid=" +Series.getId();
        int success = ortus.api.executeSQL(SQL);
	if ( success > 0)
		ortus.api.DebugLog(LogLevel.Info, "UpdateSeriesDB: Updated seriesid: " + Series.getId() + " title: " + Series.getSeriesName());
	else
		ortus.api.DebugLog(LogLevel.Trace2,"UpdateSeriesDB: Seriesid: " + Series.getId() + " not found");
    }

    public static void WriteTVFanart(int id, String metadataid, String resolution, String type, String url , String filename) {
        int width = 0;
        int height = 0;
        int imagetype = 0;

        String urlField = "high_url";
        String fileField = "high_file";
        String widthField = "high_width";
        String heightField = "high_height";
        String imageSizeField = "high_imagesize";

        if ( resolution.equalsIgnoreCase("low")) {
            urlField = "low_url";
            fileField = "low_file";
            widthField = "low_width";
            heightField = "low_height";
            imageSizeField = "low_imagesize";
        } else if ( resolution.equalsIgnoreCase("medium")) {
            urlField = "medium_url";
            fileField = "medium_file";
            widthField = "medium_width";
            heightField = "medium_height";
            imageSizeField = "medium_imagesize";
        }
        
        if ( filename != null) {
            HashMap<String,String> imginfo = ortus.image.util.GetImageInfo(ortus.api.GetFanartFolder() + java.io.File.separator + filename);

            if ( imginfo != null) {
                    width = Integer.parseInt(imginfo.get("width"));
                    height = Integer.parseInt(imginfo.get("height"));

                    if ( height < 200)
                            imagetype = 1;
                    if ( height < 600)
                            imagetype = 2;
                    if ( height > 599)
                            imagetype = 3;

                    ortus.api.DebugLog(LogLevel.Trace,"FanartImage: ImageType: " + imagetype + " Size: Width: " + width + " Height: " + height);
            }
        }

        long filesize = 0;
        File faf = new File(ortus.api.GetFanartFolder() + java.io.File.separator + filename);
        if ( faf.exists()) {
             filesize = faf.length();
        }

	String SQL = "update sage.fanart set " + widthField + "  = " + width + ", " + heightField + "=" + height + ", " + imageSizeField + " = " + filesize + "," + fileField + " = '" + StringEscapeUtils.escapeSql(filename) + "', " + urlField + " = '" + StringEscapeUtils.escapeSql(url) + "' where mediaid = " + id + " and metadataid = '" + metadataid + "' and idtype = 'SR' and type = '" + type + "'";
	int success = ortus.api.executeSQL(SQL);
	if ( success < 1) {
		SQL =  "INSERT INTO sage.fanart (" + widthField + "," + heightField + " , " + urlField + "," + fileField + "," + imageSizeField + ", metadataid, mediaid , idtype, type) VALUES("+ width + "," + height + ",'" + StringEscapeUtils.escapeSql(url) + "','" + StringEscapeUtils.escapeSql(filename) + "'," + filesize + ",'" + metadataid + "'," + id + ",'SR','" + type + "')";
		ortus.api.executeSQL(SQL);
	}
    }
    
    public static boolean UpdateEpisodeMediaID(MediaObject mo, Series series) {
        ortus.api.DebugLog(LogLevel.Trace2,"UpdateEpisodeMediaID: running");
        boolean titleset = true;
//        Pattern pattern = Pattern.compile("S(\\d+)E(\\d+)");
//        Matcher matcher = pattern.matcher(mo.getEpisodetitle());
//        int EpisodeNo = 0;
//        int SeasonNo = 0;
        List<List> mtv;
        String workSeriesTitle = StringEscapeUtils.escapeSql(mo.getShowtitle());
        String workEpisodeTitle = StringEscapeUtils.escapeSql(mo.getEpisodetitle());

        if ( ! mo.getEpisodeID().isEmpty()) {
//            	String SQL ="UPDATE sage.episode SET mediaid = " + MediaFileAPI.GetMediaFileID(mo.getMedia()) + " where episodeid = " + mo.getEpisodeID() + " and seriesid = " + mo.getSeriesID();
                String SQL = "delete from sage.episodemedia where mediaid = " + MediaFileAPI.GetMediaFileID(mo.getMedia()) + " and episodeid = " + mo.getEpisodeID();
		ortus.api.executeSQL(SQL);
              	SQL ="insert into sage.episodemedia (mediaid, episodeid) values( " + MediaFileAPI.GetMediaFileID(mo.getMedia()) + "," + mo.getEpisodeID() + ")";
		int success = ortus.api.executeSQL(SQL);
		if ( success > 0 ) {
//		    SQL = "update sage.media set mediatype = 3, mediatitle = '" + workSeriesTitle + "',episodetitle = '" + ((String)mtv.get(0).get(3)).replaceAll("'","''") + "' where mediaid = " + MediaFileAPI.GetMediaFileID(mo.getMedia());
//		    success = ortus.api.executeSQL(SQL);
//		    if ( success > 0 )
			mo.setEpisodetitle(mo.getEpisodetitle());
                        mo.setOverview(mo.getOverview());
			ortus.api.DebugLog(LogLevel.Trace2, "UpdateEpisodeMediaID: Successful");
//		    else
//			ortus.api.DebugLog(LogLevel.Trace2, "UpdateEpisodeMediaID: Failed media series set");
		} else
		    ortus.api.DebugLog(LogLevel.Trace2, "UpdateEpisodeMediaID: Fail");

                return true;
        }

        if ( ! mo.getSeasonno().isEmpty()) {
//            ortus.api.DebugLog(LogLevel.Trace2, " UpdateEpisodeID: matcher found: " + matcher.groupCount());
            ortus.api.DebugLog(LogLevel.Trace2, " Series found, title: " + mo.getShowtitle() + " Season:" +  mo.getSeasonno() + " Episode: " + mo.getEpisodeno());
            titleset = false;
//            SeasonNo = Integer.parseInt(matcher.group(1));
//            EpisodeNo = Integer.parseInt(matcher.group(2));
            String sql = "select e.episodeid, e.seasonno, s.seriesid, e.episodeno, e.title, e.description from sage.episode e, sage.series s where s.seriesid = e.seriesid and lower(s.title) = '" + workSeriesTitle.toLowerCase().trim() + "' and e.seasonno = " + mo.getSeasonno() + " and e.episodeno = " + mo.getEpisodeno();
            mtv = ortus.api.executeSQLQueryArray(sql);
        } else {
            mtv = ortus.api.executeSQLQueryArray("select e.episodeid, e.seasonno, s.seriesid, e.episodeno, e.title, e.description from sage.episode e, sage.series s where s.seriesid = e.seriesid and episodeid not = 999 and lower(s.title) = '" + workSeriesTitle.toLowerCase() + "' and lower(e.title) = '" + workEpisodeTitle.toLowerCase() + "'");
        }

        if ( mtv.size() < 1) {
		ortus.api.DebugLog(LogLevel.Trace2, " Series: " + mo.getShowtitle() + " Episode: " + mo.getEpisodetitle() + " not found" );
		if ( workEpisodeTitle.isEmpty())
			workEpisodeTitle = workSeriesTitle;
		String SQL = "delete from sage.customepisode where seriesid = " + series.getId() + " and  episodeid = 999 and mediaid = " + MediaFileAPI.GetMediaFileID(mo.getMedia());
		ortus.api.executeSQL(SQL);
                SQL = "delete from sage.metadata where mediaid = " + MediaFileAPI.GetMediaFileID(mo.getMedia());
		ortus.api.executeSQL(SQL);
		SQL =  "INSERT INTO sage.customepisode (seriesid, episodeid, seasonid, episodeno, mediaid, title,description,originalairdate,userrating,seasonno )  " +
		    " VALUES(" + series.getId() + ",999,0,999," + MediaFileAPI.GetMediaFileID(mo.getMedia()) + ",'" + StringEscapeUtils.escapeSql(workEpisodeTitle) +"','" + StringEscapeUtils.escapeSql(ShowAPI.GetShowDescription(mo.getMedia())) + "',";
		if ( ShowAPI.GetOriginalAiringDate(mo.getMedia()) == 0 )
			SQL+="'1900-01-01'";
		else {
			String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date (ShowAPI.GetOriginalAiringDate(mo.getMedia())));
                        if ( date.equals("0000-00-00"))
                            date="1900-01-01";
			SQL+="'" + date +"'";
		}
		SQL+=",5,999)";
		ortus.api.DebugLog(LogLevel.Trace2," Adding dummy series: " + series.getId() + " with SQL: " + SQL);
		int success = ortus.api.executeSQL(SQL);
		if ( success > 0 ) {
//		    SQL = "update sage.media set mediatype = 3, mediatitle = '" + mo.getShowtitle() + "',episodetitle = '" + ShowAPI.GetShowEpisode(mo.getMedia()).replaceAll("'","''") + "' where mediaid = " + MediaFileAPI.GetMediaFileID(mo.getMedia());
//		    success = ortus.api.executeSQL(SQL);
//		    if ( success > 0 )
			ortus.api.DebugLog(LogLevel.Trace2, "UpdateEpisodeMediaID: Successful");
//		    else
//			ortus.api.DebugLog(LogLevel.Trace2, "UpdateEpisodeMediaID: Failed media series set");
		} else
			ortus.api.DebugLog(LogLevel.Trace2, "UpdateEpisodeMediaID: Failed media series set");

        } else {
                String SQL = "delete from sage.episodemedia where mediaid = " + MediaFileAPI.GetMediaFileID(mo.getMedia()) + " and episodeid = " + mtv.get(0).get(0);
		ortus.api.executeSQL(SQL);
              	SQL ="insert into sage.episodemedia (mediaid, episodeid) values( " + MediaFileAPI.GetMediaFileID(mo.getMedia()) + "," + mtv.get(0).get(0) + ")";
		int success = ortus.api.executeSQL(SQL);
		if ( success > 0 ) {
//		    SQL = "update sage.media set mediatype = 3, mediatitle = '" + workSeriesTitle + "',episodetitle = '" + ((String)mtv.get(0).get(3)).replaceAll("'","''") + "' where mediaid = " + MediaFileAPI.GetMediaFileID(mo.getMedia());
//		    success = ortus.api.executeSQL(SQL);
//		    if ( success > 0 )
			mo.setEpisodetitle(mo.getEpisodetitle());
                        mo.setOverview(mo.getOverview());
			ortus.api.DebugLog(LogLevel.Trace2, "UpdateEpisodeMediaID: Successful");
//		    else
//			ortus.api.DebugLog(LogLevel.Trace2, "UpdateEpisodeMediaID: Failed media series set");
		} else
		    ortus.api.DebugLog(LogLevel.Trace2, "UpdateEpisodeMediaID: Fail");

                return true;
	}
	return true;
    }

//    public boolean WritePictureDB(Object mediafile) {
//
//        if ( name.isEmpty())
//            name = MediaFileAPI.GetMediaTitle(mediafile);
//        overview = overview.replaceAll("'","''");
//        name = name.replaceAll("'","''");
//	name = name.replaceAll("\"","");
//	episodetitle = episodetitle.replaceAll("'","''");
//	episodetitle = episodetitle.replaceAll("\"","");
//        int mfid = MediaFileAPI.GetMediaFileID(mediafile);
//
//        String SQL ="UPDATE sage.media SET mediatitle='"+ name +"', episodetitle = '" + episodetitle + "', mediatype=" + mediatype + ",mediagroup=" + mediagroup + ", mediapath='" + MediaFileAPI.GetFileForSegment(mediafile,0).getAbsolutePath().replaceAll("'","''") + "', mediaencoding = '" + MediaFileAPI.GetMediaFileFormatDescription(mediafile).trim() + "', mediasize = " + MediaFileAPI.GetSize(mediafile) + ", mediaduration = " + MediaFileAPI.GetFileDuration(mediafile) + " , lastwatchedtime = " +
//                AiringAPI.GetLatestWatchedTime(mediafile) + ", airingstarttime = " + AiringAPI.GetAiringStartTime(mediafile) + " WHERE mediaid = " + String.valueOf(mfid);
//        int success = ortus.api.executeSQL(SQL);
//        if(success < 1){
//            SQL =  "INSERT INTO sage.media (mediaid, mediatitle,mediapath, mediaencoding, mediatype, mediagroup, mediasize, mediaduration, lastwatchedtime, airingstarttime, mediaimporttime) " +
//                       " VALUES(" + mfid + ", '" + name + "','" + MediaFileAPI.GetFileForSegment(mediafile,0).getAbsolutePath().replaceAll("'","''") + "','" + MediaFileAPI.GetMediaFileFormatDescription(mediafile).trim() + "'," + mediatype + "," + mediagroup + ", " + MediaFileAPI.GetSize(mediafile) +
//                       ", " + MediaFileAPI.GetFileDuration(mediafile) + ", " + AiringAPI.GetLatestWatchedTime(mediafile) + "," + AiringAPI.GetAiringStartTime(mediafile) + ", current_timestamp)";
//            ortus.api.executeSQL(SQL);
//        }
//    } 
}