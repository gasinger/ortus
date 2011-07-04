package ortus.media.fanart;

import java.util.logging.Level;
import java.util.logging.Logger;
import ortus.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Object;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import ortus.media.metadata.item.Fanart;
import ortus.media.metadata.item.Media;
import ortus.media.metadata.item.Series;
import ortus.media.OrtusMedia;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

public class OrtusFanart extends ortus.vars implements IFanartProvider {

    private String seperator = File.separator;
    private String CentralTVFolder = GetFanartFolder() + seperator + "TV" + seperator;
	
	// All calls below are temporary until they are added to phoenix api. 
	// The return of Season calls returns series if no season is available. Always use phoenix
	// HasFanart calls before hand in studio to avoid nulls.



	@Override
	public String GetFanartFolder() {

		String fanartfolder = Configuration.GetProperty("ortus/fanart/folder", "None");

		if (fanartfolder.equalsIgnoreCase("none")) {
			fanartfolder = Ortus.getInstance().getBasePath() + java.io.File.separator +  "Fanart";
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


	public String GetFanartPosterFile(Object mediafile) {
                int mediaid = GetMediaID(mediafile);
//                ortus.api.DebugLogTrace("GetFanartPosterFile: ID: " + mediaid);
                Object medfil = MediaFileAPI.GetMediaFileForID(mediaid);
		File mf = MediaFileAPI.GetFileForSegment(medfil,0);
                String mfile = null;
                if ( mf.getAbsolutePath().contains("."))
		      mfile = mf.getAbsolutePath().substring(0, mf.getAbsolutePath().lastIndexOf("."));
                else
                      mfile = mf.getAbsolutePath() + java.io.File.separator + "folder";
//		ortus.api.DebugLog(LogLevel.Trace, "GetFanartPosterFile: " +  mfile + ".jpg");
		File faf = new File(mfile + ".jpg");
		if ( faf.exists())
			return faf.getAbsolutePath();
		else
			return null;
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
   

        private String GetFanart(Object mediafile, String fanarttype, String imagetype) {
                ortus.api.DebugLogTrace("Parms: Object: " + mediafile + " type: " + fanarttype + " ImageType: " + imagetype);
                List results = null;
                if ( mediafile instanceof OrtusMedia ) {
                    if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetFanart(fanarttype,imagetype);
                    } else if (((OrtusMedia) mediafile).IsSeries()) {
                        results = ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetFanart(fanarttype,imagetype);
                    } else if (((OrtusMedia)mediafile).IsEpisode()) {
//                        int seriesid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getSeriesid();
                        results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + ((OrtusMedia)mediafile).GetMediaID())).GetFanart(fanarttype,imagetype);
                    }
                } else if ( mediafile instanceof Integer ) {
                    results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + mediafile)).GetFanart(fanarttype,imagetype);
                } else if ( mediafile instanceof String) {
                    List<List> x = ortus.api.executeSQLQueryArray("select 's', seriesid from sage.series where title = '" + StringEscapeUtils.escapeSql((String)mediafile)  +"' union select 'm',mediaid from sage.media where mediatitle = '" + StringEscapeUtils.escapeSql((String)mediafile) +"'");
                    if ( x.size() > 0) {
                        if (((String)x.get(0).get(0)).equalsIgnoreCase("m")) {
                            results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + x.get(0).get(1))).GetFanart(fanarttype,imagetype);
                        } else {
                            results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("SR" + x.get(0).get(1))).GetFanart(fanarttype,imagetype);
                        }
                    }
                }else {
                    results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetFanart(fanarttype,imagetype);
                }

                if ( results == null) {
                    ortus.api.DebugLogTrace("fanart not found for: " + mediafile);
                    return null;
                }
                ortus.api.DebugLogTrace("results: size: " + results.size());
                if (results.size() > 0) {
                    for ( int x = 0; x < results.size();x++) {
                        ortus.api.DebugLogTrace("results: " + x + " file: " + ((Fanart)results.get(x)).getFile());
                        if ( ((Fanart)results.get(x)).getFile() != null) {                            
                            if ( ! ((Fanart)results.get(x)).getFile().equalsIgnoreCase("null") ) {
                                 ortus.api.DebugLogTrace("Return: Fanart: " + ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)results.get(x)).getFile());
                                 return ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)results.get(x)).getFile();
                            }
                        }
                    }
                    return null;
                } else {
                  return GetFanartPosterFile(mediafile);
      		}
	}
	private List<Object> GetFanartAll(Object mediafile, String fanarttype, String imagetype) {
              List results = null;
                if ( mediafile instanceof OrtusMedia ) {
                    if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetFanart(fanarttype,imagetype);
                    } else if (((OrtusMedia) mediafile).IsSeries()) {
                        results = ((Series)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).GetFanart(fanarttype,imagetype);
                    } else if (((OrtusMedia)mediafile).IsEpisode()) {
//                        int seriesid = ((Episode)ortus.cache.cacheEngine.getInstance().GetCache(((OrtusMedia)mediafile).GetKey())).getSeriesid();
                        results = ((Series)ortus.cache.cacheEngine.getInstance().GetCache("MD" + ((OrtusMedia)mediafile).GetMediaID())).GetFanart(fanarttype,imagetype);
                    }
                } else if ( mediafile instanceof String) {
                    List<List> x = ortus.api.executeSQLQueryArray("select 's', seriesid from sage.series where title = '" + StringEscapeUtils.escapeSql((String) mediafile) +"' union select 'm',mediaid from sage.media where mediatitle = '" + StringEscapeUtils.escapeSql((String) mediafile) +"'");
                    if ( x.size() > 0) {
                        if (((String)x.get(0).get(0)).equalsIgnoreCase("m")) {
                            results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + x.get(0).get(1))).GetFanart(fanarttype,imagetype);
                        } else {
                            results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("SR" + x.get(0).get(1))).GetFanart(fanarttype,imagetype);
                        }
                    }
                } else  {
                    results = ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + MediaFileAPI.GetMediaFileID(mediafile))).GetFanart(fanarttype,imagetype);
                }
                if (results.size() > 0) {
                    List<Object> ret = new ArrayList<Object>();
                    for ( Object o : results) {
                        if ( ((Fanart)o).getFile() != null) {
                            if ( ! ((Fanart)o).getFile().equalsIgnoreCase("null")) {
                                ret.add(ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)o).getFile());
                                ortus.api.DebugLogTrace("Adding Fanart: " + ((Fanart)o).getId() + " File: " + ((Fanart)o).getFile());
                            }
                        }

                    }
                    return ret;
                } else {
//                    return GetFanartPosterFile(mediafile);
                    return null;
      		}
	}

        /*
         * Default Medium Calls
         */
        public String GetFanartPoster(Object mediafile) {
            return GetFanartPosterMedium(mediafile);
        }
        public List<Object> GetFanartPosterAll(Object mediafile) {
            return GetFanartPosterMediumAll(mediafile);
        }
        public Object GetFanartPosterRandom(Object mediafile) {
            return GetFanartPosterMediumRandom(mediafile);
        }
         public String GetFanartBackground(Object mediafile) {
            return GetFanartBackgroundMedium(mediafile);
        }
        public List<Object> GetFanartBackgroundAll(Object mediafile) {
            return GetFanartBackgroundMediumAll(mediafile);
        }
        public Object GetFanartBackgroundRandom(Object mediafile) {
            return GetFanartBackgroundMediumRandom(mediafile);
        }
         public String GetFanartBanner(Object mediafile) {
            return GetFanartPosterMedium(mediafile);
        }
        public List<Object> GetFanartBannerAll(Object mediafile) {
            return GetFanartPosterMediumAll(mediafile);
        }
        public Object GetFanartBannerRandom(Object mediafile) {
            return GetFanartPosterMediumRandom(mediafile);
        }
        /*
         * Low Resolution Poster Calls
         */
	public String GetFanartPosterLow(Object mediafile) {
            return GetFanart(mediafile,"Posters","low");
	}
        public List<Object> GetFanartPosterLowAll(Object mediafile) {
            return GetFanartAll(mediafile,"Posters","low");
        }
        public Object GetFanartPosterLowRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Posters","low"));
	}
        /*
         * Medium Resolution Poster Calls
         */
        public String GetFanartPosterMedium(Object mediafile) {
            return GetFanart(mediafile,"Posters","medium");
	}
        public List<Object> GetFanartPosterMediumAll(Object mediafile) {
            return GetFanartAll(mediafile,"Posters","medium");
        }
        public Object GetFanartPosterMediumRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Posters","medium"));
	}
        /*
         * High Resolution Poster Calls
         */
        public String GetFanartPosterHigh(Object mediafile) {
            return GetFanart(mediafile,"Posters","high");
	}
        public List<Object> GetFanartPosterHighAll(Object mediafile) {
            return GetFanartAll(mediafile,"Posters","high");
        }
        public Object GetFanartPosterHighRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Posters","high"));
	}
        /*
         * Low Resolution Background Calls
         */
	public String GetFanartBackgroundLow(Object mediafile) {
            return GetFanart(mediafile,"Backgrounds","low");
	}
        public List<Object> GetFanartBackgroundLowAll(Object mediafile) {
            return GetFanartAll(mediafile,"Backgrounds","low");
        }
        public Object GetFanartBackgroundLowRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Backgrounds","low"));
	}
        /*
         * Medium Resolution Background Calls
         */
        public String GetFanartBackgroundMedium(Object mediafile) {
            return GetFanart(mediafile,"Backgrounds","medium");
	}
        public List<Object> GetFanartBackgroundMediumAll(Object mediafile) {
            return GetFanartAll(mediafile,"Backgrounds","medium");
        }
        public Object GetFanartBackgroundMediumRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Backgrounds","medium"));
	}
        /*
         * High Resolution Background Calls
         */
        public String GetFanartBackgroundHigh(Object mediafile) {
            return GetFanart(mediafile,"Backgrounds","high");
	}
        public List<Object> GetFanartBackgroundHighAll(Object mediafile) {
            return GetFanartAll(mediafile,"Backgrounds","high");
        }
        public Object GetFanartBackgroundHighRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Backgrounds","high"));
	}
                /*
         * Low Resolution Banner Calls
         */
	public String GetFanartBannerLow(Object mediafile) {
            return GetFanart(mediafile,"Banners","low");
	}
        public List<Object> GetFanartBannerLowAll(Object mediafile) {
            return GetFanartAll(mediafile,"Banners","low");
        }
        public Object GetFanartBannerLowRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Banners","low"));
	}
        /*
         * Medium Resolution Banner Calls
         */
        public String GetFanartBannerMedium(Object mediafile) {
            return GetFanart(mediafile,"Banners","medium");
	}
        public List<Object> GetFanartBannerMediumAll(Object mediafile) {
            return GetFanartAll(mediafile,"Banners","medium");
        }
        public Object GetFanartBannerMediumRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Banners","medium"));
	}
        /*
         * High Resolution Banner Calls
         */
        public String GetFanartBannerHigh(Object mediafile) {
            return GetFanart(mediafile,"Banners","high");
	}
        public List<Object> GetFanartBannerHighAll(Object mediafile) {
            return GetFanartAll(mediafile,"Banners","high");
        }
        public Object GetFanartBannerHighRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Banners","high"));
	}

        /* Low Resolution Banner Calls
         */
	public String GetFanartEpisodeLow(Object mediafile) {

            return GetFanart(mediafile,"Episode-" + ortus.api.GetEpisodeID(mediafile) + "-Posters","low");
	}
        public List<Object> GetFanartEpisodeLowAll(Object mediafile) {
            return GetFanartAll(mediafile,"Episode-" + ortus.api.GetEpisodeID(mediafile) + "-Posters","low");
        }
        public Object GetFanartEpisodeLowRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Episode-" + ortus.api.GetEpisodeID(mediafile) + "-Posters","low"));
	}
        /*
         * Medium Resolution Banner Calls
         */
        public String GetFanartEpisodeMedium(Object mediafile) {
            return GetFanart(mediafile,"Episode-" + ortus.api.GetEpisodeID(mediafile) + "-Posters","medium");
	}
        public List<Object> GetFanartEpisodeMediumAll(Object mediafile) {
            return GetFanartAll(mediafile,"Episode-" + ortus.api.GetEpisodeID(mediafile) + "-Posters","medium");
        }
        public Object GetFanartEpisodeMediumRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Episode-" + ortus.api.GetEpisodeID(mediafile) + "-Posters","medium"));
	}
        /*
         * High Resolution Banner Calls
         */
        public String GetFanartEpisodeHigh(Object mediafile) {
            return GetFanart(mediafile,"Episode-" + ortus.api.GetEpisodeID(mediafile) + "-Posters","high");
	}
        public List<Object> GetFanartEpisodeHighAll(Object mediafile) {
            return GetFanartAll(mediafile,"Episode-" + ortus.api.GetEpisodeID(mediafile) + "-Posters","high");
        }
        public Object GetFanartEpisodeHighRandom(Object mediafile) {
            return GetRandom(GetFanartAll(mediafile,"Episode-" + ortus.api.GetEpisodeID(mediafile) + "-Posters","high"));
	}

        public Object GetRandom(List<Object> fanart) {
            if ( fanart == null)
                return null;
            if ( fanart.size() == 0)
                return null;
            int Min = 0;
            int Max = fanart.size() - 1;
            int x = Min + (int)(Math.random() * ((Max - Min) + 1));
            return fanart.get(x);
        }
        public Object GetFanartForID(int id, String resolution) {
		ortus.api.DebugLog(LogLevel.Trace2, " GetFanartForID: fanart for: " + id + " with resolution: " + resolution);
                String fileField = resolution + "_file";
		List<List> result = ortus.api.executeSQLQueryArray("select " + fileField + " from sage.fanart where id = " + id);
		if ( result.size() > 0)
			return ortus.api.GetFanartFolder() + java.io.File.separator + (String)result.get(0).get(0);
		else
			return null;
	}

	public Object GetCastFanartLow(String castname) {
		ortus.api.DebugLog(LogLevel.Trace2, " GetCastFanartLow: fanart for: " + castname);
		List<List> result = ortus.api.executeSQLQueryArray("select low_file from sage.fanart where type = 'Cast-" + castname + "'");
		if ( result.size() > 0)
			return ortus.api.GetFanartFolder() + java.io.File.separator + (String)result.get(0).get(0);
		else
			return null;
	}
      	public Object GetCastFanartMedium(String castname) {
		ortus.api.DebugLog(LogLevel.Trace2, " GetCastFanartMedium: fanart for: " + castname);
		List<List> result = ortus.api.executeSQLQueryArray("select medium_file from sage.fanart where type = 'Cast-" + castname + "'");
		if ( result.size() > 0)
			return ortus.api.GetFanartFolder() + java.io.File.separator + (String)result.get(0).get(0);
		else
			return null;
	}
	public Object GetCastFanartHigh(String castname) {
		ortus.api.DebugLog(LogLevel.Trace2, " GetCastFanartHigh: fanart for: " + castname);
		List<List> result = ortus.api.executeSQLQueryArray("select high_file from sage.fanart where type = 'Cast-" + castname + "'");
		if ( result.size() > 0)
			return ortus.api.GetFanartFolder() + java.io.File.separator + (String)result.get(0).get(0);
		else
			return null;
	}

	public Object GetTVFanart(String seriesid, String type, String quality) {
                String SQL = null;
		List<HashMap> sqlresult = ortus.api.executeSQLQueryHashCache("select * from sage.fanart where low_file is not null and idtype= 'SR' and mediaid = " + seriesid + " and type = '" + type + "'");
		if (sqlresult.size() < 1) {
			return null;
		}

                if ( quality.equalsIgnoreCase("high")) {
                    return ortus.api.GetFanartFolder() + java.io.File.separator + (String) sqlresult.get(0).get("HIGH_FILE");
                } else if ( quality.equalsIgnoreCase("medium")) {
                    return ortus.api.GetFanartFolder() + java.io.File.separator + (String) sqlresult.get(0).get("MEDIUM_FILE");
                } else {
                    return ortus.api.GetFanartFolder() + java.io.File.separator + (String) sqlresult.get(0).get("LOW_FILE");
                }		
	}

       	public List<Object> GetTVFanartAll(String seriesid, String type, String quality) {
                String SQL = null;
                List<Object>results = new ArrayList<Object>();
		List<HashMap> sqlresult = ortus.api.executeSQLQueryHashCache("select * from sage.fanart where low_file is not null and idtype= 'SR' and mediaid = " + seriesid + " and type = '" + type + "'");
		if (sqlresult.size() < 1) {
			return null;
		}

                for ( HashMap x : sqlresult) {
                    if ( quality.equalsIgnoreCase("high")) {
                        results.add(ortus.api.GetFanartFolder() + java.io.File.separator + (String) x.get("HIGH_FILE"));
                    } else if ( quality.equalsIgnoreCase("medium")) {
                        results.add(ortus.api.GetFanartFolder() + java.io.File.separator + (String) x.get("MEDIUM_FILE"));
                    } else {
                        results.add(ortus.api.GetFanartFolder() + java.io.File.separator + (String) x.get("LOW_FILE"));
                    }
                }

                return results;
	}

	public Object GetTVFanartRandom(String seriesid, String type, String quality) {
		return GetRandom(GetTVFanartAll(seriesid, type, quality));
   	}

	public Object GetSeasonFanartPoster(Object mediafile) {
                String SQL = null;
                int SageMediaID = 0;
                if ( mediafile instanceof OrtusMedia) {
                    if ( ((OrtusMedia)mediafile).IsEpisode()) {
                        List<Object> x = ortus.api.executeSQLQuery("select mediaid from sage.episode where episodeid = " + ((OrtusMedia)mediafile).GetID());
                        if ( x.size() > 0) {
                            try { SageMediaID = Integer.parseInt((String)x.get(0)); } catch ( Exception e) {}
                        } 
                    } else if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        SageMediaID = ((OrtusMedia)mediafile).GetID();
                    }
                } else {
                    SageMediaID = MediaFileAPI.GetMediaFileID(mediafile);
                }
		List<Object> sqlresult = ortus.api.executeSQLQueryCache("select seasonno from sage.episode as e, sage.series as s where e.seriesid = s.seriesid and e.mediaid = " + SageMediaID);
		if (sqlresult.size() < 1) {
			return null;
		}
		List<Object> result = (List) ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + SageMediaID)).GetFanart("Season-" + sqlresult.get(0) + "-Posters","medium");

		if (result == null || result.size() == 0) {
                        return GetFanartPoster(SageMediaID);
		} else {
			return ortus.api.GetFanartFolder() + java.io.File.separator + (String) result.get(0);
		}
	}

	public List<Object> GetSeasonFanartPosterAll(Object mediafile) {
                String SQL = null;
                int SageMediaID = 0;
                if ( mediafile instanceof OrtusMedia) {
                    if ( ((OrtusMedia)mediafile).IsEpisode()) {
                        List<Object> x = ortus.api.executeSQLQuery("select mediaid from sage.episode where episodeid = " + ((OrtusMedia)mediafile).GetID());
                        if ( x.size() > 0) {
                            try { SageMediaID = Integer.parseInt((String)x.get(0)); } catch ( Exception e) {}
                        }
                    } else if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        SageMediaID = ((OrtusMedia)mediafile).GetID();
                    }
                } else {
                    SageMediaID = MediaFileAPI.GetMediaFileID(mediafile);
                }

		List<Object> sqlresult = ortus.api.executeSQLQueryCache("select seasonno from sage.episode as e, sage.series as s where e.seriesid = s.seriesid and e.mediaid = " + SageMediaID);
		if (sqlresult.size() < 1) {
			return null;
		}

		List<Object> result = (List) ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD" + SageMediaID)).GetFanart("Season-" + sqlresult.get(0) + "-Posters","medium");

		return result;
	}

	public Object GetSeasonFanartBanner(Object mediafile) {
                String SQL = null;
                int SageMediaID = 0;
                if ( mediafile instanceof OrtusMedia) {
                    if ( ((OrtusMedia)mediafile).IsEpisode()) {
                        List<Object> x = ortus.api.executeSQLQuery("select mediaid from sage.episode where episodeid = " + ((OrtusMedia)mediafile).GetID());
                        if ( x.size() > 0) {
                            try { SageMediaID = Integer.parseInt((String)x.get(0)); } catch ( Exception e) {}
                        }
                    } else if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        SageMediaID = ((OrtusMedia)mediafile).GetID();
                    }
                } else {
                    SageMediaID = MediaFileAPI.GetMediaFileID(mediafile);
                }

		List<Object> sqlresult = ortus.api.executeSQLQueryCache("select seasonno from sage.episode as e, sage.series as s where e.seriesid = s.seriesid and e.mediaid = " + SageMediaID);
		if (sqlresult.size() < 1) {
			return null;
		}

		List<Object> result = (List) ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD"+SageMediaID)).GetFanart("Season-" + sqlresult.get(0) + "-Banners","medium");

		if (result == null) {
			return null;
		} else {
			return ortus.api.GetFanartFolder() + java.io.File.separator + (String) result.get(0);
		}
	}

	public List<Object> GetSeasonFanartBannerAll(Object mediafile) {
                String SQL = null;
                int SageMediaID = 0;
                if ( mediafile instanceof OrtusMedia) {
                    if ( ((OrtusMedia)mediafile).IsEpisode()) {
                        List<Object> x = ortus.api.executeSQLQuery("select mediaid from sage.episode where episodeid = " + ((OrtusMedia)mediafile).GetID());
                        if ( x.size() > 0) {
                            try { SageMediaID = Integer.parseInt((String)x.get(0)); } catch ( Exception e) {}
                        }
                    } else if ( ((OrtusMedia)mediafile).IsMediaFile()) {
                        SageMediaID = ((OrtusMedia)mediafile).GetID();
                    }
                } else {
                    SageMediaID = MediaFileAPI.GetMediaFileID(mediafile);
                }

		List<Object> sqlresult = ortus.api.executeSQLQueryCache("select seasonno from sage.episode as e, sage.series as s where e.seriesid = s.seriesid and e.mediaid = " + SageMediaID);
		if (sqlresult.size() < 1) {
			return null;
		}

		List<Object> result = (List) ((Media)ortus.cache.cacheEngine.getInstance().GetCache("MD"+SageMediaID)).GetFanart("Season-" + sqlresult.get(0) + "-Banners","medium");

		return result;
	}

	public List<String> GetMenuBackgrounds(String Menutype) {

		String Path = GetFanartFolder() + "\\" + Menutype;
		File directory = new File(Path);
		File[] AllDirectories = directory.listFiles(new OrtusDirectoryFilter());
		ArrayList<String> AllFanart = new ArrayList<String>();
		ortus.api.DebugLog(3, "Directory: " + Path + " Size of Subfolders:" + AllDirectories.length);
		if (AllDirectories != null) {
			for (int i = 0; i < AllDirectories.length - 3; i++) {
				String SubPath = AllDirectories[i].toString() + "\\Backgrounds";
				ortus.api.DebugLog(3, "Adding + SubDirectory: " + i + SubPath);
				File tempbglist = new File(SubPath);
				if (tempbglist.isDirectory()) {
					File[] tempbgs = tempbglist.listFiles(new OrtusImageFilter());
					if (tempbgs != null && tempbgs.length != 0) {
						AllFanart.add(tempbgs[0].toString());
					}
				}
			}

			return AllFanart;
		}

		ortus.api.DebugLog(1, "AllFanart equals null");
		return null;
	}

	public String GetMenuBackground(String MenuType) {
		if (MenuType.contains("tv")) {
			MenuType = "TV";
		}
		if (MenuType.contains("movie")) {
			MenuType = "Movies";
		}
		if (MenuType.contains("music")) {
			MenuType = "Music";
		}
		String Path = GetFanartFolder() + "\\" + MenuType;
		File directory = new File(Path);
		File[] AllDirectories = directory.listFiles(new OrtusDirectoryFilter());
		File[] AllFanart = null;
		ortus.api.DebugLog(3, "Directory: " + Path);
		if (AllDirectories != null) {
			for (int i = 0; AllFanart == null && i < AllDirectories.length; i++) {
				int randint = phoenix.api.GetRandomNumber(AllDirectories.length);
				String SubPath = AllDirectories[randint].toString() + "\\Backgrounds";
				ortus.api.DebugLog(3, "SubDirectory: " + SubPath);
				File subdirectory = new File(SubPath);
				AllFanart = subdirectory.listFiles(new OrtusImageFilter());
			}

			return AllFanart[0].toString();
		}
		ortus.api.DebugLog(1, "AllFanart equals null");

		return null;
	}

// Creates Fanart Posters for all folder.jpg's for a given Array of movies. Will check to make sure no fanart poster and baackgrounds
// exist and then will create Movie folder and poster folder based off @MediaTitle same method as BMI uses to create fanart.
// This allows those not wanting to use BMI to use newer stvi's which require the fanart structure.
// @ MediaObjects = Array of mediaobjects with possible folder.jpg's. Jpeg does not have to be called folder.jpg just must reside in main movie folder.
// Will move multiple jpg's if they exist. Background fanart must contain the word "background" in the name of the jpg.
	public void CreateFanartFromJPG(Object[] MediaObjects) {
		if (MediaObjects.length > 0) {
			for (int j = 0; j < MediaObjects.length; j++) {

				Object MediaObject = MediaObjects[j];

				String Title = GetSafeTitle(MediaObject);
				Boolean hasfanart = phoenix.api.HasFanartPoster(MediaObject);
				if (Title != null && Title.length() != 0 && hasfanart == false) {
					// commented out for thumbs maybe at a later date not complete need to evaluate further
					//String SThumb = MediaFileAPI.GetThumbnail(MediaObject).toString();
					//Global.ortus.api.DebugLog("Before Stringing  " + SThumb );
					//SThumb = SThumb.substring(SThumb.indexOf("[")+1,SThumb.indexOf("#"));
					//File FThumb = new File(SThumb);
					//Global.ortus.api.DebugLog("After Stringing" + SThumb);
					// Again commented out for thumbs
					//if (FThumb.isFile()){
					//Global.ortus.api.DebugLog("IsFile = true");
					//boolean success = FThumb.renameTo(new File(FDirectory,FThumb.getName()));}

					File PosterDirectory = new File(GetFanartFolder() + "\\Movies\\" + Title + "\\Posters");
					File BackgroundDirectory = new File(GetFanartFolder() + "\\Movies\\" + Title + "\\Backgrounds");
					File jpgfolder = new File(MediaFileAPI.GetParentDirectory(MediaObject).toString() + "\\");
					File[] jpegs = jpgfolder.listFiles(new OrtusImageFilter());
					if (jpegs.length != 0 && jpegs != null) {
						if (PosterDirectory.exists() == false) {
							ortus.api.DebugLog(3, "Making New Directory " + PosterDirectory);
							boolean success1 = PosterDirectory.mkdirs();
							if (success1 == true) {
								ortus.api.DebugLog(3, "Movie Directory made " + PosterDirectory);
							}
							if (success1 == false) {
								ortus.api.DebugLog(1, "Movie Directory failed creation " + PosterDirectory);
							}
						}
						for (int i = 0; i < jpegs.length; i++) {
							File Folderjpg = jpegs[i];
							ortus.api.DebugLog(3, "Folderjpg exist " + Folderjpg);
							if (PosterDirectory.exists() && !Folderjpg.toString().toLowerCase().contains("background") && !Folderjpg.toString().toLowerCase().contains("fanart")) {
								try {
									copy(Folderjpg, new File(PosterDirectory.toString() + Folderjpg.toString().substring(Folderjpg.toString().lastIndexOf("\\"))));
								} catch (Exception ex) {
								}
								boolean success3 = new File(PosterDirectory.toString() + Folderjpg.toString().substring(Folderjpg.toString().lastIndexOf("\\"))).exists();
								if (success3 == true);
								{
									ortus.api.DebugLog(3, "File copied");
								}
								if (success3 == false) {
									ortus.api.DebugLog(1, "File not copied");
								}
							} else if (Folderjpg.toString().toLowerCase().contains("background") || Folderjpg.toString().toLowerCase().contains("fanart")) {
								ortus.api.DebugLog(3, "jpg is background checking for background directory");
								if (BackgroundDirectory.exists() == false) {
									ortus.api.DebugLog(3, "Making New Background Directory " + BackgroundDirectory);
									boolean success1 = BackgroundDirectory.mkdirs();
									if (success1 == true) {
										ortus.api.DebugLog(3, "Movie Directory made " + BackgroundDirectory);
									}
									if (success1 == false) {
										ortus.api.DebugLog(1, "Movie Directory failed creation " + BackgroundDirectory);
									}
								}
								try {
									copy(Folderjpg, new File(BackgroundDirectory.toString() + Folderjpg.toString().substring(Folderjpg.toString().lastIndexOf("\\"))));
								} catch (Exception ex) {
								}
								boolean success3 = new File(PosterDirectory.toString() + Folderjpg.toString().substring(Folderjpg.toString().lastIndexOf("\\"))).exists();
								if (success3 == true);
								{
									ortus.api.DebugLog(3, "File copied");
								}
								if (success3 == false) {
									ortus.api.DebugLog(1, "File not copied");
								}
							}

							if (!PosterDirectory.exists()) {
								ortus.api.DebugLog(1, "Cannot Create Folder structure error in Title return");
							}
						}
					} else {
						ortus.api.DebugLog(1, "No folderjpegs found for title " + Title);
					}
				} else if (Title == null || Title.length() == 0 && hasfanart == false) {
					ortus.api.DebugLog(1, "No Poster Created for Movie not title found");
				} else if (hasfanart == true) {
					ortus.api.DebugLog(3, "Has Fanart Poster no jpeg conversion needed" + Title);
				}
			}
		}
	}

// Returns a ArrayList of strings of "MediaTitles" for fanart folders that exist but there is no matching title in the SageDB.
// Can then be passed to FanartCleanupMove to move this files.
//@ Object[] mediafiles passed from Sage ie. GetMediaFiles()
//@ String Type = "Movies" or "TV" for type of media files being passed.
	public Map<String, List> GetFanartCleanupList(Object[] MediaObjects, String Type) {
		ArrayList<String> Titles = new ArrayList<String>();
		for (int a = 0; a < MediaObjects.length; a++) {
			Titles.add(a, GetSafeTitle(MediaObjects[a]));
		}
		String Path = GetFanartFolder() + "\\" + Type;
		File directory = new File(Path);
		File[] AllDirectories = directory.listFiles(new OrtusDirectoryFilter());
		Map<String, List> NoFiles = new HashMapImpl();

		if (AllDirectories != null && AllDirectories.length != 0) {
			for (int i = 0; i < AllDirectories.length; i++) {
				String CurrDirectory = AllDirectories[i].toString();
				CurrDirectory = CurrDirectory.substring(CurrDirectory.lastIndexOf("\\") + 1);
				ortus.api.DebugLog(3, "Current Directory searching titles: " + CurrDirectory);
				boolean found = false;
				for (int j = 0; j < Titles.size() && found == false; j++) {
					String Title = Titles.get(j);
					if (Title.equalsIgnoreCase(CurrDirectory)) {
						ortus.api.DebugLog(3, "Title Found: " + CurrDirectory);
						Titles.remove(Titles.get(j));
						ortus.api.DebugLog(5, "Removing found title : " + Titles.size());
						found = true;
					} else {
						ortus.api.DebugLog(5, "Next Title  " + j);
					}
				}
				if (found == false) {
					ortus.api.DebugLog(3, "Fanart has no title associated added to list : " + CurrDirectory);
					String Key = AllDirectories[i].toString();
					NoFiles.put(Key, ortus.util.file.GetFolderDetail(Key));

				}
			}
		}

		return NoFiles;
	}

// Takes ArrayList from GetFanartCleanupList only!!! Moves those files to default folder of
//  "centralfanart folder//unused//ddyymm//type"
// Moves all folders passed but keeps original folder structure
//@ ArrayList<String> - accepts arraylist passed from GetFanartCleanupList
//@ Type - accepts type of media ("TV", "Movies")
	public void FanartCleanupMove(HashMap<String, List> FoldersMap, String Type) {
		Object[] Folders = api.toArray(FoldersMap);
		for (int i = 0; i < Folders.length; i++) {
			String Date = getDateTime();
			if (Type.equals("All")) {
				if (Folders[i].toString().contains("TV")) {
					Type = "TV";
				} else {
					Type = "Movies";
				}
			}
			String Folder = GetFanartFolder() + "\\unused\\" + Date + "\\" + Type;
			File Directory = new File(Folder);
			Directory.mkdirs();
			if (Directory.canRead() && Directory.canWrite()) {
				ortus.api.DebugLog(3, "Folders Files being moved to: " + Folder);
				File ODirectoryF = new File(Folders[i].toString());
				boolean success = ODirectoryF.renameTo(new File(Directory, ODirectoryF.getName()));
				if (success == true) {
					ortus.api.DebugLog(3, "Directories moved " + Folder);
				} else {
					ortus.api.DebugLog(3, "Directory failed to move : " + Folder);
				}
			} else {
				ortus.api.DebugLog(1, "Read/Write access denied");
			}
		}
	}

	public String GetShowThumbnail(Object MediaObject) {
		int MediaID = MediaFileAPI.GetMediaFileID(MediaObject);
		List results = ortus.api.executeSQLQuery("Select mediatitle,season,episode FROM sage.mediatv WHERE mediaid=" + MediaID);
		String path = "";
		if (!results.isEmpty()) {
			path = CentralTVFolder + results.get(0) + seperator + "Thumbnails" + results.get(0) + "S"
				+ results.get(2) + "E" + results.get(3) + ".jpg";
			File thumb = new File(path);
			if (!thumb.exists()) {
				path = "";
			}
		}
		return path;
	}

	public void FanartCleanupDelete(HashMap<String, List> FoldersMap) {
		Object[] Folders = api.toArray(FoldersMap);
		for (int i = 0; i < Folders.length; i++) {
			File CurrDirectory = new File(Folders[i].toString());
			ortus.api.DebugLog(3, "Directory to delete " + CurrDirectory);
			if (CurrDirectory.canRead() && CurrDirectory.canWrite()) {
				delete(CurrDirectory);
				CurrDirectory.delete();
			} else {
				ortus.api.DebugLog(1, "Cannot get read and write acess");
			}
		}
	}

	public void copy(File src, File dst) {
		InputStream in = null;
		try {
			in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception ex) {
			Logger.getLogger(OrtusFanart.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				in.close();
			} catch (Exception ex) {
				Logger.getLogger(OrtusFanart.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private static String GetSafeTitle(Object MediaObject) {
		String Title = api.GetMediaTitle(MediaObject);
		if (Title == null || Title.length() == 0) {
			Title = ShowAPI.GetShowEpisode(MediaObject);
		}
		return sagex.phoenix.fanart.FanartUtil.createSafeTitle(Title);
	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		Date date = new Date();
		return dateFormat.format(date);
	}

	private void delete(File folder) {
		if (folder.exists()) {
			ortus.api.DebugLog(3, "Folder Exist");
			File[] files = folder.listFiles();
			ortus.api.DebugLog(3, "Size of files in Folder" + folder + folder.length());
			for (int i = 0; i < files.length; i++) {
				File oFileCur = files[i];
				if (oFileCur.isDirectory()) {
					// call itself to delete the contents of the current folder
					delete(oFileCur);
				}
				oFileCur.delete();
			}
		}

	}

	private class HashMapImpl extends HashMap<String, List> {

		public HashMapImpl() {
		}
	}
}

class OrtusImageFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.getName().contains("jpg");
	}
}

class OrtusDirectoryFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}
}




