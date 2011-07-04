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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringEscapeUtils;
import ortus.Ortus;
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

    public int GetMediaID(Object mediafile) {
        return ortus.media.metadata.utils.GetMediaID(mediafile);
    }

    public int GetSeriesID(Object mediafile) {
        return ortus.media.metadata.utils.GetSeriesID(mediafile);
    }

    public int GetEpisodeID(Object mediafile) {
        return ortus.media.metadata.utils.GetEpisodeID(mediafile);
    }    

    public ortus.media.metadata.item.Media GetMetadataMedia(Object mediafile) {
        int mediaid = GetMediaID(mediafile);

        List<List> result = ortus.api.executeSQLQueryArray("select mediaid, showtitle, episodetitle, mediapath, description, mediaencoding, mediatype, mediagroup, airingstarttime, userrating, mpaarated, releasedate, mediaduration, seriesid, seasonno, episodeid, episodeno, mediasize, TimestampToEpoch(mediaimporttime) from allmedia where mediaid = " + mediaid);
        if (result.size() > 0) {
            try {
                int  workRating = 5;
                if (result.get(0).get(9) != null) {
                    try {
                        workRating = Integer.parseInt(((String) result.get(0).get(9)).trim());
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
                    List<List> misc = ortus.api.executeSQLQueryArray("select trailer from sage.metadata where mediaid = " + mediaid);
                    if ( misc.size()>0)
                        ci.setTrailer((String)misc.get(0).get(0));
                    geresult = ortus.api.executeSQLQueryArray("select g.name from sage.genre as g where g.mediaid = " + Integer.parseInt((String) result.get(0).get(0)));
//                    List<List> cast = ortus.api.executeSQLQueryArray("select personid, name, job, character from sage.cast where mediaid = " + Integer.parseInt((String)result.get(0).get(0)));
//                    for ( List castitem : cast) {
//                        ci.addCast(new Cast(Integer.parseInt((String)castitem.get(0)), (String)castitem.get(1), (String)castitem.get(2),(String)castitem.get(3)));
//                    }
                }
                for (List geitem : geresult) {
                    ci.AddGenre((String) geitem.get(0));
                }

                List<HashMap> faresult;
                if ( ci.GetMediaType() == 3) {
       //             faresult = new ArrayList();
         //           ortus.api.DebugLog(LogLevel.Trace,"select f.* from sage.fanart as f, sage.episode as e, sage.episodemedia as em where f.idtype = 'SR' and f.mediaid = e.seriesid and em.episodeid = e.episodeid and em.mediaid = " + Integer.parseInt((String) result.get(0).get(0)) + " order by id");
                 //   faresult = ortus.api.executeSQLQueryHash("select f.* from sage.fanart as f, sage.episode as e, sage.episodemedia as em where f.idtype = 'SR' and f.mediaid = e.seriesid and e.episodeid = em.episodeid and em.mediaid = " + Integer.parseInt((String) result.get(0).get(0)) + " order by id");
                    faresult = ortus.api.executeSQLQueryHash("select f.* from sage.fanart as f where f.idtype = 'SR' and f.mediaid in ( select e.seriesid from  sage.episode as e, sage.episodemedia as em where  e.episodeid = em.episodeid and em.mediaid = " + Integer.parseInt(((String)result.get(0).get(0))) + ") order by id");
                } else {
                    faresult = ortus.api.executeSQLQueryHash("select * from sage.fanart where idtype = 'MD' and mediaid = " + Integer.parseInt((String) result.get(0).get(0)) + " order by id");

                }
                for (HashMap faitem : faresult) {
//                    ortus.api.DebugLogTrace("faitem: " + faitem.toString());
                    ci.AddFanart(new Fanart(Integer.parseInt((String)faitem.get("ID")),(String) faitem.get("METADATAID"), Integer.parseInt((String) faitem.get("DEFAULT")), Integer.parseInt((String) faitem.get("LOW_HEIGHT")), Integer.parseInt((String) faitem.get("LOW_WIDTH")), "low", (String) faitem.get("TYPE"), (String) faitem.get("LOW_URL"), (String) faitem.get("LOW_FILE"),Long.parseLong((String)faitem.get("LOW_IMAGESIZE"))));
                    ci.AddFanart(new Fanart(Integer.parseInt((String)faitem.get("ID")),(String) faitem.get("METADATAID"), Integer.parseInt((String) faitem.get("DEFAULT")), Integer.parseInt((String) faitem.get("MEDIUM_HEIGHT")), Integer.parseInt((String) faitem.get("MEDIUM_WIDTH")), "medium", (String) faitem.get("TYPE"), (String) faitem.get("MEDIUM_URL"), (String) faitem.get("MEDIUM_FILE"),Long.parseLong((String)faitem.get("MEDIUM_IMAGESIZE"))));
                    ci.AddFanart(new Fanart(Integer.parseInt((String)faitem.get("ID")),(String) faitem.get("METADATAID"), Integer.parseInt((String) faitem.get("DEFAULT")), Integer.parseInt((String) faitem.get("HIGH_HEIGHT")), Integer.parseInt((String) faitem.get("HIGH_WIDTH")), "high", (String) faitem.get("TYPE"), (String) faitem.get("HIGH_URL"), (String) faitem.get("HIGH_FILE"),Long.parseLong((String)faitem.get("HIGH_IMAGESIZE"))));

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
                ci = new Series((Integer) rec.get("seriesid"), (String) rec.get("imdbid"), (String) rec.get("zap2itid"), (String) rec.get("title"), ((java.sql.Date) rec.get("firstair")).toString(), (String) rec.get("airday"), (String) rec.get("airtime"), (String) rec.get("status"), (String) rec.get("description"), (String) rec.get("network"), ((Integer) rec.get("userrating")), (String) rec.get("mpaarated"), (Long) rec.get("runtime"));
                List<Map<String, Object>> genrerecords = qr.query(conn, "select * from sage.seriesgenre where seriesid = ?", seriesid, new MapListHandler());
                for (Map<String, Object> genrerec : genrerecords) {
                    ci.addGenre((String) genrerec.get("name"));
                }
//                            records = qr.query(conn,"select ec.episodeid, ec.personid, ec.name, ec.job, ec.character from sage.seriescast as ec where ec.seriesid  = ?", seriesid,new MapListHandler());
//                            for ( Map<String,Object> castrec : records) {
//                                ((cacheItemSeries)ci).addCast(new cacheItemCast(Integer.parseInt((String)castrec.get("episodeid")), Integer.parseInt((String)castrec.get("personid")), (String)castrec.get("name"),(String)castrec.get("job"),(String)castrec.get("character")));
//                            }

                List<Map<String, Object>> fanartrecords = qr.query(conn, "select * from sage.fanart where mediaid  = ? and idtype = 'SR'", seriesid , new MapListHandler());
                for (Map<String, Object> faitem : fanartrecords) {
                    ci.AddFanart(new Fanart((Integer)faitem.get("id"),(String) faitem.get("metadataid"), (Integer)faitem.get("default"), (Integer)faitem.get("low_height"), (Integer)faitem.get("low_width"), "low", (String) faitem.get("type"), (String) faitem.get("low_url"), (String) faitem.get("low_file"),(Long)faitem.get("low_imagesize")));
                    ci.AddFanart(new Fanart((Integer)faitem.get("id"),(String) faitem.get("metadataid"), (Integer)faitem.get("default"), (Integer)faitem.get("medium_height"), (Integer)faitem.get("medium_width"), "medium", (String) faitem.get("type"), (String) faitem.get("medium_url"), (String) faitem.get("medium_file"),(Long)faitem.get("medium_imagesize")));
                    ci.AddFanart(new Fanart((Integer)faitem.get("id"),(String) faitem.get("metadataid"), (Integer)faitem.get("default"), (Integer)faitem.get("high_height"), (Integer)faitem.get("high_width"), "high", (String) faitem.get("type"), (String) faitem.get("high_url"), (String) faitem.get("high_file"),(Long)faitem.get("high_imagesize")));

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
                ortus.media.metadata.item.Episode ci = new Episode((Integer) rec.get("episodeid"), (Integer) rec.get("seriesid"), (Integer) rec.get("episodeno"), (Integer) rec.get("seasonid"), (Integer) rec.get("seasonno"), workid, (String) rec.get("title"), (String) rec.get("description"), ((java.sql.Date) rec.get("originalairdate")).toString(), (Integer)rec.get("userrating"), (String) rec.get("thumbpath"));
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

    public List<HashMap> GetMetadataCast(Object mediafile) {
        int mediaid = GetMediaID(mediafile);

        ortus.api.DebugLogTrace("GetMetadataCast: Getting cast info for id: " + mediaid);

        List<HashMap> result = new ArrayList<HashMap>();

        List<List> cast = ortus.api.executeSQLQueryArray("select c.personid, c.name, c.job, c.character from sage.cast as c where c.mediaid = " + mediaid);
        
        try {

            for ( List castitem : cast) {
                int personid = Integer.parseInt((String)castitem.get(0));
                ortus.api.DebugLogTrace(" Personid: " + personid);
                HashMap ci = new HashMap();
                ci.put("personid",personid);
                ci.put("name",(String)castitem.get(1));
                ci.put("job",(String)castitem.get(2));
                ci.put("character",(String)castitem.get(3));
                List<List> rec = ortus.api.executeSQLQueryArray("select biography, nomovies, birthday, birthplace from sage.actor where id = " + personid);
                if ( rec.size() > 0) {
                    ci.put("biography", rec.get(0).get(0));
                    ci.put("nomovies", rec.get(0).get(1));
                    ci.put("birthdate",rec.get(0).get(2));
                    ci.put("birthplace", rec.get(0).get(3));
                }
                
                List<HashMap> personfanart = new ArrayList<HashMap>();
                List<HashMap> cafa = ortus.api.executeSQLQueryHash("select * from sage.fanart where idtype = 'CA' and mediaid = " + personid);
                for ( HashMap cafaitem : cafa) {
                    ortus.api.DebugLogTrace(" Fanart id: " + cafaitem.get("ID"));
                    HashMap x = new HashMap();
                    x.put("id",cafaitem.get("ID"));
                    HashMap q = new HashMap();
                    q.put("file",cafaitem.get("LOW_FILE"));
                    q.put("url",cafaitem.get("LOW_URL"));
                    x.put("low",q);
                    HashMap r = new HashMap();
                    q.put("file",cafaitem.get("MEDIUM_FILE"));
                    q.put("url",cafaitem.get("MEDIUM_URL"));
                    x.put("medium",r);
                    HashMap s = new HashMap();
                    q.put("file",cafaitem.get("HIGH_FILE"));
                    q.put("url",cafaitem.get("HIGH_URL"));
                    x.put("high",s);
                    personfanart.add(x);
                }
                ci.put("fanart",personfanart);

                List<HashMap> personfilms = new ArrayList<HashMap>();
                List<List> cafi = ortus.api.executeSQLQueryArray("select id, name, character, job from sage.actormovies where personid = " + personid);
                for ( List cafiitem : cafi) {
                    HashMap x = new HashMap();
                    x.put("id",cafiitem.get(0));
                    x.put("name",cafiitem.get(1));
                    x.put("character",cafiitem.get(2));
                    x.put("job",cafiitem.get(3));
                    personfilms.add(x);
                }
                ci.put("films",personfilms);

                result.add(ci);
            }
        } catch ( Exception e) {
            ortus.api.DebugLog(LogLevel.Error, "GetMetadataCast: Exception: ",e);
        }

        return result;
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
        ortus.api.DebugLogTrace("GetMetadata: Called with " + mediafile + " of type: " + mediafile.getClass().getName());
        if ( mediafile instanceof Integer || mediafile instanceof String) {
            IItem ii = (IItem)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediafile);
            dataHash = ii.toHash();
        } else if (mediafile instanceof OrtusMedia) {
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

            List<HashMap> results = ortus.api.executeSQLQueryHash("select * from sage.usermedia where mediaid = " + dataHash.get("mediaid"));
            if ( results.size() > 0) {
                List userWatched = new ArrayList();
                for ( HashMap x : results) {
                    HashMap y = new HashMap();
                    y.put("userid",x.get("USERID"));
                    y.put("watched",x.get("WATCHED"));
                    y.put("lastwatched",x.get("LASTWATCHEDTIMESTAMP "));
                    userWatched.add(y);
                }
                dataHash.put("watched", userWatched);
            }

            
//            ortus.api.DebugLogTrace("GetMetadata: Return: " + dataHash);
        }
        return dataHash;
    }

    public HashMap GetMetadataFull(Object mediafile) {
        HashMap dataHash = null;
//        ortus.api.DebugLogTrace("GetMetadataFull: Called with " + mediafile);
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

            List<HashMap> results = ortus.api.executeSQLQueryHash("select * from sage.usermedia where mediaid = " + dataHash.get("mediaid"));
            if ( results.size() > 0) {
                List userWatched = new ArrayList();
                for ( HashMap x : results) {
                    HashMap y = new HashMap();
                    y.put("userid",x.get("USERID"));
                    y.put("watched",x.get("WATCHED"));
                    y.put("lastwatched",x.get("LASTWATCHEDTIMESTAMP "));
                    userWatched.add(y);
                }
                dataHash.put("watched", userWatched);
            }


//            ortus.api.DebugLogTrace("GetMetadataFull: Return: " + dataHash);
        }
        return dataHash;

    }

    public String GetMetadataFullXML(Object mediafile) {
         String xml = "<MediaFile></MediaFile>";
//        ortus.api.DebugLogTrace("GetMetadataFull: Called with " + mediafile);
        if (mediafile instanceof OrtusMedia) {
            IItem ii = (IItem)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey());
            xml = ii.toXML();
        } else if (MediaFileAPI.IsMediaFileObject(mediafile)) {
            IItem ii = (IItem)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile));
            if (ii.isValid()) {
                xml = ii.toXML();
            } 
        }

        if (xml == null) {
            ortus.api.DebugLogError("GetMetadataFullXML: Invalid Object");
        } 

        ortus.api.DebugLogTrace("GetMetadataFullXML: XML: " + xml);

        return xml;

    }
	public String GetFanartFolder() {

		String fanartfolder = Configuration.GetProperty("ortus/fanart/folder", "None");

		if (fanartfolder.equalsIgnoreCase("none")) {
			fanartfolder = Ortus.getInstance().getBasePath() + seperator + "STVs" + java.io.File.separator + "Ortus" + java.io.File.separator +  "Fanart";
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
            int mediaid = GetMediaID(mediafile);
	    title = StringEscapeUtils.escapeSql(title);
            ortus.api.executeSQL("update sage.media set mediatitle = '" + title + "' where mediaid = " + mediaid);
            int rc = ortus.api.executeSQL("update sage.metadata set name = '" + title + "' where mediaid = " + mediaid);
            if ( rc == 0) {
                ortus.api.executeSQL("insert into sage.metadata (mediaid,name) values('" + title + "', " + mediaid + ")");
            }
	     ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { "MD"+mediaid } );
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
            int mediaid = GetMediaID(mediafile);
	    title = StringEscapeUtils.escapeSql(title);
            ortus.api.executeSQL("update sage.media set episodetitle = '" + title + "' where mediaid = " + mediaid);
            
	    ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { "MD"+mediaid } );
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
            int mediaid = GetMediaID(mediafile);
	    description = StringEscapeUtils.escapeSql(description);
            int result = ortus.api.executeSQL("update sage.metadata set overview = '" + description + "' where mediaid = " + mediaid);
            if ( result < 1) {
                result = ortus.api.executeSQL("insert into sage.metadata (mediaid, overview) values( " + mediaid + ",'" + description + "')");
            }
             ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { "MD"+mediaid } );
    }
    public void SetMPAARating(Object mediafile, String newmpaarating) {
            int mediaid = GetMediaID(mediafile);
	     int result = ortus.api.executeSQL("update sage.metadata set certification = '" + newmpaarating + "' where mediaid = " + mediaid);
            if ( result < 1) {
                result = ortus.api.executeSQL("insert into sage.metadata (mediaid, certification) values( " + mediaid + ",'" + newmpaarating + "')");
            }
	     ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { "MD"+mediaid } );
    }

   public void SetUserRating(Object mediafile, String newuserrating) {
            int mediaid = GetMediaID(mediafile);
	    int userrating = 5;
	    try {
		    userrating = Integer.parseInt(newuserrating);
	    } catch(Exception e) {
		    userrating = 5;
	    }

            int result = ortus.api.executeSQL("update sage.metadata set rating = '" + newuserrating + "' where mediaid = " + mediaid);
            if ( result < 1) {
                result = ortus.api.executeSQL("insert into sage.metadata (mediaid, rating) values( " + mediaid + ",'" + newuserrating + "')");
            }
	    ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { "MD"+mediaid } );
    }
    public void SetReleaseDate(Object mediafile, String newreleasedate) {
         int mediaid = GetMediaID(mediafile);
         
        int result = ortus.api.executeSQL("update sage.metadata set releasedate = '" + newreleasedate + "' where mediaid = " + mediaid);
            if ( result < 1) {
                result = ortus.api.executeSQL("insert into sage.metadata (mediaid, releasedate) values( " + mediaid + ",'" + newreleasedate + "')");
        }
	ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { "MD"+mediaid } );
    }

    public void SetMediaType(Object mediafile, int mediatype) {
        int mediaid = GetMediaID(mediafile);

        int result = ortus.api.executeSQL("update sage.media set mediatype = " + mediatype + " where mediaid = " + mediaid);

        if ( mediatype != 3) {
            result = ortus.api.executeSQL("update sage.episode set mediaid = null where mediaid = " + mediaid);
        }

	 ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { "MD"+mediaid } );
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

    public void SetGenre(Object mediafile, List genres) {
           int mediaid = GetMediaID(mediafile);

           int result = ortus.api.executeSQL("delete from sage.genre where mediaid = " + mediaid);

           for ( Object x : genres) {
                result = ortus.api.executeSQL("insert into sage.genre (mediaid,name) values( " + mediaid + ",'" + x + "')");
           }
	   ortus.mq.api.fireMQMessage(ortus.mq.vars.MsgPriority.High,ortus.mq.vars.EvenType.Broadcast,"ReloadMediaCache", new Object[] { "MD"+mediaid } );
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
        String outputFormat = "SM";
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
                if ( ! work[1].trim().equalsIgnoreCase("allmedia"))
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
                    user_watched = Integer.parseInt(work[1].trim());
                } catch ( Exception e) {
                    ortus.api.DebugLogError("Error processing user; setting to 0; input: " + work[1],e);
                    user_watched = 0;
                }
            }
            if ( work[0].trim().equalsIgnoreCase("userunwatched")) {
                try {
                    user_unwatched = Integer.parseInt(work[1].trim());
                } catch ( Exception e) {
                    ortus.api.DebugLogError("Error processing user; setting to 0; input: " + work[1],e);
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
            if ( work[0].trim().equalsIgnoreCase("output")) {
                outputFormat=work[1].trim();
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
 //           if ( user_watched == -999 && user_unwatched == -999)
                sql.append("select mediaid, showtitle,episodetitle");
  //          else
   //             sql.append("select a.mediaid, a.showtitle, a.episodetitle");
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
        
//        } else {
//            if ( user_watched == -999 && user_unwatched == -999 )
//                    sql.append("allmedia as a");
//            else {
//                if ( user_unwatched != -999) {
//                    sql.append(" allmedia as a left join sage.usermedia as um on a.mediaid = um.mediaid");
//                    if ( ! where.isEmpty()) {
//                        where += " and ( um.watched is null or um.watched = false)";
//                    } else
//                        where = " where ( um.watched is null or um.watched = false)";
//                    if ( user_watched > -1)
//                        where+=" and um.userid = " + user_unwatched;
//                } else {
//                    sql.append("allmedia as a left join sage.usermedia as um on a.mediaid = um.mediaid");
//                    if ( ! where.isEmpty()) {
//                        where += " and um.watched = true";
//                    } else
//                        where = " where um.watched = true";
//                    if ( user_watched > -1)
//                        where+=" and um.userid = " + user_watched;
//                }
//            }
        } else {
            sql.append("allmedia as a");
        }

        if ( ! where.isEmpty()) {
            sql.append(where);
        }

        if ( user_watched != -999) {
            if ( user_watched == -1) {
                user_watched = ortus.api.GetCurrentUser();
            }
            if ( ! where.isEmpty()) {
              sql.append("and mediaid in ( select mediaid from sage.usermedia where userid = " + user_watched + " and watched = true)");
            } else {
               sql.append("where mediaid in ( select mediaid from sage.usermedia where userid = " + user_watched + " and watched = true)");
            }
        }

        if ( user_unwatched != -999) {
            if ( user_unwatched == -1) {
                user_unwatched = ortus.api.GetCurrentUser();
            }
            if ( ! where.isEmpty()) {
              sql.append("and mediaid not in ( select mediaid from sage.usermedia where userid = " + user_unwatched + " and watched = true)");
            } else {
               sql.append("where mediaid not in ( select mediaid from sage.usermedia where userid = " + user_unwatched + " and watched = true)");
            }
        }

        if ( sortcnt > 0 )
            sql.append(sort);

        if ( ! limit.isEmpty())
            sql.append(limit);

        ortus.api.DebugLog(LogLevel.Trace, "GetMedia: SQL: " + sql);
        if ( ! groupby.isEmpty())
            ortus.api.DebugLogTrace("Groupby: " + groupby + " Offset: " + groupbyoffset);

        Object result = null;

        if ( groupbysql )
            result = new LinkedHashMap();
        else
            result = new LinkedList();

        Connection conn = ortus.api.GetConnection();
        QueryRunner qr = new QueryRunner();
        try {
          List<Map<String,Object>> records = qr.query(conn,sql.toString(), new MapListHandler());
          for( Map rec : records) {

//              if ( user_watched != -999)
//                  if ( user_watched == -1) {
//                      if( ! ortus.api.IsUserWatched(rec.get("MEDIAID")))
//                          continue;
//                  } else {
//                      if( ! ortus.api.IsUserWatched(rec.get("MEDIAID"),user_watched))
//                          continue;
//                  }
//              if ( user_unwatched != -999)
//                  if ( user_unwatched == -1) {
//                      if( ortus.api.IsUserWatched(rec.get("MEDIAID")))
//                      continue;
//                  } else {
//                      if( ortus.api.IsUserWatched(rec.get("MEDIAID"),user_unwatched))
//                          continue;
//                  }
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

              if ( outputFormat.equalsIgnoreCase("web")) {
                  HashMap show = ortus.api.GetMetadata(om);

                  if ( groupbysql ) {
                        String groupkey = "";
                        if ( rec.get(groupby.toUpperCase()) instanceof String)
                            groupkey = (String)rec.get(groupby.toUpperCase());
                        else
                            groupkey = String.valueOf(rec.get(groupby.toUpperCase()));

                        if ( ((LinkedHashMap)result).get(groupkey) == null) {
                            List x = new LinkedList();

                            x.add(show);
                            ((LinkedHashMap)result).put(groupkey, x);
                        } else {
                            ((LinkedList)((LinkedHashMap)result).get(groupkey)).add(show);
                        }
                  } else {
                      ((LinkedList)result).add(show);
                  }              
              } else {
    //              ortus.api.DebugLogTrace("Search Results:  ID: " + om.GetMediaID() + " Title: " + om.getTitle());
                  if ( groupbysql ) {
                        String groupkey = "";
                        if ( rec.get(groupby.toUpperCase()) instanceof String)
                            groupkey = (String)rec.get(groupby.toUpperCase());
                        else
                            groupkey = String.valueOf(rec.get(groupby.toUpperCase()));

                        if ( ((LinkedHashMap)result).get(groupkey) == null) {
                            LinkedList x = new LinkedList();

                            x.add(om);
                            ((LinkedHashMap)result).put(groupkey, x);
                        } else {
                            ((LinkedList)((LinkedHashMap)result).get(groupkey)).add(om);
                        }
                  } else {
                      ((LinkedList)result).add(om);
                  }
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
                     returnval = ortus.api.GroupByPath((LinkedList)result, groupbyoffset);
                else if ( groupby.equalsIgnoreCase("genre"))
                     returnval = ortus.api.GroupByGenre((LinkedList)result,groupbyoffset);
                else {
                    if ( groupbyoffset.isEmpty())
                        returnval = result;
                    else {
                        returnval = ((LinkedHashMap)result).get(groupbyoffset);
                    }
                }
            } else
                returnval = result;

            if ( returnval instanceof LinkedHashMap) {
                ortus.api.DebugLogTrace("Search: Returning Type: HashMap Num Keys: " + ((LinkedHashMap)returnval).keySet().size());
            } else if ( returnval instanceof LinkedList) {
                ortus.api.DebugLogTrace("Search: Returning Type: List Num Values: " + ((LinkedList)returnval).size());
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
