package ortus.onlinescrapper.thetvdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import ortus.onlinescrapper.MediaObject;
import ortus.onlinescrapper.tools.XMLHelper;
import ortus.onlinescrapper.tools.database;
import ortus.onlinescrapper.tools.urldownload;
import sagex.api.Configuration;

/**
 * @author altman.matthew
 */
public class TheTVDB extends ortus.vars {

    private String apiKey;
    private String xmlMirror;
    private String zipMirror;
    private String bannerMirror;
    private Mirrors mirrors = null;
    //private String zipMirror;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public TheTVDB() {
	    InitializeTheTVDB("694E79769B377E8D");
    }
    
    private void InitializeTheTVDB(String apiKey) {
        mirrors = new Mirrors(apiKey);
        xmlMirror = mirrors.getMirror(Mirrors.TYPE_XML) + "/api/";
        zipMirror = mirrors.getMirror(Mirrors.TYPE_ZIP) + "/api/";
        bannerMirror = mirrors.getMirror(Mirrors.TYPE_BANNER) + "/banners/";
        //zipMirror = mirrors.getMirror(Mirrors.TYPE_ZIP);

        this.apiKey = apiKey;
}
    
    public Series getSeries(String pathfilename) {
        Series series = null;
        
        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLHelper.getEventReaderFile(pathfilename + java.io.File.separator + "en.xml");

            series = parseNextSeries(xmlReader);
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error,"TheTVDB: Series error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }
        
        return series;
    }

    public Series getSeriesUrl(String seriesid) {
        Series series = null;

        XMLEventReader xmlReader = null;
	ortus.api.DebugLog(LogLevel.Trace2,"getSeriesUrl: URL: " +xmlMirror + apiKey + "/series/" + seriesid + "/en.xml");
        try {
            xmlReader = XMLHelper.getEventReaderUrl(xmlMirror + apiKey + "/series/" + seriesid + "/en.xml");

            series = parseNextSeries(xmlReader);
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error,"TheTVDB: getSeriesUrl error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }

        return series;
    }

    public Episode getEpisode(String pathfilename) {
        Episode episode = null;
        
        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLHelper.getEventReaderFile(pathfilename + java.io.File.separator + "en.xml");

            episode = parseNextEpisode(xmlReader);
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error,"TheTVDB: Episode error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }
        
        return episode;
    }
    
    public Episode getEpisodeUrl(String episodeid) {
        Episode episode = null;

        XMLEventReader xmlReader = null;
	ortus.api.DebugLog(LogLevel.Trace2,"getEpisodeUrl: URL: " +xmlMirror + apiKey + "/episodes/" + episodeid + "/en.xml");
        try {
            xmlReader = XMLHelper.getEventReaderUrl(xmlMirror + apiKey + "/episodes/" + episodeid + "/en.xml");

            episode = parseNextEpisode(xmlReader);
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error,"TheTVDB: Episode error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }

        return episode;
    }


    public List<Episode> getEpisodes(String pathfilename) {
        List<Episode> results = new ArrayList<Episode>();
        
        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLHelper.getEventReaderFile(pathfilename + java.io.File.separator + "en.xml");

            while (xmlReader.hasNext()) {
                Episode episode = parseNextEpisode(xmlReader);
                if (episode != null) {
                    results.add(episode);
                }
            }
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error, "TheTVDB: Episodes error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }

//        Collections.sort(results);
        return results;
    }

    public Episode getDVDEpisode(String id, int seasonNbr, int episodeNbr, String language) {
        Episode episode = null;

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLHelper.getEventReaderUrl(xmlMirror + apiKey + "/series/" + id + "/dvd/" + seasonNbr + "/" + episodeNbr + "/" + (language!=null?language+".xml":""));

            episode = parseNextEpisode(xmlReader);
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error, "TheTVDB: DVDEpisode error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }

        return episode;
    }

//    public String getSeasonYear(String id, int seasonNbr, String language) {
//        String year = null;
//
//        Episode episode = getEpisode("123");
//        if (episode != null) {
//            if (episode.getFirstAired() != null && !episode.getFirstAired().isEmpty()) {
//                try {
//                    Date date = dateFormat.parse(episode.getFirstAired());
//                    if (date != null) {
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(date);
//                        year = ""+cal.get(Calendar.YEAR);
//                    }
//                } catch (Exception ignore) {}
//            }
//        }
//
//        return year;
//    }

    public Banners getBanners(String pathfilename) {
        Banners banners = new Banners();
        
        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLHelper.getEventReaderFile(pathfilename + java.io.File.separator + "banners.xml");

            while (xmlReader.hasNext()) {
                Banner banner = parseNextBanner(xmlReader);
                if (banner != null) {
//                    ortus.api.DebugLogTrace("Extracted: Banner: Type: " + banner.getBannerType() + " Type2: " + banner.getBannerType2() + " Path: " + banner.getUrl());
                    banners.addBanner(banner);
                }
            }
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error, "TheTVDB: Banners error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }
        
        return banners;
    }
    
    public List<Actor> getActors(String pathfilename) {
        List<Actor> results = new ArrayList<Actor>();
        
        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLHelper.getEventReaderFile(pathfilename + java.io.File.separator + "actors.xml");

            while (xmlReader.hasNext()) {
                Actor actor = parseNextActor(xmlReader);
                if (actor != null) {
                    results.add(actor);
                }
            }
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error, "TheTVDB: Actors error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }
        
        Collections.sort(results);
        return results;
    }
    
    public void getServerTime() {
	long t0 = System.currentTimeMillis();

        HashMap<String,Series> results = new HashMap<String,Series>();

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLHelper.getEventReaderUrl("http://www.thetvdb.com/api/Updates.php?type=none");

            while (xmlReader.hasNext()) {
                long servertime = parseNextTime(xmlReader);
		if ( servertime > 0) {
			Configuration.SetServerProperty("ortus/tvdb/time", String.valueOf(servertime));
                }
            }
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error, "TheTVDB: update time error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }

	long ttotal = System.currentTimeMillis() - t0;
	ortus.api.DebugLog(LogLevel.Trace, "TheTVDB: get server time in " + ttotal + " ms");

        return;
    }

    public void getUpdates() {
	long t0 = System.currentTimeMillis();
	long servertime = Long.parseLong(Configuration.GetServerProperty("ortus/tvdb/time","0"));
	if ( servertime == 0)
		return;

        XMLEventReader xmlReader = null;
        try {
            xmlReader = XMLHelper.getEventReaderUrl("http://www.thetvdb.com/api/Updates.php?type=all&time=" + String.valueOf(servertime));

            while (xmlReader.hasNext()) {

                String ret = parseNextUpdate(xmlReader);

		if ( ! ret.isEmpty()) {
			String[] updcmd = ret.split(":");
			ortus.api.DebugLog(LogLevel.Trace2,"Updatecmd: " + updcmd[0] + " value: " + updcmd[1]);
			if ( updcmd[0].equalsIgnoreCase("t")) {
				ortus.api.DebugLog(LogLevel.Trace2,"getUpdates: Updating server time: " + updcmd[1]);
				Configuration.SetServerProperty("ortus/tvdb/time", updcmd[1]);
			} else if ( updcmd[0].equalsIgnoreCase("s")) {
				ortus.api.DebugLog(LogLevel.Trace2,"getUpdates: Updating Series: " + updcmd[1]);
				Series x = getSeriesUrl(updcmd[1]);
				if ( x != null)
					database.UpdateSeriesDB(x);
			} else if ( updcmd[0].equalsIgnoreCase("e")) {
				ortus.api.DebugLog(LogLevel.Trace2,"getUpdates: Updating Episode: " + updcmd[1]);
				Episode x = getEpisodeUrl(updcmd[1]);
				if ( x != null)
					database.UpdateEpisodeDB(x);
			}
		}
            }
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error, "TheTVDB: Series error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }

	long ttotal = System.currentTimeMillis() - t0;
	ortus.api.DebugLog(LogLevel.Trace, "TheTVDB: getUpdates in " + ttotal + " ms");

        return;
    }

    public HashMap<String,Series> searchSeries(String title, String language) {
	long t0 = System.currentTimeMillis();
        HashMap<String,Series> results = new HashMap<String,Series>();
        
        XMLEventReader xmlReader = null;
        try {
//	    ortus.api.DebugLog(LogLevel.Trace2,"TVDB geturl: " + xmlMirror + "GetSeries.php?seriesname=" + URLEncoder.encode(title, "UTF-8") + (language!=null?"&language="+language:""));
//            xmlReader = XMLHelper.getEventReaderUrl(xmlMirror + "GetSeries.php?seriesname=" + URLEncoder.encode(title, "UTF-8") + (language!=null?"&language="+language:""));
	    ortus.api.DebugLog(LogLevel.Trace,"TVDB geturl: " + xmlMirror + "GetSeries.php?seriesname=" + URLEncoder.encode(ortus.onlinescrapper.tools.parser.CleanName(title), "UTF-8"));
            xmlReader = XMLHelper.getEventReaderUrl(xmlMirror + "GetSeries.php?seriesname=" + URLEncoder.encode(ortus.onlinescrapper.tools.parser.CleanName(title), "UTF-8"));

            while (xmlReader.hasNext()) {
                Series series = parseNextSeries(xmlReader);
                if (series != null) {
                    results.put(series.getSeriesName(),series);
                }
            }
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error, "TheTVDB: Series error: " + error.getMessage());
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }

	long ttotal = System.currentTimeMillis() - t0;
	ortus.api.DebugLog(LogLevel.Info, "TheTVDB: Search returned " + results.size() + " in " + ttotal + " ms");

        return results;
    }

    private long parseNextTime(XMLEventReader xmlReader) throws XMLStreamException {
        long servertime = 0;

        while (xmlReader.hasNext()) {
            XMLEvent event = xmlReader.nextEvent();

            if (event.isStartElement()) {
                String tag = event.toString();
                if (tag.equalsIgnoreCase("<Time>")) {
                    servertime = Long.parseLong(XMLHelper.getCData(xmlReader));
		}
            } else if (event.isEndElement()) {
                if (event.toString().equalsIgnoreCase("</Time>")) {
                    break;
                }
            }
        }
        return servertime;
    }

    private String parseNextUpdate(XMLEventReader xmlReader) throws XMLStreamException {
        String result="";

        while (xmlReader.hasNext()) {
            XMLEvent event = xmlReader.nextEvent();

            if (event.isStartElement()) {
                String tag = event.toString();
                if (tag.equalsIgnoreCase("<Time>")) {
                    result = "t:" + XMLHelper.getCData(xmlReader);
		} else if ( tag.equalsIgnoreCase("<Series>")) {
		    result = "s:" + XMLHelper.getCData(xmlReader);
		} else if ( tag.equalsIgnoreCase("<Episode>")) {
		    result = "e:" + XMLHelper.getCData(xmlReader);
		}
            } else if (event.isEndElement()) {
                if (event.toString().equalsIgnoreCase("</Time>")) 
                    break;
                if (event.toString().equalsIgnoreCase("</Series>"))
                    break;
                if (event.toString().equalsIgnoreCase("</Episode>"))
                    break;
            }
        }
        return result;
    }
    
    private Series parseNextSeries(XMLEventReader xmlReader) throws XMLStreamException {
        Series series = null;
        
        while (xmlReader.hasNext()) {
            XMLEvent event = xmlReader.nextEvent();

            if (event.isStartElement()) {
                String tag = event.toString();
                if (tag.equalsIgnoreCase("<Series>")) {
                    series = new Series();
                } else if (tag.equalsIgnoreCase("<seriesid>")) {
                    series.setSeriesId(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<language>")) {
                    series.setLanguage(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<SeriesName>")) {
                    series.setSeriesName(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<banner>")) {
                    String s = XMLHelper.getCData(xmlReader);
                    if (!s.isEmpty()) {
                        series.setBanner(bannerMirror + s);
                    }
                } else if (tag.equalsIgnoreCase("<Overview>")) {
                    series.setOverview(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<FirstAired>")) {
                    series.setFirstAired(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<IMDB_ID>")) {
                    series.setImdbId(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<zap2it_id>")) {
                    series.setZap2ItId(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<id>")) {
                    series.setId(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<Actors>")) {
                    series.setActors(parseList(XMLHelper.getCData(xmlReader), "|,"));
                } else if (tag.equalsIgnoreCase("<Airs_DayOfWeek>")) {
                    series.setAirsDayOfWeek(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<Airs_Time>")) {
                    series.setAirsTime(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<ContentRating>")) {
                    series.setContentRating(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<Genre>")) {
                    series.setGenres(parseList(XMLHelper.getCData(xmlReader), "|,"));
                } else if (tag.equalsIgnoreCase("<Network>")) {
                    series.setNetwork(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<Rating>")) {
                    series.setRating(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<Runtime>")) {
                    series.setRuntime(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<Status>")) {
                    series.setStatus(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<fanart>")) {
                    String s = XMLHelper.getCData(xmlReader);
                    if (!s.isEmpty()) {
                        series.setFanart(bannerMirror + s);
                    }
                } else if (tag.equalsIgnoreCase("<lastupdated>")) {
                    series.setLastUpdated(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<poster>")) {
                    String s = XMLHelper.getCData(xmlReader);
                    if (!s.isEmpty()) {
                        series.setPoster(bannerMirror + s);
                    }
                }
            } else if (event.isEndElement()) {
                if (event.toString().equalsIgnoreCase("</Series>")) {
                    break;
                }
            }
        }
        return series;
    }
    
    private Banner parseNextBanner(XMLEventReader xmlReader) throws XMLStreamException {
        Banner banner = null;
        
        while (xmlReader.hasNext()) {
            XMLEvent event = xmlReader.nextEvent();

            if (event.isStartElement()) {
                String tag = event.toString();
                if (tag.equalsIgnoreCase("<Banner>")) {
                    banner = new Banner();
                } else if (tag.equalsIgnoreCase("<BannerPath>")) {
                    String s = XMLHelper.getCData(xmlReader);
                    if (!s.isEmpty()) {
                        banner.setUrl(bannerMirror + s);
                    }
                } else if (tag.equalsIgnoreCase("<VignettePath>")) {
                    String s = XMLHelper.getCData(xmlReader);
                    if (!s.isEmpty()) {
                        banner.setVignette(bannerMirror + s);
                    }
                } else if (tag.equalsIgnoreCase("<ThumbnailPath>")) {
                    String s = XMLHelper.getCData(xmlReader);
                    if (!s.isEmpty()) {
                        banner.setThumb(bannerMirror + s);
                    }
                } else if (tag.equalsIgnoreCase("<id>")) {
                    String s = XMLHelper.getCData(xmlReader);
                    if (!s.isEmpty()) {
                        banner.setId(s);
                    }
                } else if (tag.equalsIgnoreCase("<BannerType>")) {
                    banner.setBannerType(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<BannerType2>")) {
                    banner.setBannerType2(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<Language>")) {
                    banner.setLanguage(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<Season>")) {
                    banner.setSeason(XMLHelper.getCDataInt(xmlReader));
                }
            } else if (event.isEndElement()) {
                if (event.toString().equalsIgnoreCase("</Banner>")) {
                    break;
                }
            }
        }
        return banner;
    }
    
    private Actor parseNextActor(XMLEventReader xmlReader) throws XMLStreamException {
        Actor actor = null;
        
        while (xmlReader.hasNext()) {
            XMLEvent event = xmlReader.nextEvent();

            if (event.isStartElement()) {
                String tag = event.toString();
                if (tag.equalsIgnoreCase("<Actor>")) {
                    actor = new Actor();
                } else if (tag.equalsIgnoreCase("<Image>")) {
                    String s = XMLHelper.getCData(xmlReader);
                    if (!s.isEmpty()) {
                        actor.setImage(bannerMirror + s);
                    }
                } else if (tag.equalsIgnoreCase("<Name>")) {
                    actor.setName(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<id>")) {
                    actor.setId(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<Role>")) {
                    actor.setRole(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("<SortOrder>")) {
                    actor.setSortOrder(XMLHelper.getCDataInt(xmlReader));
                }
            } else if (event.isEndElement()) {
                if (event.toString().equalsIgnoreCase("</Actor>")) {
                    break;
                }
            }
        }
        return actor;
    }
    
    private Episode parseNextEpisode(XMLEventReader xmlReader) throws XMLStreamException {
        Episode episode = null;
        boolean inEpisode = false;

        while (xmlReader.hasNext()) {
            XMLEvent event = xmlReader.nextEvent();
            if (event.isStartElement()) {
                String tag = event.toString();
                if (tag.equalsIgnoreCase("<Episode>")) {
	            inEpisode = true;
                    episode = new Episode();
		}
		if ( inEpisode ) {
			if (tag.equalsIgnoreCase("<id>")) {
			    episode.setId(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<Combined_episodenumber>")) {
			    episode.setCombinedEpisodeNumber(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<Combined_season>")) {
			    episode.setCombinedSeason(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<DVD_chapter>")) {
			    episode.setDvdChapter(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<DVD_discid>")) {
			    episode.setDvdDiscId(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<DVD_episodenumber>")) {
			    episode.setDvdEpisodeNumber(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<DVD_season>")) {
			    episode.setDvdSeason(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<Director>")) {
			    episode.setDirectors(parseList(XMLHelper.getCData(xmlReader), "|,"));
			} else if (tag.equalsIgnoreCase("<EpImgFlag>")) {
			    episode.setEpImgFlag(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<EpisodeName>")) {
			    episode.setEpisodeName(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<EpisodeNumber>")) {
			    episode.setEpisodeNumber(XMLHelper.getCDataInt(xmlReader));
			} else if (tag.equalsIgnoreCase("<FirstAired>")) {
			    episode.setFirstAired(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<GuestStars>")) {
			    episode.setGuestStars(parseList(XMLHelper.getCData(xmlReader), "|,"));
			} else if (tag.equalsIgnoreCase("<IMDB_ID>")) {
			    episode.setImdbId(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<Language>")) {
			    episode.setLanguage(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<Overview>")) {
			    episode.setOverview(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<ProductionCode>")) {
			    episode.setProductionCode(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<Rating>")) {
			    episode.setRating(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<SeasonNumber>")) {
			    episode.setSeasonNumber(XMLHelper.getCDataInt(xmlReader));
			} else if (tag.equalsIgnoreCase("<Writer>")) {
			    episode.setWriters(parseList(XMLHelper.getCData(xmlReader), "|,"));
			} else if (tag.equalsIgnoreCase("<absolute_number>")) {
			    episode.setAbsoluteNumber(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<filename>")) {
			    String s = XMLHelper.getCData(xmlReader);
			    if (!s.isEmpty()) {
				episode.setFilename(bannerMirror + s);
			    }
			} else if (tag.equalsIgnoreCase("<lastupdated>")) {
			    episode.setLastUpdated(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<seasonid>")) {
			    episode.setSeasonId(XMLHelper.getCData(xmlReader));
			} else if (tag.equalsIgnoreCase("<seriesid>")) {
			    episode.setSeriesId(XMLHelper.getCData(xmlReader));
			}
		}
            } else if (event.isEndElement()) {
                if (event.toString().equalsIgnoreCase("</Episode>")) {
                    break;
                }
            }
        }
        return episode;
    }
    
    private List<String> parseList(String input, String delim) {
        List<String> result = new ArrayList<String>();

        StringTokenizer st = new StringTokenizer(input, delim);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (token.length() > 0) {
                result.add(token);
            }
        }

        return result;
    }
public void GetSeriesXML(String ID,String FilePath) {

    File enfile = new File(FilePath + java.io.File.separator + "en.xml");
    if ( enfile.exists()) {
        ortus.api.DebugLog(LogLevel.Trace2, " XML Exists: " + FilePath);
        return;
    }

    String language = "en";
    String dest = FilePath + java.io.File.separator;
    urldownload.fileUrl(zipMirror + "694E79769B377E8D/" + "series/" + ID + "/all/" + (language!=null?language+".zip":""),"temp.zip",dest);
    ortus.api.DebugLog( LogLevel.Trace2 ,"downloadin series zip:" + xmlMirror);
    getZipFiles(dest+"temp.zip",dest);
    File Zip = new File(dest + "temp.zip");
    Zip.delete();
}

private void getZipFiles(String filename,String destinationname)
    {
        try
        {

            byte[] buf = new byte[1024];
            ZipInputStream zipinputstream = null;
            ZipEntry zipentry;
            zipinputstream = new ZipInputStream(
                new FileInputStream(filename));

            zipentry = zipinputstream.getNextEntry();
            while (zipentry != null)
            {
                String entryName = zipentry.getName();
                File existingfile = new File(destinationname+entryName);
                if(existingfile.exists()){
                zipinputstream.closeEntry();
                zipentry = zipinputstream.getNextEntry();
                }

                else if(!existingfile.exists()){
                //for each entry to be extracted

                ortus.api.DebugLog(LogLevel.Trace2,"getZipFiles: unzipping file "+entryName);
                int n;
                FileOutputStream fileoutputstream;
                File newFile = new File(entryName);

                String directory = newFile.getParent();

                if(directory == null)
                {
                    if(newFile.isDirectory())
                        break;
                }

                fileoutputstream = new FileOutputStream(
                   destinationname+entryName);

                while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
                    fileoutputstream.write(buf, 0, n);

                fileoutputstream.close();
                zipinputstream.closeEntry();
                zipentry = zipinputstream.getNextEntry();

                }}//while

            zipinputstream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	public void GetSeriesFanart(MediaObject mo, String tvdbpath, String Title, boolean override) {
		Banners fanart = getBanners(tvdbpath + java.io.File.separator + Title);
		Series series = getSeries(tvdbpath + java.io.File.separator + Title);
                List<Episode> episodes = getEpisodes(tvdbpath + java.io.File.separator + Title);
		String CurrentFolder = ortus.api.GetFanartFolder() + java.io.File.separator + "TV" + java.io.File.separator + Title;

		File Folder = new File(CurrentFolder);
		if (!Folder.exists() || override == true) {
			Folder.mkdirs();
		}
		List<Banner> fanarts = fanart.getFanartList();
		List<Banner> posters = fanart.getPosterList();
		List<Banner> banners = fanart.getSeriesList();
		List<Banner> seasons = fanart.getSeasonList();
		if (!fanarts.isEmpty()) {
			File Backgrounds = new File(CurrentFolder + java.io.File.separator + "Backgrounds" + java.io.File.separator);
			Backgrounds.mkdirs();
		}
		int downlimit = java.lang.Integer.parseInt(ortus.api.GetSageProperty("ortus/fanart/download_limit", "4"));
                int downloadCount = 0;
		for (int i = 0; i < fanarts.size(); i++) {
			Banner currentbanner = fanarts.get(i);
                        if ( ! currentbanner.getLanguage().equalsIgnoreCase("en"))
                            continue;
			String url = currentbanner.getUrl();
			String dest = CurrentFolder + java.io.File.separator + "Backgrounds" + java.io.File.separator;
                        String FanartPath = "TV" + java.io.File.separator + Title + java.io.File.separator + "Backgrounds" + java.io.File.separator;
			String filename = currentbanner.getUrl().substring(currentbanner.getUrl().lastIndexOf("/")+1);

                        if ( downloadCount < downlimit ) {
                            downloadCount++;
                            urldownload.fileUrl(url, filename, dest);
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"high","Backgrounds", url, FanartPath + filename);
                 //           ortus.image.util.scale(dest+filename,780,439,dest+"med-"+filename);
                            ortus.image.util.generate(dest+filename,780,dest+"med-"+filename);
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"medium","Backgrounds",FanartPath+"med-"+ filename,FanartPath+"med-"+ filename);
                            // ortus.image.util.scale(dest+filename,300,169,dest+"thmb-"+filename);
                            ortus.image.util.generate(dest+filename,300,dest+"thmb-"+filename);
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"low","Backgrounds",FanartPath+"thmb-"+ filename,FanartPath+"thmb-"+ filename);
                        }  else {
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"high","Backgrounds", url, null);//
                        }
                }
		if (!posters.isEmpty()) {
			File Posters = new File(CurrentFolder + java.io.File.separator + "Posters" + java.io.File.separator);
			Posters.mkdirs();
		}
                downloadCount=0;
		for (int i = 0; i < posters.size() ; i++) {
			Banner currentbanner = posters.get(i);
                        if ( ! currentbanner.getLanguage().equalsIgnoreCase("en"))
                            continue;
			String url = currentbanner.getUrl();
			String dest = CurrentFolder + java.io.File.separator + "Posters" + java.io.File.separator;
                        String FanartPath = "TV" + java.io.File.separator + Title + java.io.File.separator + "Posters" + java.io.File.separator;
			String filename = currentbanner.getUrl().substring(currentbanner.getUrl().lastIndexOf("/")+1);

                        if ( downloadCount < downlimit) {
                            downloadCount++;
                            urldownload.fileUrl(url, filename, dest);
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"high","Posters", url, FanartPath + filename);
                         //   ortus.image.util.scale(dest+filename,480,706,dest+"med-"+filename);
                            ortus.image.util.generate(dest+filename,706,dest+"med-"+filename);
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"medium","Posters",FanartPath+"med-"+ filename,FanartPath+"med-"+ filename);
                         //   ortus.image.util.scale(dest+filename,280,412,dest+"thmb-"+filename);
                            ortus.image.util.generate(dest+filename,412,dest+"thmb-"+filename);
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"low","Posters",FanartPath+"thmb-"+ filename,FanartPath+"thmb-"+ filename);
                        } else {
                           database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"high","Posters", url, null);//
                        }
                }
		if (!banners.isEmpty()) {
			File Banners = new File(CurrentFolder + java.io.File.separator + "Banners" + java.io.File.separator);
			Banners.mkdirs();
		}
                downloadCount=0;
		for (int i = 0; i < banners.size(); i++) {
			Banner currentbanner = banners.get(i);
                        if ( ! currentbanner.getLanguage().equalsIgnoreCase("en"))
                            continue;
			String url = currentbanner.getUrl();
			String dest = CurrentFolder + java.io.File.separator + "Banners" + java.io.File.separator;
                        String FanartPath = "TV" + java.io.File.separator + Title + java.io.File.separator + "Banners" + java.io.File.separator;
			String filename = currentbanner.getUrl().substring(currentbanner.getUrl().lastIndexOf("/")+1);

                        if ( downloadCount < downlimit) {
                            downloadCount++;
                            urldownload.fileUrl(url, filename, dest);
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"high", "Banners", url, FanartPath + filename);
                            // ortus.image.util.scale(dest+filename,455,84,dest+"med-"+filename);
                            ortus.image.util.generate(dest+filename,500,dest+"med-"+filename);
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"medium","Banners",FanartPath+"med-"+ filename,FanartPath+"med-"+ filename);
                            // ortus.image.util.scale(dest+filename,190,35,dest+"thmb-"+filename);
                            ortus.image.util.generate(dest+filename,300,dest+"thmb-"+filename);
                            database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"low","Banners",FanartPath+"thmb-"+ filename,FanartPath+"thmb-"+ filename);
                        } else {
                           database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"high","Banners", url, null);//
                        }
		}
		for (int i = 0; i < seasons.size(); i++) {
			Banner currentbanner = seasons.get(i);
                        if ( ! currentbanner.getLanguage().equalsIgnoreCase("en"))
                            continue;                    
			String url = currentbanner.getUrl();
                        String dest = "";
                        String bannertype="Posters";
//                        ortus.api.DebugLogTrace("Season Fanart: Type2: " + currentbanner.getBannerType2() + " URL: " + currentbanner.getUrl());
                        if ( currentbanner.getBannerType2().trim().equalsIgnoreCase("seasonwide")) {
                            File ca = new File(CurrentFolder + java.io.File.separator + "Season-" + currentbanner.getSeason() + java.io.File.separator + "Banners");
                            if ( ! ca.exists())
                                    ca.mkdirs();
                            dest = CurrentFolder + java.io.File.separator + "Season-" + currentbanner.getSeason() + java.io.File.separator + "Banners" + java.io.File.separator;
                            bannertype="Banners";
                        } else {
                            File ca = new File(CurrentFolder + java.io.File.separator + "Season-" + currentbanner.getSeason() + java.io.File.separator + "Posters");
                            if ( ! ca.exists())
                                    ca.mkdirs();
                            dest = CurrentFolder + java.io.File.separator + "Season-" + currentbanner.getSeason() + java.io.File.separator + "Posters" + java.io.File.separator;
                            bannertype="Posters";
                        }
//                        ortus.api.DebugLogTrace("Season Fanart: downloading to : " + dest);
			String FanartPath = "TV" + java.io.File.separator + Title + java.io.File.separator + "Season-" + currentbanner.getSeason() + java.io.File.separator + bannertype + java.io.File.separator;
			String filename = currentbanner.getUrl().substring(currentbanner.getUrl().lastIndexOf("/")+1);
			if ( urldownload.fileUrl(url, filename, dest).equals("OK"))
                            database.WriteTVFanart(Integer.parseInt(series.getId()), currentbanner.getId(),"high", "Season-" + currentbanner.getSeason() + "-" + bannertype, url, FanartPath + filename);

                        // ortus.image.util.scale(dest+filename,185,254,dest+"med-"+filename);
                        ortus.image.util.generate(dest+filename,706,dest+"med-"+filename);
                        database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"medium","Season-" + currentbanner.getSeason() + "-" + bannertype,FanartPath+"med-"+ filename,FanartPath+"med-"+ filename);
                        // ortus.image.util.scale(dest+filename,92,126,dest+"thmb-"+filename);
                        ortus.image.util.generate(dest+filename,412,dest+"thmb-"+filename);
                        database.WriteTVFanart(Integer.parseInt(series.getId()),currentbanner.getId(),"low","Season-" + currentbanner.getSeason() + "-" + bannertype,FanartPath+"thmb-"+ filename,FanartPath+"thmb-"+ filename);
                }
                for ( Episode ep : episodes) {
			String url = ep.getFilename();
                        if ( url == null)
                            continue;
			String dest = CurrentFolder + java.io.File.separator + "Episode" + java.io.File.separator;
                        String FanartPath = "TV" + java.io.File.separator + Title + java.io.File.separator + "Episode" + java.io.File.separator;
			String filename = url.substring(url.lastIndexOf("/")+1);
			if ( urldownload.fileUrl(url, filename, dest).equals("OK"))
                            database.WriteTVFanart(Integer.parseInt(series.getId()), ep.getId(), "high", "Episode-" + ep.getId() + "-Posters", url, FanartPath + filename);

                        // ortus.image.util.scale(dest+filename,192,144,dest+"med-"+filename);
                        ortus.image.util.generate(dest+filename,280,dest+"med-"+filename);
                        database.WriteTVFanart(Integer.parseInt(series.getId()),ep.getId(),"medium","Episode-" + ep.getId() + "-Posters",FanartPath+"med-"+ filename,FanartPath+"med-"+ filename);
                        // ortus.image.util.scale(dest+filename,80,60,dest+"thmb-"+filename);
                        ortus.image.util.generate(dest+filename,125,dest+"thmb-"+filename);
                        database.WriteTVFanart(Integer.parseInt(series.getId()),ep.getId(),"low","Episode-" + ep.getId() + "-Posters",FanartPath+"thmb-"+ filename,FanartPath+"thmb-"+ filename);
                }
	}
	
    private static HashMap<Integer, List> GroupSeasonBanners(Banners banner) {
        List<Banner> fanarts = banner.getSeasonList();
//        api.ortus.api.DebugLog(5, "sizeofbannersbeforeparsing"+fanarts.size());
        int sizebanners= 0;
        HashMap<Integer,List> BannersbySeason = new HashMap<Integer,List>();
        for(int i =0;i<fanarts.size();i++){
        List<Banner> bannerslist = new ArrayList<Banner>();
        Banner currentbanner = fanarts.get(i);
        int currseason =currentbanner.getSeason();
//        api.ortus.api.DebugLog(5, "currentseason =" + currseason);
        bannerslist.add(currentbanner);
            for(int j=i+1;j<fanarts.size();j++){

             Banner currentbanner2=fanarts.get(j);
             int currseason2=currentbanner2.getSeason();
             if(currseason2==currseason){
             bannerslist.add(currentbanner2);
//             ortus.api.ortus.api.DebugLog(5, "matchfound" + j);
                          }

            }
        if(!BannersbySeason.containsKey(currseason)){
        BannersbySeason.put(currseason,bannerslist);
        sizebanners = bannerslist.size()+sizebanners;}
        }
//        ortus.api.ortus.api.DebugLog(5, "size of banners after="+sizebanners);
        return BannersbySeason;
    }

}
