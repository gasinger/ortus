/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ortus.onlinescrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.StringEscapeUtils;
import ortus.onlinescrapper.themoviedb.CastItem;
import ortus.onlinescrapper.themoviedb.ImageItem;
import ortus.onlinescrapper.themoviedb.Movie;
import ortus.vars.LogLevel;
import ortus.vars.MediaGroup;
import ortus.vars.MediaType;
import ortus.property.SortedProperties;
import sagex.api.AiringAPI;
import sagex.api.AlbumAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

/**
 *
 * @author jphipps
 */
public class MediaObject {

	private Object mediafile;
	private boolean media_exist = false;
	private int metadata = 0;
	private int fanart = 0;
	private int scantype = 0;
	private boolean likely_tv = false;
	private MediaType mediatype = MediaType.None;
	private MediaGroup mediagroup = MediaGroup.Imported;

	/*
	 * Muisc Metadata
	 */
	private String artistname = "";
	private String albumname = "";
	private String songtitle = "";
	private String albumyear ="";
	private String genre="";
	private int trackno = 0;

	/*
	 * Video Metadata
	 */
	private String showtitle = "";
	private String episodetitle = "";
	private String alternatename = "";
	private String seasonno = "";
	private String episodeno = "";
	private String type = "";
	private String overview = "";
	private float rating = 5;
	private String rated = "";
	private String releasedate = "1970-01-01";
	private String runtime = "";
	private String trailer = "";
	private String metadatasource = "";
	private boolean metadatafound = false;
	private String url = "";
	private String path = "";
	private String imdbid = null;
	private String tmdbid = null;
	private String tvdbid = null;
	private List<String> genres = new ArrayList<String>();
	private List<ImageItem> images = new ArrayList<ImageItem>();
	private List<CastItem> cast = new ArrayList<CastItem>();
	private Properties mp = new Properties();
	
	public MediaObject() {
	}

	public MediaObject(Object mo) {
//		ortus.api.DebugLog(LogLevel.Trace, "VideoObject: create");
		mediafile = mo;
		showtitle = MediaFileAPI.GetMediaTitle(mo);
		metadatasource = "Sage";
		if (MediaFileAPI.IsTVFile(mo)) {
			mediatype = MediaType.Recording;
			mediagroup = MediaGroup.Recorded;
			episodetitle = ShowAPI.GetShowEpisode(mo);
		} else if ( MediaFileAPI.IsPictureFile(mo)) {
			mediatype = MediaType.Picture;
		} else if ( MediaFileAPI.IsMusicFile(mo)) {
			mediatype = MediaType.Music;
			Object album = MediaFileAPI.GetAlbumForFile(mo);
			artistname = AlbumAPI.GetAlbumArtist(album);
			albumname = AlbumAPI.GetAlbumName(album);
			genres.add(AlbumAPI.GetAlbumGenre(album));
			trackno = AiringAPI.GetTrackNumber(mo);
			songtitle = MediaFileAPI.GetMediaTitle(mo);
			albumyear = AlbumAPI.GetAlbumYear(album);
		}
		overview = ShowAPI.GetShowDescription(mo);
		rated = ShowAPI.GetShowRated(mo);
	}

	/**
	 * @return the mo
	 */
	public Object getMedia() {
		return mediafile;
	}

	/**
	 * @param mo the mo to set
	 */
	public void setMedia(Object mo) {
		this.mediafile = mo;
	}

	/**
	 * @return the media_exist
	 */
	public boolean isMedia_exist() {
		return media_exist;
	}

	/**
	 * @param media_exist the media_exist to set
	 */
	public void setMedia_exist(boolean media_exist) {
		this.media_exist = media_exist;
	}

	/**
	 * @return the metadata
	 */
	public int getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(int metadata) {
		this.metadata = metadata;
	}

	/**
	 * @return the fanart
	 */
	public int getFanart() {
		return fanart;
	}

	/**
	 * @param fanart the fanart to set
	 */
	public void setFanart(int fanart) {
		this.fanart = fanart;
	}

	/**
	 * @return the scantype
	 */
	public int getScantype() {
		return scantype;
	}

	/**
	 * @param scantype the scantype to set
	 */
	public void setScantype(int scantype) {
		this.scantype = scantype;
	}

	/**
	 * @return the likely_tv
	 */
	public boolean isLikely_tv() {
		return likely_tv;
	}

	/**
	 * @param likely_tv the likely_tv to set
	 */
	public void setLikely_tv(boolean likely_tv) {
		this.likely_tv = likely_tv;
	}

	/**
	 * @return the mediatype
	 */
	public MediaType getMediatype() {
		return mediatype;
	}

	public boolean isVideo() {
	    if ( mediatype == MediaType.None ||
		 mediatype == MediaType.Recording ||
		 mediatype == MediaType.Movie ||
		 mediatype == MediaType.Series ||
		 mediatype == MediaType.Home)
		    return true;
	    else
		    return false;

	}

	public boolean isMusic() {
	    if ( mediatype == MediaType.Music)
		return true;
	    else
		    return false;
	}

	public boolean isPicture() {
	    if ( mediatype == MediaType.Picture)
		return true;
	    else
		return false;
	}

	public int getMediaTypeInt() {
		if ( mediatype == MediaType.None)
			return 0;
		if ( mediatype == MediaType.Recording)
			return 1;
		if ( mediatype == MediaType.Movie)
			return 2;
		if ( mediatype == MediaType.Series)
			return 3;
		if ( mediatype == MediaType.Home)
			return 4;
		if ( mediatype == MediaType.Picture)
			return 5;
		if ( mediatype == MediaType.Music)
			return 6;
		return 0;
	}
	/**
	 * @param mediatype the mediatype to set
	 */
	public void setMediatype(MediaType mediatype) {
		this.mediatype = mediatype;
	}

	public boolean isMediaTypeSeries() {
		if (mediatype == MediaType.Series) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isMediaTypeMovie() {
		if (mediatype == MediaType.Movie) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isMediaTypeHome() {
		if (mediatype == MediaType.Home) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isMediaTypeRecording() {
		if (mediatype == MediaType.Recording) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the mediagroup
	 */
	public MediaGroup getMediagroup() {
		return mediagroup;
	}

	public int getMediaGroupInt() {
		if ( mediagroup == MediaGroup.Recorded)
			return 0;
		if ( mediagroup == MediaGroup.Imported)
			return 1;
		return 1;
	}
	/**
	 * @param mediagroup the mediagroup to set
	 */
	public void setMediagroup(MediaGroup mediagroup) {
		this.mediagroup = mediagroup;
	}

	/**
	 * @return the showtitle
	 */
	public String getShowtitle() {
		return showtitle;
	}

	public String getShowtitleFiltered() {
		return showtitle.replaceAll(":", "");
	}

	/**
	 * @param showtitle the showtitle to set
	 */
	public void setShowtitle(String showtitle) {
		this.showtitle = showtitle;
	}

	/**
	 * @return the episodetitle
	 */
	public String getEpisodetitle() {
		return episodetitle;
	}

	/**
	 * @param episodetitle the episodetitle to set
	 */
	public void setEpisodetitle(String episodetitle) {
		this.episodetitle = episodetitle;
	}

	/**
	 * @return the alternatename
	 */
	public String getAlternatename() {
		return alternatename;
	}

	/**
	 * @param alternatename the alternatename to set
	 */
	public void setAlternatename(String alternatename) {
		this.alternatename = alternatename;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the overview
	 */
	public String getOverview() {
		return overview;
	}

	/**
	 * @param overview the overview to set
	 */
	public void setOverview(String overview) {
		this.overview = overview;
	}

	/**
	 * @return the rating
	 */
	public float getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(float rating) {
		this.rating = rating;
	}

	/**
	 * @return the rated
	 */
	public String getRated() {
		return rated;
	}

	/**
	 * @param rated the rated to set
	 */
	public void setRated(String rated) {
		this.rated = rated;
	}

	/**
	 * @return the releasedate
	 */
	public String getReleasedate() {
		return releasedate;
	}

	/**
	 * @param releasedate the releasedate to set
	 */
	public void setReleasedate(String releasedate) {
		this.releasedate = releasedate;
	}

	/**
	 * @return the runtime
	 */
	public String getRuntime() {
		return runtime;
	}

	/**
	 * @param runtime the runtime to set
	 */
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	/**
	 * @return the trailer
	 */
	public String getTrailer() {
		return trailer;
	}

	/**
	 * @param trailer the trailer to set
	 */
	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

	/**
	 * @return the metadatasource
	 */
	public String getMetadatasource() {
		return metadatasource;
	}

	/**
	 * @param metadatasource the metadatasource to set
	 */
	public void setMetadatasource(String metadatasource) {
		this.metadatasource = metadatasource;
	}

	/**
	 * @return the metadatafound
	 */
	public boolean isMetadatafound() {
		return metadatafound;
	}

	/**
	 * @param metadatafound the metadatafound to set
	 */
	public void setMetadatafound(boolean metadatafound) {
		this.metadatafound = metadatafound;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the imdbid
	 */
	public String getImdbid() {
		return imdbid;
	}

	/**
	 * @param imdbid the imdbid to set
	 */
	public void setImdbid(String imdbid) {
		this.imdbid = imdbid;
	}

	/**
	 * @return the tmdbid
	 */
	public String getTmdbid() {
		return tmdbid;
	}

	/**
	 * @param tmdbid the tmdbid to set
	 */
	public void setTmdbid(String tmdbid) {
		this.tmdbid = tmdbid;
	}

	/**
	 * @return the tvdbid
	 */
	public String getTvdbid() {
		return tvdbid;
	}

	/**
	 * @param tvdbid the tvdbid to set
	 */
	public void setTvdbid(String tvdbid) {
		this.tvdbid = tvdbid;
	}

	/**
	 * @return the genres
	 */
	public List<String> getGenres() {
		return genres;
	}

	/**
	 * @param genres the genres to set
	 */
	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	/**
	 * @return the images
	 */
	public List<ImageItem> getImages() {
		return images;
	}

	/**
	 * @param images the images to set
	 */
	public void setImages(List<ImageItem> images) {
		this.images = images;
	}

	/**
	 * @return the cast
	 */
	public List<CastItem> getCast() {
		return cast;
	}

	/**
	 * @param cast the cast to set
	 */
	public void setCast(List<CastItem> cast) {
		this.cast = cast;
	}

	public boolean isSeasonno() {
		if (seasonno.isEmpty() && episodeno.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @return the seasonno
	 */
	public String getSeasonno() {
		return seasonno;
	}

	/**
	 * @param seasonno the seasonno to set
	 */
	public void setSeasonno(String seasonno) {
		this.seasonno = seasonno;
	}

	/**
	 * @return the episodeno
	 */
	public String getEpisodeno() {
		return episodeno;
	}

	/**
	 * @param episodeno the episodeno to set
	 */
	public void setEpisodeno(String episodeno) {
		this.episodeno = episodeno;
	}

	public void setMovie(Movie movie) {
            try {
                    this.showtitle = movie.getName();
                    this.alternatename = movie.getAlternateName();
                    this.overview = movie.getOverview();
                    this.rating = movie.getRating();
                    this.rated = movie.getCertification();
                    this.releasedate = movie.getReleasedate();
                    this.runtime = String.valueOf(movie.getRuntime());
                    this.trailer = movie.getTrailer();
                    this.url = movie.getUrl();
                    this.path = movie.getPath();
                    this.genres = movie.GetGenres();
                    this.images = movie.GetImages();
                    this.cast = movie.GetCast();
            } catch ( Exception e) {
                ortus.api.DebugLog(LogLevel.Fatal, "setMovie: Exception",e);
            }
	}

	public boolean ReadProperty() {
		try {
			File propfile = new File(MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath() + ".properties");
			if ( ! propfile.exists())
				propfile = new File(ortus.api.GetProperty("ortus/config") + java.io.File.separator + "metaprop" + java.io.File.separator + ortus.util.scrubString.ScrubFileName(showtitle) + ".properties");

			if (propfile.exists()) {
				ortus.api.DebugLog(LogLevel.Info, "readProperty: " + propfile.getAbsolutePath());
				mp.load(new FileInputStream(propfile));
				String[] workproviders = mp.getProperty("MediaProviderDataID").split(";");
				for (String prov : workproviders) {
					String[] subprov = prov.split(":");
					if (subprov[0].equalsIgnoreCase("themoviedb")) {
						tmdbid = subprov[1];
					}
					if (subprov[0].equalsIgnoreCase("theTVDB")) {
						tvdbid = subprov[1];
					}
					if (subprov[0].equalsIgnoreCase("tvdb")) {
						tvdbid = subprov[1];
					}
					if (subprov[0].equalsIgnoreCase("imdb")) {
						imdbid = subprov[1];
					}
				}
				
				showtitle = mp.getProperty("MediaTitle");
				overview = mp.getProperty("Description");
				runtime = mp.getProperty("RunningTime");
				rated = mp.getProperty("MPAA");
				releasedate = mp.getProperty("OriginalAirDate");

				if (tmdbid == null && imdbid == null && tvdbid == null) {
					return false;
				} else {
					ortus.api.DebugLog(LogLevel.Trace2, "TVDB: " + tvdbid + " TMDB: " + tmdbid + " IMDB: " + imdbid);
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			ortus.api.DebugLog(LogLevel.Error, "ReadProperty Exception: " + e);
			return false;
		}
	}

    public void WriteProperty() {
//	ortus.api.DebugLog(LogLevel.Trace2,"WriteProperty: Starting");
	String propComment = "Ortus: Updated Metadata Property file";

	if ( mp.isEmpty())
		propComment = "Ortus: New Metadata Property file";
//        SortedProperties mp = new SortedProperties(mp);

        mp.setProperty("MediaTitle", showtitle);
        mp.setProperty("Title", showtitle);
        mp.setProperty("Description", overview);
        mp.setProperty("RunningTime", runtime);
        if ( mediatype == MediaType.Recording )
            mp.setProperty("MediaType", "TV");
        else if ( mediatype == MediaType.Series)
            mp.setProperty("MediaType", "Series");
        else if ( mediatype == MediaType.Home)
            mp.setProperty("MediaType", "HomeMovie");
        else
            mp.setProperty("MediaType","Movie");

        mp.setProperty("MPAA",rated);
        mp.setProperty("UserRating",String.valueOf(Math.round(rating)));
        mp.setProperty("OriginalAirDate", releasedate);
        mp.setProperty("Trailer", trailer);

        StringBuffer xgenre = new StringBuffer(10);
        for ( String g : genres) {
            xgenre.append(g + " ");
        }
        mp.setProperty("Genre", xgenre.toString().trim());

        StringBuffer xactor = new StringBuffer(10);
        StringBuffer xdirector = new StringBuffer(10);
        StringBuffer xwriter = new StringBuffer(10);
        for ( CastItem ci : cast ) {
//            ortus.api.DebugLog(LogLevel.Trace2,"Cast: " + ci.GetName() + " doing: " + ci.GetJob());
            if ( ci.GetJob().equals("Actor"))
                xactor.append(ci.GetName() + " -- " + ci.GetCharacter() + ";\n");
            if ( ci.GetJob().equals("Director"))
                xdirector.append(ci.GetName() + ";");
            if ( ci.GetJob().equals("Writer"))
                xwriter.append(ci.GetName() + ";");
        }
	if ( ! xdirector.toString().trim().isEmpty())
		mp.setProperty("Director", xdirector.toString().trim());
	if ( ! xwriter.toString().trim().isEmpty())
		mp.setProperty("Writer", xwriter.toString().trim());
	if ( ! xactor.toString().trim().isEmpty())
		mp.setProperty("Actor", xactor.toString().trim());

//	ortus.api.DebugLog(LogLevel.Trace, "Finished cast, starting id");
	String metadataprovider = "";
        if ( tmdbid != null)
	    metadataprovider+="themoviedb:" + tmdbid;
	if ( imdbid != null) {
		if ( ! metadataprovider.isEmpty())
			metadataprovider+=";";
		metadataprovider+="imdb:" + imdbid;
	}
        if ( tvdbid != null) {
		if ( ! metadataprovider.isEmpty())
			metadataprovider+=";";
	    metadataprovider+="tvdb:" + tvdbid;
	}

        mp.setProperty("MediaProviderDataID", metadataprovider);

//	ortus.api.DebugLog(LogLevel.Trace,"build prop getting ready to write");
	File mpfile = null;
        try {
	    mpfile = new File(ortus.api.GetProperty("ortus/basepath") + java.io.File.separator + "metaprop" + java.io.File.separator + ortus.util.scrubString.ScrubFileName(showtitle) + ".properties");
//            mp.store(new FileOutputStream(new File(MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath() + ".properties")), "Generator: Ortus; MediaFile: file:" + MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath());
	    mp.store(new FileOutputStream(mpfile),propComment);
        } catch (IOException ex) {
            ortus.api.DebugLog(LogLevel.Error, "Movie: Failed to write property file: " + mpfile.getAbsolutePath() );
            return;
        }

        return;
    }


    public boolean WriteDB() {
	    if ( mediatype == MediaType.None ||
		 mediatype == MediaType.Recording ||
		 mediatype == MediaType.Movie ||
		 mediatype == MediaType.Series ||
		 mediatype == MediaType.Home ||
		 mediatype == MediaType.Picture)
		    return WriteVideoDB();
	    else if ( mediatype == MediaType.Music)
		    return WriteMusicDB();

	    return false;
    }

    public boolean WriteMusicDB() {
		albumname = StringEscapeUtils.escapeSql(albumname);
		artistname = StringEscapeUtils.escapeSql(artistname);
		songtitle = StringEscapeUtils.escapeSql(songtitle);
		int mfid = MediaFileAPI.GetMediaFileID(mediafile);

		String SQL = "UPDATE sage.media SET mediatitle='" + songtitle + "', episodetitle = '" + artistname + " - " + albumname + "', mediatype=" + getMediaTypeInt() + ",mediagroup=" + getMediaGroupInt() + ", mediapath='" + MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath().replaceAll("'", "''") + "', mediaencoding = '" + MediaFileAPI.GetMediaFileFormatDescription(mediafile).trim() + "', mediasize = " + MediaFileAPI.GetSize(mediafile) + ", mediaduration = " + MediaFileAPI.GetFileDuration(mediafile) + " , lastwatchedtime = "
			+ AiringAPI.GetLatestWatchedTime(mediafile) + ", airingstarttime = " + AiringAPI.GetAiringStartTime(mediafile) + " WHERE mediaid = " + String.valueOf(mfid);
		int success = ortus.api.executeSQL(SQL);
		if (success < 1) {
			SQL = "INSERT INTO sage.media (mediaid, mediatitle,episodetitle, mediapath, mediaencoding, mediatype, mediagroup, mediasize, mediaduration, lastwatchedtime, airingstarttime, mediaimporttime) "
				+ " VALUES(" + mfid + ", '" + songtitle + "','" + artistname + " - " + albumname + "','" + MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath().replaceAll("'", "''") + "','" + MediaFileAPI.GetMediaFileFormatDescription(mediafile).trim() + "'," + getMediaTypeInt() + "," + getMediaGroupInt() + ", " + MediaFileAPI.GetSize(mediafile)
				+ ", " + MediaFileAPI.GetFileDuration(mediafile) + ", " + AiringAPI.GetLatestWatchedTime(mediafile) + "," + AiringAPI.GetAiringStartTime(mediafile) + ", current_timestamp)";
			ortus.api.executeSQL(SQL);
		}

		SQL = "UPDATE sage.music SET artist='" + artistname + "', album = '" + albumname + "', trackno=" + trackno + ",title = '" + songtitle + "' WHERE mediaid = " + String.valueOf(mfid);
		success = ortus.api.executeSQL(SQL);
		if (success < 1) {
			SQL = "INSERT INTO sage.music (mediaid, artist, album, trackno, title)VALUES(" + mfid + ", '" + artistname + "','" + albumname + "'," + trackno + ",'" + songtitle + "')";
			ortus.api.executeSQL(SQL);
		}

		SQL = "delete from sage.genre where mediaid = " + String.valueOf(mfid);
		ortus.api.executeSQL(SQL);
		for (String g : genres) {
			g = StringEscapeUtils.escapeSql(g);
			SQL = "insert into sage.genre ( mediaid, name) values ( " + String.valueOf(mfid) + " , '" + g + "')";
			ortus.api.executeSQL(SQL);
		}
		
		return true;
    }
	public boolean WriteVideoDB() {
//	ortus.api.DebugLog(LogLevel.Trace,"WriteDB");
//        WriteProperty(mediafile);

		if (showtitle.isEmpty()) {
			showtitle = MediaFileAPI.GetMediaTitle(mediafile);
		}
		overview = StringEscapeUtils.escapeSql(overview);
		showtitle = StringEscapeUtils.escapeSql(showtitle);
		showtitle = showtitle.replaceAll("\"", "");
		episodetitle = StringEscapeUtils.escapeSql(episodetitle);
		episodetitle = episodetitle.replaceAll("\"", "");
		int mfid = MediaFileAPI.GetMediaFileID(mediafile);
                long mediaduration = MediaFileAPI.GetFileDuration(mediafile);

		String SQL = "UPDATE sage.media SET mediatitle='" + showtitle + "', episodetitle = '" + episodetitle + "', mediatype=" + getMediaTypeInt() + ",mediagroup=" + getMediaGroupInt() + ", mediapath='" + MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath().replaceAll("'", "''") + "', mediaencoding = '" + MediaFileAPI.GetMediaFileFormatDescription(mediafile).trim() + "', mediasize = " + MediaFileAPI.GetSize(mediafile) + ", mediaduration = " + mediaduration + ",lastwatchedtime = "
			+ AiringAPI.GetLatestWatchedTime(mediafile) + ", airingstarttime = " + AiringAPI.GetAiringStartTime(mediafile) + " WHERE mediaid = " + String.valueOf(mfid);
		int success = ortus.api.executeSQL(SQL);
		if (success < 1) {
			SQL = "INSERT INTO sage.media (mediaid, mediatitle,episodetitle, mediapath, mediaencoding, mediatype, mediagroup, mediasize, mediaduration, lastwatchedtime, airingstarttime, mediaimporttime) "
				+ " VALUES(" + mfid + ", '" + showtitle + "','" + episodetitle + "','" + MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath().replaceAll("'", "''") + "','" + MediaFileAPI.GetMediaFileFormatDescription(mediafile).trim() + "'," + getMediaTypeInt() + "," + getMediaGroupInt() + ", " + MediaFileAPI.GetSize(mediafile)
				+ ", " + mediaduration + ", " + AiringAPI.GetLatestWatchedTime(mediafile) + "," + AiringAPI.GetAiringStartTime(mediafile) + ", current_timestamp)";
			ortus.api.executeSQL(SQL);
		}

//		if ( isMediaTypeSeries() || isMediaTypeRecording() || isPicture() ) {
//		    return true;
//		}
//
//		String workYear = ShowAPI.GetShowYear(mediafile);
//		if (workYear.isEmpty() && !releasedate.isEmpty()) {
//			workYear = releasedate.substring(0, 4);
//		}
//
//		if (releasedate.isEmpty()) {
//			releasedate = "1970-01-01";
//		}
//
//		SQL = "UPDATE sage.mediavideos SET description ='" + overview + "', releasedate='" + releasedate + "',userrating=" + rating + ", mpaarated = '" + rated + "', "
//			+ " mediayear = '" + ShowAPI.GetShowYear(mediafile) + "', tmdbid = " + String.valueOf(tmdbid) + ", imdbid = '" + String.valueOf(imdbid) + "', metadatasource = '" + metadatasource + "',trailer = '" + trailer + "' "
//			+ " WHERE mediaid = " + String.valueOf(mfid);
//		success = ortus.api.executeSQL(SQL);
//		if (success < 1) {
//			SQL = "INSERT INTO sage.mediavideos (mediaid, mediayear, description, releasedate, userrating, mpaarated, tmdbid, imdbid, metadatasource, trailer) "
//				+ " VALUES(" + mfid + ", '" + workYear + "','" + overview + "','" + releasedate + "'," + rating + ",'" + rated + "'," + String.valueOf(tmdbid) + ",'" + imdbid + "','" + metadatasource + "','" + trailer + "')";
//			ortus.api.executeSQL(SQL);
//		}
//
//		SQL = "delete from sage.fanart where mediaid = " + String.valueOf(mfid);
//		ortus.api.executeSQL(SQL);
//
//		for (ImageItem ii : images) {
//			ii.WriteImageDB();
//		}
//
//		SQL = "delete from sage.genre where mediaid = " + String.valueOf(mfid);
//		ortus.api.executeSQL(SQL);
//		for (String g : genres) {
//			SQL = "insert into sage.genre ( mediaid, name) values ( " + String.valueOf(mfid) + " , '" + g + "')";
//			ortus.api.executeSQL(SQL);
//		}
//		SQL = "delete from sage.cast where mediaid = " + String.valueOf(mfid);
//		ortus.api.executeSQL(SQL);
//		for (CastItem ci : cast) {
//			ci.WriteToDB(mediafile);
//			SQL = "insert into sage.cast ( mediaid, personid, name, job, character) values ( " + String.valueOf(mfid) + ", " + tmdbid + ",'" + ci.GetName().replaceAll("'", "''") + "','" + ci.GetJob().replaceAll("'", "''") + "','" + ci.GetCharacter().replaceAll("'", "''") + "')";
//			ortus.api.executeSQL(SQL);
//		}
		return true;
	}

	public void DownloadImages(String destination) {
		destination = destination.trim();

		int fanart_limit = Integer.parseInt(ortus.api.GetSageProperty("ortus/fanart/download_limit", "4"))*4;

		int bgfa = 0;
		int pstfa = 0;

		ortus.api.DebugLog(LogLevel.Trace2, "DownloadImages to: " + destination + " Limit: " + fanart_limit);
		for (ImageItem ii : images) {
			if (ii.IsBackgrounds()) {
                                bgfa++;
                            if (bgfa > fanart_limit)
                                    continue;
                        }
			if (ii.IsPoster()) {
                                pstfa++;
                            if (pstfa > fanart_limit)
                                    continue;
                        }
			ii.getImage(destination);
		}
	}

	public void DownloadCastImages(String destination) {
		for (CastItem ci : cast) {
			ci.DownloadImages(mediafile, destination);
		}
	}

	public void Dump() {
		ortus.api.DebugLog(LogLevel.Trace2, "Dump: Running");
		ortus.api.DebugLog(LogLevel.Trace2, "Title: " + showtitle);
		ortus.api.DebugLog(LogLevel.Trace2, "IMDBID: " + imdbid);
		ortus.api.DebugLog(LogLevel.Trace2, "TMDBID: " + tmdbid);
		for (ImageItem ii : images) {
			ortus.api.DebugLog(LogLevel.Trace2, "Fanart: " + ii.GetUrl());
		}
		for (String g : genres) {
			ortus.api.DebugLog(LogLevel.Trace2, "Genre: " + g);
		}
		for (CastItem ci : cast) {
			ortus.api.DebugLog(LogLevel.Trace2, "Cast: " + ci.GetName() + " as " + ci.GetJob());
		}
	}
}
