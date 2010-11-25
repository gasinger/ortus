/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.metadata;

import java.io.File;
import ortus.media.metadata.item.Media;
import ortus.media.metadata.item.Series;
import ortus.media.metadata.item.Episode;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringEscapeUtils;
import ortus.configurationEngine;
import ortus.media.OrtusMedia;
import ortus.media.metadata.item.Fanart;
import ortus.media.metadata.item.IItem;
import sagex.api.Configuration;
import sagex.api.Database;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

/**
 *
 * @author jphipps
 */
public class DBMetadata extends ortus.vars implements IMetadataProvider {

    private String seperator = File.separator;
    private String CentralTVFolder = GetFanartFolder() + seperator + "TV" + seperator;

    public ortus.media.metadata.item.Media GetMetadataMedia(Object mediafile) {
        int mediaid = GetMediaID(mediafile);

        List<List> result = ortus.api.executeSQLQueryArray("select mediaid, showtitle, episodetitle, mediapath, description, mediaencoding, mediatype, mediagroup, airingstarttime, userrating, mpaarated, releasedate, mediaduration, seriesid, seasonno, episodeid, episodeno, mediasize, TimestampToEpoch(mediaimporttime) from allmedia where mediaid = " + mediaid);
        if (result.size() > 0) {
            try {
                float workRating = 5;
                if (result.get(0).get(9) != null) {
                    try {
                        workRating = Float.parseFloat(((String) result.get(0).get(9)).trim());
                    } catch (Exception e) {
                        ortus.api.DebugLog(5, "Couldn't convert: " + result.get(0).get(9));
                    }
                }
                String workRated = "";
                if (result.get(0).get(10) != null) {
                    workRated = (String) result.get(0).get(10);
                }
                ortus.media.metadata.item.Media ci = new Media(Integer.parseInt((String) result.get(0).get(0)), (String) result.get(0).get(1), (String) result.get(0).get(2), (String) result.get(0).get(3), (String) result.get(0).get(4), (String) result.get(0).get(5), Integer.parseInt((String) result.get(0).get(6)), Integer.parseInt((String) result.get(0).get(7)), Long.parseLong((String) result.get(0).get(8)), workRating, workRated, (String) result.get(0).get(11), Long.parseLong((String) result.get(0).get(12)), Long.parseLong((String)result.get(0).get(17)), Long.parseLong((String)result.get(0).get(18)));
                ci.setSeriesid(Integer.parseInt((String) result.get(0).get(13)));
                ci.setSeasonno(Integer.parseInt((String) result.get(0).get(14)));
                ci.setEpisodeid(Integer.parseInt((String) result.get(0).get(15)));
                ci.setEpisodeno(Integer.parseInt((String) result.get(0).get(16)));
                List<List> geresult;
                if (Integer.parseInt((String) result.get(0).get(6)) == 3) {
                    geresult = ortus.api.executeSQLQueryArray("select g.name from sage.seriesgenre as g, sage.episode as e where g.seriesid = e.seriesid and e.mediaid = " + Integer.parseInt((String) result.get(0).get(0)));
//                                                List<List> cast = ortus.api.executeSQLQueryArray("select ec.episodeid, ec.personid, ec.name, ec.job, ec.character from sage.episode as e, sage.seriescast as ec where e.seriesid = ec.seriesid and e.mediaid = " + Integer.parseInt((String)result.get(0).get(0)));
//                                                for ( List castitem : cast) {
//                                                    ((cacheItemMedia)ci).addCast(new cacheItemCast(Integer.parseInt((String)castitem.get(0)), Integer.parseInt((String)castitem.get(1)), (String)castitem.get(2),(String)castitem.get(3), (String)castitem.get(4)));
//                                                }
                } else {
                    List<List> misc = ortus.api.executeSQLQueryArray("select trailer from sage.movies where mediaid = " + mediaid);
                    if ( misc.size()>0)
                        ci.setTrailer((String)misc.get(0).get(0));
                    geresult = ortus.api.executeSQLQueryArray("select g.name from sage.genre as g, sage.movies as v where g.metadataid = v.metadataid and v.mediaid = " + Integer.parseInt((String) result.get(0).get(0)));
//                                                List<List> cast = ortus.api.executeSQLQueryArray("select 0, ec.personid, ec.name, ec.job, ec.character from sage.movie as m, sage.cast as ec where m.metadataid = ec.metadataid and m.mediaid = " + Integer.parseInt((String)result.get(0).get(0)));
//                                                for ( List castitem : cast) {
//                                                    ((cacheItemMedia)ci).addCast(new cacheItemCast(Integer.parseInt((String)castitem.get(0)), Integer.parseInt((String)castitem.get(1)), (String)castitem.get(2),(String)castitem.get(3), (String)castitem.get(4)));
//                                                }
                }
                for (List geitem : geresult) {
                    ci.AddGenre((String) geitem.get(0));
                }
                List<List> faresult = ortus.api.executeSQLQueryArray("select f.metadataid, f.default, f.height, f.width, f.imagetype, f.type, f.url, f.file, f.imagetype from sage.fanart as f, sage.movies as v where f.file is not null and f.file not like '%png' and f.metadataid = v.metadataid and v.mediaid = " + Integer.parseInt((String) result.get(0).get(0)) + " order by default desc");
                for (List faitem : faresult) {
                    ci.AddFanart(new Fanart((String) faitem.get(0), Integer.parseInt((String) faitem.get(1)), Integer.parseInt((String) faitem.get(2)), Integer.parseInt((String) faitem.get(3)), Integer.parseInt((String) faitem.get(4)), (String) faitem.get(5), (String) faitem.get(6), (String) faitem.get(7)));
                }
                return ci;
            } catch (Exception e) {
                ortus.api.DebugLog(LogLevel.Error, "GetMetadataMedia: Exception: ", e);
            }
        }

        return new Media();
    }

    public ortus.media.metadata.item.Series GetMetadataSeries(Object mediafile) {
        int seriesid = GetSeriesID(mediafile);
        ortus.media.metadata.item.Series ci = null;
        Connection conn = ortus.api.GetConnection();
        QueryRunner qr = new QueryRunner();
        try {
            List<Map<String, Object>> records = qr.query(conn, "select * from sage.series where seriesid = ?", seriesid, new MapListHandler());
            for (Map<String, Object> rec : records) {
                ci = new Series((Integer) rec.get("seriesid"), (String) rec.get("imdbid"), (String) rec.get("zap2itid"), (String) rec.get("title"), ((java.sql.Date) rec.get("firstair")).toString(), (String) rec.get("airday"), (String) rec.get("airtime"), (String) rec.get("status"), (String) rec.get("description"), (String) rec.get("network"), ((Double) rec.get("userrating")).floatValue(), (String) rec.get("mpaarated"), (Long) rec.get("runtime"));
                List<Map<String, Object>> genrerecords = qr.query(conn, "select * from sage.seriesgenre where seriesid = ?", seriesid, new MapListHandler());
                for (Map<String, Object> genrerec : genrerecords) {
                    ci.addGenre((String) genrerec.get("name"));
                }
//                            records = qr.query(conn,"select ec.episodeid, ec.personid, ec.name, ec.job, ec.character from sage.seriescast as ec where ec.seriesid  = ?", seriesid,new MapListHandler());
//                            for ( Map<String,Object> castrec : records) {
//                                ((cacheItemSeries)ci).addCast(new cacheItemCast(Integer.parseInt((String)castrec.get("episodeid")), Integer.parseInt((String)castrec.get("personid")), (String)castrec.get("name"),(String)castrec.get("job"),(String)castrec.get("character")));
//                            }

                List<Map<String, Object>> fanartrecords = qr.query(conn, "select * from sage.fanart where metadataid like ?", "SR" + seriesid + "%", new MapListHandler());
                for (Map<String, Object> fanartrec : fanartrecords) {
                    ci.AddFanart(new Fanart((String) fanartrec.get("metadataid"), (Integer) fanartrec.get("default"), (Integer) fanartrec.get("height"), (Integer) fanartrec.get("width"), (Integer) fanartrec.get("imagetype"), (String) fanartrec.get("type"), (String) fanartrec.get("url"), (String) fanartrec.get("file")));
                }
//                            icp.Put("SR"+seriesid, ci);
            }
            return ci;
        } catch (Exception e) {
            ortus.api.DebugLogError("GetCache: Series:", e);
        } finally {
            try {
                DbUtils.close(conn);
            } catch (Exception e) {
            }
        }

        return new ortus.media.metadata.item.Series();
    }

    public ortus.media.metadata.item.Episode GetMetadataEpisode(Object mediafile) {
        int episodeid = GetEpisodeID(mediafile);
        Connection conn = ortus.api.GetConnection();
        QueryRunner qr = new QueryRunner();
        try {
            List<Map<String, Object>> records = qr.query(conn, "select * from sage.episode where episodeid = ?", episodeid, new MapListHandler());
            if ( records.size() > 0) {
                Map<String, Object> rec = records.get(0);
                int workid = 0;
                if (rec.get("mediaid") != null) {
                    workid = (Integer) rec.get("mediaid");
                }
                ortus.media.metadata.item.Episode ci = new Episode((Integer) rec.get("episodeid"), (Integer) rec.get("seriesid"), (Integer) rec.get("episodeno"), (Integer) rec.get("seasonid"), (Integer) rec.get("seasonno"), workid, (String) rec.get("title"), (String) rec.get("description"), ((java.sql.Date) rec.get("originalairdate")).toString(), ((Double) rec.get("userrating")).floatValue(), (String) rec.get("thumbpath"));
                //                            records = qr.query(conn,"select ec.episodeid, ec.personid, ec.name, ec.job, ec.character from sage.seriescast as ec where ec.seriesid  = ?", ((cacheItemEpisode)ci).getSeriesid(),new MapListHandler());
                //                            for ( Map<String,Object> castrec : records) {
                //                                ((cacheItemEpisode)ci).addCast(new cacheItemCast(Integer.parseInt((String)castrec.get("episodeid")), Integer.parseInt((String)castrec.get("personid")), (String)castrec.get("name"),(String)castrec.get("job"),(String)castrec.get("character")));
                //                            }
                records = qr.query(conn, "select * from sage.fanart where type = ?", "Episode-" + episodeid + "-Posters", new MapListHandler());
                if (records.size() > 0) {
                    ((Episode) ci).setFanart((String) records.get(0).get("file"));
                }

                return ci;
            } 
        } catch (Exception e) {
            ortus.api.DebugLogError("GetMetadataEpisode: Series:", e);
        } finally {
            try {
                DbUtils.close(conn);
            } catch (Exception e) {
            }
        }

        return new Episode();
    }

    public int GetMediaID(Object mediafile) {
        int mediaid = 0;
        if (mediafile instanceof Integer) {
            mediaid = (Integer) mediafile;
        } else if ( mediafile instanceof String) {
            try { mediaid = Integer.parseInt((String)mediafile); } catch ( Exception e) {}
        } else if (mediafile instanceof OrtusMedia) {
            if (((OrtusMedia) mediafile).IsMediaFile() || ((OrtusMedia) mediafile).IsEpisode()) {
                mediaid = ((OrtusMedia) mediafile).GetMediaID();
            }
        } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
            mediaid = MediaFileAPI.GetMediaFileID(mediafile);
        }

        return mediaid;
    }

    public int GetEpisodeID(Object mediafile) {
        int episodeid = 0;
        int mediaid = 0;
        if (mediafile instanceof Integer) {
            episodeid = (Integer) mediafile;
        } else if (mediafile instanceof OrtusMedia) {
            if (((OrtusMedia) mediafile).IsMediaFile() || ((OrtusMedia) mediafile).IsEpisode()) {
                mediaid = ((OrtusMedia) mediafile).GetMediaID();
            } else if (((OrtusMedia) mediafile).IsEpisode()) {
                episodeid = ((OrtusMedia) mediafile).GetID();
            }
        } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
            mediaid = MediaFileAPI.GetMediaFileID(mediafile);
        }

        if (mediaid > 0) {
            List qr = ortus.api.executeSQLQuery("select episodeid from sage.episode where mediaid = " + mediaid);
            if (qr.size() > 0) {
                episodeid = Integer.parseInt((String) qr.get(0));
            }
        }

        return episodeid;
    }

    public int GetSeriesID(Object mediafile) {
        int seriesid = 0;
        int mediaid = 0;
        if (mediafile instanceof Integer) {
            seriesid = (Integer) mediafile;
        } else if (mediafile instanceof OrtusMedia) {
            if (((OrtusMedia) mediafile).IsMediaFile() || ((OrtusMedia) mediafile).IsEpisode()) {
                mediaid = ((OrtusMedia) mediafile).GetMediaID();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                seriesid = ((OrtusMedia) mediafile).GetID();
            }
        } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
            mediaid = MediaFileAPI.GetMediaFileID(mediafile);
        }

        if (mediaid > 0) {
            List qr = ortus.api.executeSQLQuery("select seriesid from sage.episode where mediaid = " + mediaid);
            if (qr.size() > 0) {
                seriesid = Integer.parseInt((String) qr.get(0));
            }
        }

        return seriesid;
    }

    public boolean IsMetadataKeyMedia(String key) {
        if ( key.startsWith("MD"))
            return true;
        else
            return false;
    }

    public boolean IsMetadataKeyEpisode(String key) {
        if ( key.startsWith("EP"))
            return true;
        else
            return false;
    }

    public boolean IsMetadataKeySeries(String key) {
        if ( key.startsWith("SR"))
            return true;
        else
            return false;
    }

    public int GetMetadataKeyValue(String key) {
        int result = 0;
        try {
            result = Integer.parseInt(key.substring(2));
        } catch(Exception e) {}
        return result;
    }

    public HashMap GetMetadata(Object mediafile) {
        HashMap dataHash = null;
//        ortus.api.DebugLogTrace("GetMetadata: Called with " + mediafile);
        if (mediafile instanceof OrtusMedia) {
            IItem ii = (IItem)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey());
            dataHash = ii.toHash();
        } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
            IItem ii = (IItem)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile));
            if (ii.isValid()) {
                dataHash = ii.toHash();
            } else {
                dataHash = new HashMap();
                dataHash.put("type", "sage");
                dataHash.put("mediaid", MediaFileAPI.GetMediaFileID(mediafile));
                dataHash.put("title", MediaFileAPI.GetMediaTitle(mediafile));
                dataHash.put("description", ShowAPI.GetShowDescription(mediafile));
                dataHash.put("size", MediaFileAPI.GetSize(mediafile));
                dataHash.put("runtime", MediaFileAPI.GetFileDuration(mediafile));
            }
        }

        if (dataHash == null) {
            ortus.api.DebugLogError("GetMetadata: Invalid Object");
        } else {
            if ( dataHash.get("mediaid") != null && dataHash.get("type").equals("episode")) {
                IItem ii = (IItem)ortus.cache.cacheEngine.getInstance().GetCache("MD" + dataHash.get("mediaid"));
                dataHash.put("mediainfo", ii.toHash());
            }
//            ortus.api.DebugLogTrace("GetMetadata: Return: " + dataHash);
        }
        return dataHash;
    }

    public HashMap GetMetadataFull(Object mediafile) {
        HashMap dataHash = null;
//        ortus.api.DebugLogTrace("GetMetadata: Called with " + mediafile);
        if (mediafile instanceof OrtusMedia) {
            IItem ii = (IItem)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey());
            dataHash = ii.toHashFull();
        } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
            IItem ii = (IItem)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile));
            if (ii.isValid()) {
                dataHash = ii.toHashFull();
            } else {
                dataHash = new HashMap();
                dataHash.put("type", "sage");
                dataHash.put("mediaid", MediaFileAPI.GetMediaFileID(mediafile));
                dataHash.put("title", MediaFileAPI.GetMediaTitle(mediafile));
                dataHash.put("description", ShowAPI.GetShowDescription(mediafile));
                dataHash.put("size", MediaFileAPI.GetSize(mediafile));
                dataHash.put("runtime", MediaFileAPI.GetFileDuration(mediafile));
            }
        }

        if (dataHash == null) {
            ortus.api.DebugLogError("GetMetadataFull: Invalid Object");
        } else {
            if ( dataHash.get("seriesid") != null)
                if ( ! ((String)dataHash.get("type")).equalsIgnoreCase("series")) {
                     IItem si = (IItem)ortus.cache.cacheEngine.getInstance().GetCache("SR" + String.valueOf(dataHash.get("seriesid")));
                     dataHash.put("seriesinfo", si.toHashFull());
                }
            if ( dataHash.get("mediaid") != null && dataHash.get("type").equals("episode")) {
                IItem ii = (IItem)ortus.cache.cacheEngine.getInstance().GetCache("MD" + dataHash.get("mediaid"));
                dataHash.put("mediainfo", ii.toHash());
            }

//            ortus.api.DebugLogTraxce("GetMetadataFull: Return: " + dataHash);
        }
        return dataHash;

    }

	public String GetFanartFolder() {

		String fanartfolder = Configuration.GetProperty("ortus/fanart/folder", "None");

		if (fanartfolder.equalsIgnoreCase("none")) {
			fanartfolder = configurationEngine.getInstance().getBasePath() + seperator + "Fanart";
			File df = new File(fanartfolder);
			if (!df.exists()) {
				df.mkdir();
			}
		}

		return fanartfolder;
	}

	public void SetFanartFolder(String folder) {

		Configuration.SetProperty("ortus/fanart/folder", folder);

		File df = new File(folder);
		if (!df.exists()) {
			df.mkdir();
		}

		return;
	}








    public long GetOriginalAirDate(Object mediafile) {
//        if ( mediafile instanceof OrtusMedia ) {
//            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
//                    return  ((cacheItemMedia)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetAiringStartTime();
//            } else if (((OrtusMedia) mediafile).IsSeries()) {
//                    return ((cacheItemSeries)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey()))..GetReleaseDate(mediafile);
//            } else if (((OrtusMedia)mediafile).IsEpisode()) {
//                    return ((cacheItemEpisode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getTitle();
//            } else
//                return "Not Available";
//        } else {
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetAiringStartTime();
//        }
    }

    public boolean IsOrtusMediaObject(Object mediafile) {
        if ( mediafile instanceof OrtusMedia)
            return true;
        else
            return false;
    }

    public Object GetMediaForOrtusMedia(Object mediafile) {
        if ( mediafile instanceof OrtusMedia) {
            if ( ((OrtusMedia)mediafile).IsMediaFile())
                return MediaFileAPI.GetMediaFileForID(((OrtusMedia)mediafile).GetID());
            else
                return null;
        } else 
            return null;
    }

    public HashMap GetMediaInfo(Object mediafile) {
        HashMap result = null;
        int mediaid = 0;
        if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile() || ((OrtusMedia)mediafile).IsEpisode())
                mediaid = ((OrtusMedia)mediafile).GetMediaID();
        } else
            mediaid = MediaFileAPI.GetMediaFileID(mediafile);

        if ( mediaid > 0) {
            Media cim = (Media) ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediaid);
            if ( cim != null)
                result = cim.toHash();
        }

        if ( result == null)
            result = new HashMap();

        return result;
    }


    public String GetAllmediaData(Object mediafile, String column) {
        int mediaid = 0;
        if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile() || ((OrtusMedia)mediafile).IsEpisode())
                mediaid = ((OrtusMedia)mediafile).GetMediaID();
        } else
            mediaid = MediaFileAPI.GetMediaFileID(mediafile);

        List<Object> result = ortus.api.executeSQLQuery("select " + column + " from allmedia where mediaid = " + mediaid);
        if ( result.size() > 0) {
            return (String)result.get(0);
        } else {
            return null;
        }
    }

    public long GetShowDuration(Object mediafile) {
        if ( mediafile instanceof OrtusMedia ) {
//            ortus.api.DebugLog(LogLevel.Debug, "GetShowDuration: found OrtusMedia");
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
//                ortus.api.DebugLog(LogLevel.Debug, "GetShowDuration: Found mediaobject, id = " + ((OrtusMedia)mediafile).GetID());
                return ShowAPI.GetShowDuration(MediaFileAPI.GetMediaFileForID(((OrtusMedia)mediafile).GetID()));
            }  else
                if ( ((OrtusMedia)mediafile).IsSeries()) {
//                    ortus.api.DebugLog(LogLevel.Debug, "GetShowDuration: Found seriesobject, id = " + ((OrtusMedia)mediafile).GetID());
                    List<Object> result = ortus.api.executeSQLQuery("select runtime from sage.series where seriesid = " + ((OrtusMedia)mediafile).GetID());
                    if ( result.size() > 0) {
                        return Long.parseLong((String)result.get(0))*60*1000;
                    } else {
                        return 0;
                    }
                } else if ( ((OrtusMedia)mediafile).IsEpisode()) {
//                    ortus.api.DebugLog(LogLevel.Debug, "GetShowDuration: Found episodeobject, id = " + ((OrtusMedia)mediafile).GetID());
                    List<Object> result = ortus.api.executeSQLQuery("select distinct(s.runtime) from sage.series as s, sage.episode as e where e.episodeid = " + ((OrtusMedia)mediafile).GetID() + " and e.seriesid = s.seriesid");
                    if ( result.size() > 0) {
                        return Long.parseLong((String)result.get(0))*60*1000;
                    } else {
                        return 0;
                    }

                } else
                    return 0;

        } else
            return ShowAPI.GetShowDuration(mediafile);
    }
//    public String GetMediaTitle(Object mediafile) {
//	    String mediatitle = cacheEngine.getInstance().GetCache(mediafile).GetMediaTitle();
//	    if ( mediatitle.isEmpty())
//		    mediatitle = MediaFileAPI.GetMediaTitle(mediafile);
//	    return  mediatitle;
////            List<Object> result = ortus.api.executeSQLQuery("select showtitle from allmedia where mediaid = " + MediaFileAPI.GetMediaFileID(mediafile));
////            if ( result.size() > 0) {
////                return (String)result.get(0);
////            } else {
////                return MediaFileAPI.GetMediaTitle(mediafile);
////            }
//    }
    public String GetMediaTitle(Object mediafile) {
        if ( mediafile instanceof OrtusMedia ) {
//            ortus.api.DebugLogInfo("GetMediaTitle: OrtusMedia");
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
//                    ortus.api.DebugLogInfo("GetMediaTitle: Media");
                    return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetMediaTitle();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
//                ortus.api.DebugLogInfo("GetMediaTitle: Series");
                    return ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getTitle();

            } else if (((OrtusMedia)mediafile).IsEpisode()) {
//                ortus.api.DebugLogInfo("GetMediaTitle: Episode");
                    return ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getTitle();
            } else
                return "Not Available";
        } else {
//            ortus.api.DebugLogInfo("GetMediaTitle: SageMedia");
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetMediaTitle();
        }
    }
    
    public void SetMediaTitle(Object mediafile,String title) {
            int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetMediaID() : MediaFileAPI.GetMediaFileID(mediafile));
	    title = StringEscapeUtils.escapeSql(title);
            ortus.api.executeSQL("update sage.media set mediatitle = '" + title + "' where mediaid = " + mediaid);
	    ortus.cache.cacheEngine.getInstance().ReLoadCache(mediafile);
    }

    public String GetShowTitle(Object mediafile) {
        if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetMediaTitle();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getTitle();
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                    return ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getTitle();
            } else
                return "Not Available";
        } else {
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetMediaTitle();
        }
    }

//    public String GetEpisodeTitle(Object mediafile) {
//        if ( mediafile instanceof OrtusMedia ) {
//            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
//                    return  ((cacheItemMedia)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetMediaTitle();
//            } else if (((OrtusMedia) mediafile).IsSeries()) {
//                    return ((cacheItemSeries)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getTitle();
//            } else if (((OrtusMedia)mediafile).IsEpisode()) {
//                    return ((cacheItemEpisode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getTitle();
//            } else
//                return "Not Available";
//        } else {
//            return ((cacheItemMedia)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetMediaTitle();
//        }
//    }
    public void SetEpisodeTitle(Object mediafile,String title) {
            int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetMediaID() : MediaFileAPI.GetMediaFileID(mediafile));
	    title = StringEscapeUtils.escapeSql(title);
            ortus.api.executeSQL("update sage.media set episodetitle = '" + title + "' where mediaid = " + mediaid);
	    ortus.cache.cacheEngine.getInstance().ReLoadCache(mediafile);
    }

    public int GetSeasonNumber(Object mediafile) {
           if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return 0;
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return 0;
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                    return ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getSeasonno();
            } else
                return 0;
        } else {
            return 0;
        }

    }

    public int GetEpisodeNumber(Object mediafile) {
           if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return 0;
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return 0;
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                    return ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getEpisodeno();
            } else
                return 0;
        } else {
            return 0;
        }
    }

    public String GetDescription(Object mediafile) {
       if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetDescription();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getDescription();
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                    return ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getDescription();
            } else
                return "Not Available";
        } else {
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetDescription();
        }
    }
    
    public void SetDescription(Object mediafile,String description) {
            int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetMediaID() : MediaFileAPI.GetMediaFileID(mediafile));
	    description = StringEscapeUtils.escapeSql(description);
            int result = ortus.api.executeSQL("update sage.mediavideos set description = '" + description + "' where mediaid = " + mediaid);
            if ( result < 1) {
                result = ortus.api.executeSQL("insert into sage.mediavideos (mediaid, description) values( " + mediaid + ",'" + description + "')");
            }
    }

    public List<String> GetGenre(Object mediafile) {
       if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetGenre();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getGenre();
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                    int seriesid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getSeriesid();
                    return ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getGenre();
            } else {
                List<String> result = new ArrayList<String>();
                result.add("None");
                return result;
            }
        } else {
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetGenre();
        }
    }
    
    public boolean IsTV(Object mediafile) {
       if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).IsTV();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return true;
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                 int mediaid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getMediaid();
                 if ( mediaid != 0)
                     return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediaid)).IsTV();
                 else
                    return false;
            } else
                return false;
        } else {
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).IsTV();
        }
    }

    public boolean IsRecording(Object mediafile) {
       if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).IsRecording();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return false;
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                 int mediaid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getMediaid();
                 if ( mediaid != 0)
                     return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediaid)).IsRecording();
                 else
                    return false;
            } else
                return false;
        } else {
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).IsRecording();
        }
    }


    public boolean IsMovie(Object mediafile) {
       if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).IsMovie();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return false;
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                 int mediaid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getMediaid();
                 if ( mediaid != 0)
                     return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediaid)).IsMovie();
                 else
                    return false;
            } else
                return false;
        } else {
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).IsMovie();
        }
    }

    public boolean IsHomeMovie(Object mediafile) {
            int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetID() : MediaFileAPI.GetMediaFileID(mediafile));
            List<Object> result = ortus.api.executeSQLQueryCache("select mediaid from allmedia where mediatype = 4 and mediaid = " + mediaid);
            if ( result.size() > 0)
                return true;
            else
                return false;
    }

    public boolean IsSeries(Object mediafile) {
            int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetID() : MediaFileAPI.GetMediaFileID(mediafile));
            List<Object> result = ortus.api.executeSQLQueryCache("select mediaid from allmedia where mediatype = 3 and mediaid = " + mediaid);
            if ( result.size() > 0)
                return true;
            else
                return false;
    }

    public boolean IsDVD(Object mediafile) {
        if ( mediafile instanceof OrtusMedia)
            return MediaFileAPI.IsDVD(((OrtusMedia)mediafile).Unwrap());
        else
            return MediaFileAPI.IsDVD(mediafile);
    }

    public boolean IsBluRay(Object mediafile) {
        if ( mediafile instanceof OrtusMedia)
            return MediaFileAPI.IsBluRay(((OrtusMedia)mediafile).Unwrap());
        else
            return MediaFileAPI.IsBluRay(mediafile);
    }

    public int GetMediaType(Object mediafile) {
          if ( mediafile instanceof OrtusMedia ) {
                if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetMediaType();
                } else if (((OrtusMedia) mediafile).IsSeries()) {
                        return 3;
                } else if (((OrtusMedia)mediafile).IsEpisode()) {
                     int mediaid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getMediaid();
                     return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediaid)).GetMediaType();
                } else
                    return 0;
            } else {
                return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetMediaType();
            }
    }

    public void SetMediaType(Object mediafile, Object mediatype) {
        int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetMediaID() : MediaFileAPI.GetMediaFileID(mediafile));
        int workmediatype = Integer.parseInt(mediatype.toString());
        if ( GetMediaType(mediafile) == 3 && workmediatype != 3) {
            int result = ortus.api.executeSQL("update sage.episode set mediaid = null where mediaid = " + mediaid);
        }

        int result = ortus.api.executeSQL("update sage.media set mediatype = " + workmediatype + " where mediaid = " + mediaid);
	ortus.cache.cacheEngine.getInstance().ReLoadCache(mediafile);
    }

    public List<Object> GetMovies() {
            return ortus.api.getMediaFilesSQL("select mediaid from allmedia where mediatype = 2");
    }

    public List<Object> GetRecordings() {
            return ortus.api.getMediaFilesSQL("select mediaid from allmedia where mediatype = 1 and mediagroup = 1");
    }

    public List<Object> GetSeries() {
            return ortus.api.getMediaFilesSQL("SELECT mediaid FROM ALLMEDIA where mediatype = 3");
    }

    public List<HashMap<String,String>> GetSeries(Object seriesid, boolean allepisodes, Object seasonno) {
	    List<HashMap<String,String>> retval = new ArrayList<HashMap<String,String>>();

	    String SQL = "select episodeid, mediaid from sage.episode where seriesid = " + seriesid;

	    if ( allepisodes == false)
		    SQL+=" and mediaid is not null";

	    if ( seasonno != null)
		    SQL+= " and seasonno = " + seasonno;

	    SQL+=" order by seasonno desc, episodeid desc";
	    ortus.api.DebugLog(LogLevel.Trace2,"GetSeries: SQL: " + SQL);
	    List<List> result = ortus.api.executeSQLQueryArray(SQL);

	    for ( List x : result) {
		    HashMap<String,String> y = new HashMap<String,String>();
		    y.put("episodeid",(String)x.get(0));
		    y.put("mediaid",(String)x.get(1));
		    retval.add(y);
	    }

	    return retval;
    }


    public List<Object> GetTV() {
            return ortus.api.getMediaFilesSQL("select mediaid from allmedia where mediatype = 1");
    }

//    public List<Object> Search(String params) {
//        return GetMediaInternal("allmedia",params);
//    }

//    public List<Object> GetMediaCast(String params) {
//        return GetMediaInternal("allmediacast",params);
//    }
//
//    public List<Object> GetMediaGenre(String params) {
//        return GetMediaInternal("allmediagenre",params);
//    }

    @Override
    public Object Search(String params) {

//        if ( Ortus.getInstance().modules.get("db") == null  )
//            return null;
     
	String table = "";
        String where = "";
        String sort = "";
        String limit = "";
        String postsort = "";
        String groupby = "";
        String groupbyoffset = "";
        boolean groupbysql = false;
        int sortcnt = 0;
        int user_watched = -999;
        int user_unwatched = -999;
        OrtusMediaType omt = OrtusMediaType.MediaFile;

        String[] p = params.split(";");
        
        for ( String x : p ) {
            ortus.api.DebugLog(LogLevel.Trace2, " Processing: " + x);
            String[] work = x.split(":");
            if( work[0].trim().equalsIgnoreCase("limit")) {
                limit = " limit " + work[1];
            }
            if ( work[0].trim().equalsIgnoreCase("sort")) {
                String[] sp = work[1].split(",");
                for ( String y : sp ) {
                    sortcnt++;
                    if ( sort.isEmpty())
                        sort = " order by ";
                    if ( sortcnt > 1)
                        sort+=",";
                    sort+=y;
                }
            }
            if ( work[0].trim().equalsIgnoreCase("filter")) {
		if ( where.isEmpty())
			where = " where ";
		else
			where+=" and ";
                where+= " " + work[1] + " ";
            }
	    if ( work[0].trim().equalsIgnoreCase("table")) {
		table = work[1].trim();
	    }

            if ( work[0].trim().equalsIgnoreCase("watched")) {
                user_watched = -1;
            }
            if ( work[0].trim().equalsIgnoreCase("unwatched")) {
                user_unwatched = -1;
            }
            if ( work[0].trim().equalsIgnoreCase("userwatched")) {
                try {
                    user_watched = Integer.parseInt(work[1]);
                } catch ( Exception e) {
                    user_watched = 0;
                }
            }
            if ( work[0].trim().equalsIgnoreCase("userunwatched")) {
                try {
                    user_unwatched = Integer.parseInt(work[1]);
                } catch ( Exception e) {
                    user_unwatched = 0;
                }
            }

            if ( work[0].trim().equalsIgnoreCase("groupby")) {
                groupby = work[1].trim();
            }
            if ( work[0].trim().equalsIgnoreCase("groupbyoffset")) {
                groupbyoffset = work[1].trim();
            }

            if ( work[0].trim().equalsIgnoreCase("postsort")) {
                postsort = work[1].trim();
            }

        }

        StringBuffer sql = new StringBuffer();

        if ( table.equalsIgnoreCase("episode")) {
            omt = OrtusMediaType.Episode;
            sql.append("select episodeid, mediaid, title");
        } else if ( table.equalsIgnoreCase("series")) {
            omt = OrtusMediaType.Series;
            sql.append("select seriesid, title");
        } else {
            if ( user_watched == -999 && user_unwatched == -999)
                sql.append("select mediaid, showtitle,episodetitle");
            else
                sql.append("select a.mediaid, a.showtitle, a.episodetitle");
        }

        if ( ! groupby.isEmpty()) {
            if ( ( ! groupby.equalsIgnoreCase("filesystem") ) && (! groupby.equalsIgnoreCase("genre"))) {
                groupbysql = true;
                if ( groupby.equalsIgnoreCase("mediaimporttime"))
                    sql.append(", formatdatetime(mediaimporttime,'yyyy-MM-dd') as mediaimporttime");
                else if ( groupby.equalsIgnoreCase("releasedate"))
                    sql.append(", YEAR(releasedate) as releasedate");
                else if ( groupby.equalsIgnoreCase("originalairdate"))
                    sql.append(", YEAR(releasedate) as originalairdat");
                else if ( groupby.equalsIgnoreCase("firstair"))
                    sql.append(", YEAR(firstair) as firstair");
                else
                    sql.append(", " + groupby);
            }
        }

        sql.append(" from ");

        if ( ! table.isEmpty()) {
            if ( table.toLowerCase().startsWith("all"))
                sql.append(table);
            else 
                sql.append("sage." + table);
        } else {
            if ( user_watched == -999 && user_unwatched == -999 )
                    sql.append("allmedia as a");
            else {
                if ( user_unwatched != -999) {
                    sql.append(" from allmedia as a left join sage.usermedia as um on a.mediaid = um.mediaid");
                    if ( ! where.isEmpty()) {
                        where += " and ( um.watched is null or um.watched = false)";
                    } else
                        where = " where ( um.watched is null or um.watched = false)";
                    if ( user_watched > -1)
                        where+=" and um.userid = " + user_unwatched;
                } else {
                    sql.append("allmedia as a left join sage.usermedia as um on a.mediaid = um.mediaid");
                    if ( ! where.isEmpty()) {
                        where += " and um.watched = true";
                    } else
                        where = " where um.watched = true";
                    if ( user_watched > -1)
                        where+=" and um.userid = " + user_watched;
                }
            }
        }

        if ( ! where.isEmpty())
            sql.append(where);

        if ( sortcnt > 0 )
            sql.append(sort);

        if ( ! limit.isEmpty())
            sql.append(limit);

        ortus.api.DebugLog(LogLevel.Trace, "GetMedia: SQL: " + sql);
        if ( ! groupby.isEmpty())
            ortus.api.DebugLogTrace("Groupby: " + groupby + " Offset: " + groupbyoffset);

        Object result = null;

        if ( groupbysql )
            result = new HashMap();
        else
            result = new ArrayList();

        Connection conn = ortus.api.GetConnection();
        QueryRunner qr = new QueryRunner();
        try {
          List<Map<String,Object>> records = qr.query(conn,sql.toString(), new MapListHandler());
          for( Map rec : records) {
//              for ( Object x : rec.keySet().toArray())
//                  ortus.api.DebugLog(LogLevel.Trace, " Key: " + x + " value: " + rec.get(x));
              int id = 0;
              switch(omt) {
                  case Episode:   id = (Integer)rec.get("EPISODEID");
                                    break;
                  case Series:    id = (Integer)rec.get("SERIESID");
                                    break;
                  default:  id = (Integer)rec.get("MEDIAID");
                                    break;
              }
              OrtusMedia om = new OrtusMedia(id,omt);
              if ( rec.get("MEDIAID") != null)
                   om.SetMediaID(rec.get("MEDIAID"));
              if ( rec.get("SHOWTITLE") != null)
                  om.setTitle((String)rec.get("SHOWTITLE"));
              else if ( rec.get("EPISODETITLE") != null)
                  om.setTitle((String)rec.get("EPISODETITLE"));
              else if ( rec.get("TITLE") != null)
                  om.setTitle((String)rec.get("TITLE"));
//              ortus.api.DebugLogTrace("Search Results:  ID: " + om.GetMediaID() + " Title: " + om.getTitle());
              if ( groupbysql ) {
                    String groupkey = "";
                    if ( rec.get(groupby.toUpperCase()) instanceof String)
                        groupkey = (String)rec.get(groupby.toUpperCase());
                    else
                        groupkey = String.valueOf(rec.get(groupby.toUpperCase()));

                    if ( ((HashMap)result).get(groupkey) == null) {
                        List x = new ArrayList();

                        x.add(om);
                        ((HashMap)result).put(groupkey, x);
                    } else {
                        ((List)((HashMap)result).get(groupkey)).add(om);
                    }
              } else {
                  ((List)result).add(om);
              }
          }
        } catch(Exception e ) {
            ortus.api.DebugLog(LogLevel.Error,"SQL: " + sql);
            ortus.api.DebugLog( LogLevel.Error, "Search Exception",e);
            return null;
        } finally {
            try { DbUtils.close(conn);} catch(Exception e) {}
        }

        Object returnval = null;

        if ( omt == OrtusMediaType.MediaFile) {
            if ( ! groupby.isEmpty()) {
                if ( groupby.equalsIgnoreCase("filesystem"))
                     returnval = ortus.api.GroupByPath((List)result, groupbyoffset);
                else if ( groupby.equalsIgnoreCase("genre"))
                     returnval = ortus.api.GroupByGenre((List)result,groupbyoffset);
                else {
                    if ( groupbyoffset.isEmpty())
                        returnval = result;
                    else {
                        returnval = ((HashMap)result).get(groupbyoffset);
                    }
                }
            } else
                returnval = result;

            if ( returnval instanceof HashMap) {
                ortus.api.DebugLogTrace("Search: Returning Type: HashMap Num Keys: " + ((HashMap)returnval).keySet().size());
            } else if ( returnval instanceof List) {
                ortus.api.DebugLogTrace("Search: Returning Type: List Num Values: " + ((List)returnval).size());
            } else
                ortus.api.DebugLogTrace("Search: Returning Type: Unknown");

            if ( ! postsort.isEmpty()) {
                if ( postsort.equalsIgnoreCase("desc") )
                       return Database.Sort(returnval, true, "CaseInsensitive");
                else
                    return Database.Sort(returnval, false, "CaseInsensitive");
            }
        } else
            returnval = result;

//        ortus.api.DebugLogTrace("returnval: " + returnval);
        return returnval;

    }

	public int GetMediaGroup(Object mediafile) {
          if ( mediafile instanceof OrtusMedia ) {
                if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetMediaGroup();
                } else if (((OrtusMedia) mediafile).IsSeries()) {
                        return 0;
                } else if (((OrtusMedia)mediafile).IsEpisode()) {
                     int mediaid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getMediaid();
                     return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediaid)).GetMediaGroup();
                } else
                    return 0;
            } else {
                return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetMediaGroup();
            }
        }

	public boolean IsTVMovie(Object mediafile) {
            return false;
        }
	public boolean IsImportedTV(Object mediafile) {
            return false;
        }
	public boolean IsRecorded(Object mediafile) {
            return false;
        }
	public boolean IsImported(Object mediafile) {
            return false;
        }
	public boolean IsIntelligentRecorded(Object mediafile) {
            return false;
        }

	public boolean IsFavorite(Object mediafile) {
            return false;
        }
	public boolean IsHD(Object mediafile) {
            return false;
        }
	public boolean IsSD(Object mediafile) {
            return false;
        }
	public boolean IsHDMovie(Object mediafile) {
            return false;
        }
	public boolean IsSDMovie(Object mediafile) {
            return false;
        }

	//Common Metadata
	public String GetMediaFileID(Object mediafile) {
            if ( mediafile instanceof OrtusMedia) {
                if ( ((OrtusMedia)mediafile).GetMediaID() != null)
                    return String.valueOf(((OrtusMedia)mediafile).GetMediaID());
                else 
                    if ( ((OrtusMedia)mediafile).IsMediaFile())
                        return String.valueOf(((OrtusMedia)mediafile).GetID());
                    else
                        return null;

            }  else
                return String.valueOf(MediaFileAPI.GetMediaFileID(mediafile));
        }
	public String GetImportDate(Object mediafile) {
            int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetID() : MediaFileAPI.GetMediaFileID(mediafile));
            List<Object> result = ortus.api.executeSQLQueryCache("select mediaimporttime from sage.media where mediaid = " + mediaid);
            if ( result.size() > 0)
                return (String)result.get(0);
            else
                return null;
        }
	public String GetPath(Object mediafile) {
          if ( mediafile instanceof OrtusMedia ) {
                if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetMediaPath();
                } else if (((OrtusMedia) mediafile).IsSeries()) {
                        return "Not Available";
                } else if (((OrtusMedia)mediafile).IsEpisode()) {
                     int mediaid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getMediaid();
                     return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediaid)).GetMediaPath();
                } else
                    return "Not Available";
            } else {
                return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetMediaPath();
            }
        }
	public String GetMediaEncoding(Object mediafile) {
          if ( mediafile instanceof OrtusMedia ) {
                if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetMediaEncoding();
                } else if (((OrtusMedia) mediafile).IsSeries()) {
                        return "Not Available";
                } else if (((OrtusMedia)mediafile).IsEpisode()) {
                     int mediaid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getMediaid();
                     return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediaid)).GetMediaEncoding();
                } else
                    return "Not Available";
            } else {
                return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetMediaEncoding();
            }
        }
	public String GetVideoEncoding(Object mediafile) {
            return null;
        }
	public String GetAudioEncoding(Object mediafile) {
            return null;
        }
	public String GetSubpicEncoding(Object mediafile) {
            return null;
        }

	public String GetEpisodeTitle(Object mediafile) {
            int mediaid = 0;
            int episodeid = 0;
            if ( mediafile instanceof OrtusMedia) {
                if (((OrtusMedia)mediafile).IsMediaFile())
                    mediaid = ((OrtusMedia)mediafile).GetMediaID();
                else if (((OrtusMedia)mediafile).IsEpisode())
                    episodeid = ((OrtusMedia)mediafile).GetID();
            } else
                mediaid = MediaFileAPI.GetMediaFileID(mediafile);

            if ( episodeid > 0) {
                List<Object> result = ortus.api.executeSQLQuery("select title from sage.episode where episodeid = " + episodeid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            } else {
                List<Object> result = ortus.api.executeSQLQuery("select title from sage.episode where mediaid = " + mediaid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            }
        }

	public String GetEpisodeData(Object mediafile, String column) {
            int mediaid = 0;
            int episodeid = 0;
            if ( mediafile instanceof OrtusMedia) {
                if (((OrtusMedia)mediafile).IsMediaFile())
                    mediaid = ((OrtusMedia)mediafile).GetMediaID();
                else if (((OrtusMedia)mediafile).IsEpisode())
                    episodeid = ((OrtusMedia)mediafile).GetID();
            } else
                mediaid = MediaFileAPI.GetMediaFileID(mediafile);

            if ( episodeid > 0) {
                List<Object> result = ortus.api.executeSQLQuery("select " + column + " from sage.episode where episodeid = " + episodeid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            } else {
                List<Object> result = ortus.api.executeSQLQuery("select " + column + " from sage.episode where mediaid = " + mediaid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            }
        }

	public String GetEpisodeDescription(Object mediafile) {
            int mediaid = 0;
            int episodeid = 0;
            if ( mediafile instanceof OrtusMedia) {
                if (((OrtusMedia)mediafile).IsMediaFile())
                    mediaid = ((OrtusMedia)mediafile).GetMediaID();
                else if (((OrtusMedia)mediafile).IsEpisode())
                    episodeid = ((OrtusMedia)mediafile).GetID();
            } else
                mediaid = MediaFileAPI.GetMediaFileID(mediafile);

            if ( episodeid > 0) {
                List<Object> result = ortus.api.executeSQLQuery("select description from sage.episode where episodeid = " + episodeid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            } else {
                List<Object> result = ortus.api.executeSQLQuery("select description from sage.episode where mediaid = " + mediaid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            }
        }
        
	public String GetSeriesData(Object mediafile,String column) {
            int seriesid = GetSeriesID(mediafile);

            if ( seriesid > 0 ) {
                List<Object> result = ortus.api.executeSQLQuery("select " + column + " from sage.series where seriesid = " + seriesid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            } else
                return null;
        }

	public HashMap GetSeriesCounts(Object mediafile) {
            HashMap results = new HashMap();
            int seriesid = GetSeriesID(mediafile);


            if ( seriesid > 0 ) {
                List<Object> result = ortus.api.executeSQLQuery("select count(*) from sage.episode where mediaid is not null and seriesid = " + seriesid);
                if ( result.size() > 0)
                    results.put("episode",result.get(0));
                result = ortus.api.executeSQLQuery("select count(distinct(seasonno)) from sage.episode where mediaid is not null and seriesid = " + seriesid);
                if ( result.size() > 0)
                    results.put("season",result.get(0));

            }

            return results;
        }
        
        public HashMap GetEpisodeInfo(Object mediafile) {
            int mediaid = 0;
            int episodeid = 0;
            HashMap result = null;

            if ( mediafile instanceof OrtusMedia) {
                if (((OrtusMedia)mediafile).IsMediaFile())
                    mediaid = ((OrtusMedia)mediafile).GetMediaID();
                else if (((OrtusMedia)mediafile).IsEpisode())
                    episodeid = ((OrtusMedia)mediafile).GetID();
            } else
                mediaid = MediaFileAPI.GetMediaFileID(mediafile);

            if ( episodeid == 0 && mediaid > 0) {
                List<Object> x = ortus.api.executeSQLQuery("select episodeid from sage.episode where mediaid = " + mediaid);
                if ( x.size() > 0)
                    try { episodeid = Integer.parseInt((String)x.get(0)); } catch (Exception e) {}
            }

            if (((OrtusMedia)mediafile).GetID() != 999)
                episodeid = 0;

            if ( episodeid > 0 ) {
                Episode eis = (Episode)ortus.cache.cacheEngine.getInstance().GetCache("EP" + episodeid);
                if ( eis != null) {
                    result = eis.toHash();
                }
            }

            if ( result == null)
                result = new HashMap();

            return result;
        }


        public HashMap GetSeriesInfo(Object mediafile) {
            HashMap result = null;
            int seriesid = GetSeriesID(mediafile);

            if ( seriesid > 0 ) {
                Series cis = (Series)ortus.cache.cacheEngine.getInstance().GetCache("SR" + seriesid);
                if ( cis != null) {
                    result = cis.toHash();
                    List<Object> y = ortus.api.executeSQLQuery("select count(*) from sage.episode where mediaid is not null and seriesid = " + seriesid);
                    if ( y.size() > 0)
                        result.put("episode",y.get(0));
                    y = ortus.api.executeSQLQuery("select count(distinct(seasonno)) from sage.episode where mediaid is not null and seriesid = " + seriesid);
                    if ( y.size() > 0)
                        result.put("season",y.get(0));
                }
            }

            if ( result == null)
                result = new HashMap();

            return result;
        }

	//TV Series Metadata
	public String GetSeriesTitle(Object mediafile) {
            int mediaid = 0;
            int seriesid = 0;
            if ( mediafile instanceof OrtusMedia) {
                if (((OrtusMedia)mediafile).IsMediaFile() || ((OrtusMedia)mediafile).IsEpisode())
                    mediaid = ((OrtusMedia)mediafile).GetMediaID();
                else if (((OrtusMedia)mediafile).IsSeries())
                    seriesid =  ((OrtusMedia)mediafile).GetID();
            } else
                mediaid = MediaFileAPI.GetMediaFileID(mediafile);

            if ( seriesid > 0 ) {
                List<Object> result = ortus.api.executeSQLQuery("select title from sage.series where seriesid = " + seriesid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            } else {
                List<Object> result = ortus.api.executeSQLQuery("select s.title from allmedia as a, sage.series as s where a.mediaid = " + mediaid + " and a.seriesid = s.seriesid");
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            }
        }
        
	public String GetSeriesDescription(Object mediafile) {
            int mediaid = 0;
            int seriesid = 0;
            if ( mediafile instanceof OrtusMedia) {
                if (((OrtusMedia)mediafile).IsMediaFile() || ((OrtusMedia)mediafile).IsEpisode())
                    mediaid = ((OrtusMedia)mediafile).GetMediaID();
                else if (((OrtusMedia)mediafile).IsSeries())
                    seriesid =  ((OrtusMedia)mediafile).GetID();
            } else
                mediaid = MediaFileAPI.GetMediaFileID(mediafile);

            if ( seriesid > 0 ) {
                List<Object> result = ortus.api.executeSQLQuery("select description from sage.series where seriesid = " + seriesid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            } else {
                List<Object> result = ortus.api.executeSQLQuery("select s.description from allmedia as a, sage.series as s where a.mediaid = " + mediaid + " and a.seriesid = s.seriesid");
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            }
        }
	public String GetSeriesNetwork(Object mediafile) {
            int mediaid = 0;
            int seriesid = 0;
            if ( mediafile instanceof OrtusMedia) {
                if (((OrtusMedia)mediafile).IsMediaFile() || ((OrtusMedia)mediafile).IsEpisode())
                    mediaid = ((OrtusMedia)mediafile).GetMediaID();
                else if (((OrtusMedia)mediafile).IsSeries())
                    seriesid =  ((OrtusMedia)mediafile).GetID();
            } else
                mediaid = MediaFileAPI.GetMediaFileID(mediafile);

            if ( seriesid > 0 ) {
                List<Object> result = ortus.api.executeSQLQuery("select network from sage.series where seriesid = " + seriesid);
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            } else {
                List<Object> result = ortus.api.executeSQLQuery("select s.network from allmedia as a, sage.series as s where a.mediaid = " + mediaid + " and a.seriesid = s.seriesid");
                if ( result.size() > 0)
                    return (String)result.get(0);
                else
                    return null;
            }
        }
	public long GetSeriesFirstAirDate(Object mediafile) {
            int mediaid = 0;
            int seriesid = 0;
            if ( mediafile instanceof OrtusMedia) {
                if (((OrtusMedia)mediafile).IsMediaFile() || ((OrtusMedia)mediafile).IsEpisode())
                    mediaid = ((OrtusMedia)mediafile).GetMediaID();
                else if (((OrtusMedia)mediafile).IsSeries())
                    seriesid =  ((OrtusMedia)mediafile).GetID();
            } else
                mediaid = MediaFileAPI.GetMediaFileID(mediafile);

            if ( seriesid > 0 ) {
                List<Object> result = ortus.api.executeSQLQuery("select DateToEpoch(firstair) from sage.series where seriesid = " + seriesid);
                if ( result.size() > 0)
                    return Long.parseLong((String)result.get(0));
                else
                    return System.currentTimeMillis();
            } else {
                ortus.api.DebugLogTrace("GetSeriesFirstAirDate: Using Mediaid: " + mediaid);
                List<Object> result = ortus.api.executeSQLQuery("select DateToEpoch(s.firstair) from allmedia as a, sage.series as s where a.mediaid = " + mediaid + " and a.seriesid = s.seriesid");
                if ( result.size() > 0)
                    return Long.parseLong((String)result.get(0));
                else
                    return System.currentTimeMillis();
            }

        }
	public long GetSeriesFinalAirDate(Object mediafile) {
            return 0;
        }
	public boolean IsSeriesStillRunning(Object mediafile) {
            return false;
        }
	public String GetSeriesAirDay(Object mediafile) {
            return null;
        }
	public long GetSeriesRunTime(Object mediafile) {
            return 0;
        }
	public long GetSeriesNextEpisodeDate(Object mediafile) {
            return 0;
        }
	public String GetSeriesTVRating(Object mediafile) {
            return null;
        }
	public int GetSeriesTotalSeasons(Object mediafile) {
            return 0;
        }
	public int GetSeriesTotalEpisodes(Object mediafile) {
            return 0;
        }
	public int GetSeriesTotalEpisodesAvailable(Object mediafile) {
            return 0;
        }

	//TV Metadata

	//Movie Metadata
	//Cast Metadata
    public String GetDirector(Object mediafile) {
        return null;
    }
    public String GetWriter(Object mediafile) {
        return null;
    }
    public String GetProducer(Object mediafile) {
        return null;
    }
    public List<String> GetActors(Object mediafile) {
        return null;
    }

    public List<HashMap> GetCast(Object mediafile, String job) {
        return null;
    }
    public String GetMPAARating(Object mediafile) {
       if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return  ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetMPAARated();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getMpaarated();
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                 int seriesid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getSeriesid();
                 return ((Series)ortus.cache.cacheEngine.getInstance().GetCache("SR" + seriesid)).getMpaarated();
            } else
                return "Not Available";
        } else {
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetMPAARated();
        }
    }
    public void SetMPAARating(Object mediafile, String newmpaarating) {
        int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetMediaID() : MediaFileAPI.GetMediaFileID(mediafile));
	    int result = ortus.api.executeSQL("update sage.mediavideos set mpaarated = '" + newmpaarating + "' where mediaid = " + mediaid);
	    ortus.cache.cacheEngine.getInstance().ReLoadCache(mediafile);
    }
    public int GetUserRating(Object mediafile) {
       if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return  Math.round(((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetUserRating());
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return Math.round(((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getUserrating());
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                 return Math.round(((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getUserrating());
            } else
                return 0;
        } else {
            return Math.round(((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetUserRating());
        }
    }
    public String GetUserRatingString(Object mediafile) {
       if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return  String.valueOf(((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetUserRating());
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return String.valueOf(((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getUserrating());
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                 return String.valueOf(((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getUserrating());
            } else
                return "0";
        } else {
            return String.valueOf(((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetUserRating());
        }
    }
    
    public void SetUserRating(Object mediafile, String newuserrating) {
            int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetMediaID() : MediaFileAPI.GetMediaFileID(mediafile));
	    float userrating = 5;
	    try {
		    userrating = Float.parseFloat(newuserrating);
	    } catch(Exception e) {
		    userrating = 5;
	    }

            if ( IsSeries(mediafile)) {
                int result = ortus.api.executeSQL("update sage.episode set userrating = " + userrating + " where mediaid = " + mediaid);
            } else {
               int result = ortus.api.executeSQL("update sage.mediavideos set userrating = " + userrating + " where mediaid = " + mediaid);
            }
	    ortus.cache.cacheEngine.getInstance().ReLoadCache(mediafile);
    }
    public int GetDiscNumber(Object mediafile) {
        return 0;
    }
    public String GetReleaseDate(Object mediafile) {
      if ( mediafile instanceof OrtusMedia ) {
            if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                    return ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetReleaseDate();
            } else if (((OrtusMedia) mediafile).IsSeries()) {
                    return ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getFirstair();
            } else if (((OrtusMedia)mediafile).IsEpisode()) {
                 return ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getOriginalairdate();
            } else
                return "Not Available";
        } else {
            return ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetReleaseDate();
        }
    }
    public void SetReleaseDate(Object mediafile, String newreleasedate) {

        int mediaid = ( mediafile instanceof OrtusMedia ? ((OrtusMedia)mediafile).GetMediaID() : MediaFileAPI.GetMediaFileID(mediafile));
        if ( IsSeries(mediafile)) {
            int result = ortus.api.executeSQL("update sage.episode set originalairdate = '" +  newreleasedate + "' where mediaid = " + mediaid);
        } else {
	    int result = ortus.api.executeSQL("update sage.mediavideos set releasedate = '" +  newreleasedate + "' where mediaid = " + mediaid);
        }
	ortus.cache.cacheEngine.getInstance().ReLoadCache(mediafile);
    }

	@Override
	public List<HashMap> GetMusicByArtist(String filter) {
		String SQL = "select artist), count(*) from sage.music";
		if ( ! filter.isEmpty())
			SQL+=" where " + filter;

		List<List> result = ortus.api.executeSQLQueryArray(SQL);
		List<HashMap> retval = new ArrayList<HashMap>();
		for ( List x : result) {
			HashMap y = new HashMap();
			y.put("artist", x.get(0));
			y.put("count", x.get(1));
			retval.add(y);
		}
		return retval;
	}

	@Override
	public List<HashMap> GetMusicByAlbum(String filter) {
		String SQL = "select distinct(album), count(*) from sage.music";
		if ( ! filter.isEmpty())
			SQL+=" where " + filter;

		List<List> result = ortus.api.executeSQLQueryArray(SQL);
		List<HashMap> retval = new ArrayList<HashMap>();
		for ( List x : result) {
			HashMap y = new HashMap();
			y.put("album", x.get(0));
			y.put("count", x.get(1));
			retval.add(y);
		}
		return retval;

	}

	@Override
	public List<Object> GetMusicBySong(String filter) {
		String SQL = "select mediaid from sage.music";
		if ( ! filter.isEmpty())
			SQL+=" where " + filter;

		List<List> result = ortus.api.executeSQLQueryArray(SQL);
		List<Object> retval = new ArrayList<Object>();
		for ( List x : result) {
			retval.add(MediaFileAPI.GetMediaFileForID(Integer.parseInt((String)x.get(0))));
		}
		return retval;


	}

//        public HashMap GetMetadataCache(Object mediafile) {
//            if ( mediafile instanceof OrtusMedia) {
//                Object x = ortus.cache.cacheEngine.getInstance().getProvider().Get(((OrtusMedia)mediafile).GetKey());
//                if ( x != null)
//                    return (HashMap)x;
//                else {
//                    HashMap y = GetMetadata(mediafile);
//                    ortus.cache.cacheEngine.getInstance().Put(, y);
//                }
//
//            } else if ( MediaFileAPI.IsMediaFileObject(mediafile)) {
//
//            } else
//                return new HashMap();
//        }

}
