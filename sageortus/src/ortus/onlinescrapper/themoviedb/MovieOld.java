/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import ortus.onlinescrapper.MediaObject;
import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

/**
 *
 * @author jphipps
 */
public class MovieOld extends ortus.vars implements Serializable {
    private String name="";
    private String episodetitle="";
    private String alternatename = "";
    private String type = "";
    private String id = "0";
    private String overview = "";
    private float rating = 5;
    private String rated = "";
    private String releasedate="1970-01-01";
    private String runtime="";
    private String trailer="";
    private String imdbid="";
    private String metadatasource="";
    private boolean metadatafound = false;
    private String url="";
    private String path="";
    private int mediatype = 0;
    private int mediagroup = 1;
    private List<String> genres = new ArrayList<String>();
    private List<ImageItem> images = new ArrayList<ImageItem>();
    private List<CastItem> cast = new ArrayList<CastItem>();


   public MovieOld() {
   }
   
   public MovieOld(MediaObject mo) {
	ortus.api.DebugLog(LogLevel.Trace,"Movie: create");
	name = mo.getShowtitle();
	episodetitle = mo.getEpisodetitle();
	mediatype = mo.getMediaTypeInt();
	mediagroup = mo.getMediaGroupInt();
	overview = ShowAPI.GetShowDescription(mo.getMedia());
	rated = ShowAPI.GetShowRated(mo.getMedia());
	metadatasource="Sage";
   }

   public String GetName() {
        return name;
    }
    public void SetName(String name) {
        this.name = name;
    }

    public void SetEpisodeTitle(String episodetitle) {
	    this.episodetitle = episodetitle;
    }
    
    public String GetAlternateName() {
        return alternatename;
    }
    public void SetAlternateName(String alternatename) {
        this.alternatename = alternatename;
    }

    public String GetPath() {
        return path;
    }
    public void SetPath(String path) {
        this.path = path;
    }
    public String GetType() {
        return type;
    }
    public void SetType(String type) {
        this.type = type;
    }

    public String GetId() {
        return id;
    }
    public void SetId(String id) {
        this.id = id;
    }

    public String GetOverview() {
        return overview;
    }
    public void SetOverview(String overview) {
        this.overview = overview;
    }

    public float GetRating() {
        return rating;
    }
    public void SetRating(float rating) {
        this.rating = rating;
    }

    public String GetRated() {
        return rated;
    }
    public void SetRated(String rated) {
        this.rated = rated;
    }

    public String GetReleaseDate() {
        return releasedate;
    }
    public void SetReleaseDate(String releasedate) {
        this.releasedate = releasedate;
    }

    public String GetRunTime() {
        return runtime;
    }
    public void SetRunTime( String runtime) {
        this.runtime = runtime;
    }

    public String GetMetadataSource() {
        return metadatasource;
    }
    public void SetMetadataSource(String metadatasource) {
        this.metadatasource = metadatasource;
    }

    public String GetIMDBId() {
        return imdbid;
    }
    public void SetIMDBId(String imdbid) {
        this.imdbid = imdbid;
    }

    public int GetMediaType() {
        return mediatype;
    }
    public void SetMediaType(int mediatype) {
        this.mediatype = mediatype;
    }

    public int GetMediaGroup() {
        return mediagroup;
    }
    public void SetMediaGroup(int mediagroup) {
        this.mediagroup = mediagroup;
    }

    public boolean IsMetadataFound() {
        return metadatafound;
    }

    public void SetMetadataFound(boolean metadatafound) {
        this.metadatafound = metadatafound;
    }

    public String GetTrailer() {
        return trailer;
    }
    public void SetTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String GetUrl() {
	    return url;
    }
    public void SetUrl(String url) {
	    this.url = url;
    }
    public List<String> GetGenres() {
        return genres;
    }
    public void AddGenre(String genreentry) {
        genres.add(genreentry);
    }

    public List<ImageItem> GetImages() {
        return images;
    }
    public void AddImage(String type, String size, String url ) {
        images.add(new ImageItem("",type, size,url));
    }

    public List<CastItem> GetCast() {
        return cast;
    }
    public void AddCast(String id, String name, String job, String character) {
        cast.add(new CastItem(id, name,job,character));
    }

    public void DownloadImages(Object mediafile, String destination) {
        destination = destination.trim();

        int fanart_limit = Integer.parseInt(ortus.api.GetSageProperty("ortus/fanart/download_limit","4"));

        int bgfa = 0;
        int bgfaposter = 0;
        int bgfathumb = 0;

        int pstfa = 0;
        int pstcover = 0;
        int pstmid = 0;
        int pstthumb = 0;

        ortus.api.DebugLog(LogLevel.Trace2, "DownloadImages to: " + destination + " Limit: " + fanart_limit);
        for( ImageItem ii : images ) {
            if ( ii.IsBackgrounds()) {
                if( ii.GetUrl().endsWith("_poster.jpg")) {
                    bgfaposter++;
                    if ( bgfaposter > fanart_limit)
                        continue;
                } else if ( ii.GetUrl().endsWith("_thumb.jpg")) {
                    bgfathumb++;
                    if ( bgfathumb > fanart_limit)
                        continue;
                } else if ( ii.GetUrl().endsWith(".jpg")) {
                    bgfa++;
                    if ( bgfa > fanart_limit)
                        continue;
                }
            }
            if ( ii.IsPoster()) {
                if( ii.GetUrl().endsWith("_conver.jpg")) {
                    pstcover++;
                    if ( pstcover > fanart_limit)
                        continue;
                } else if( ii.GetUrl().endsWith("_mid.jpg")) {
                    pstmid++;
                    if ( pstmid > fanart_limit)
                        continue;
                } else if ( ii.GetUrl().endsWith("_thumb.jpg")) {
                    pstthumb++;
                    if ( pstthumb > fanart_limit)
                        continue;
                } else if ( ii.GetUrl().endsWith(".jpg")) {
                    pstfa++;
                    if ( pstfa > fanart_limit)
                        continue;
                }
            }

            ii.getImage(destination);
        }
    }
    
    public void DownloadCastImages(Object mediafile, String destination) {
        for( CastItem ci : cast) {
		ci.DownloadImages(mediafile, destination);
        }
    }

    public void Dump() {
        ortus.api.DebugLog(LogLevel.Trace2,"Dump: Running");
        ortus.api.DebugLog(LogLevel.Trace2,"Title: " + name);
        ortus.api.DebugLog(LogLevel.Trace2,"IMDBID: " + imdbid);
        ortus.api.DebugLog(LogLevel.Trace2,"TMDBID: " + id);
	for ( ImageItem ii : images) {
	    ortus.api.DebugLog(LogLevel.Trace2,"Fanart: " + ii.GetUrl());
	}
        for ( String g : genres ) {
            ortus.api.DebugLog(LogLevel.Trace2,"Genre: " + g);
        }
        for ( CastItem ci : cast) {
            ortus.api.DebugLog(LogLevel.Trace2,"Cast: " + ci.GetName() + " as " + ci.GetJob());
        }
    }
    public boolean WriteDB(Object mediafile) {
//	ortus.api.DebugLog(LogLevel.Trace,"WriteDB");
//        WriteProperty(mediafile);

        if ( name.isEmpty())
            name = MediaFileAPI.GetMediaTitle(mediafile);
        overview = overview.replaceAll("'","''");
        name = name.replaceAll("'","''");
	name = name.replaceAll("\"","");
	episodetitle = episodetitle.replaceAll("'","''");
	episodetitle = episodetitle.replaceAll("\"","");
        int mfid = MediaFileAPI.GetMediaFileID(mediafile);

        String SQL ="UPDATE sage.media SET mediatitle='"+ name +"', episodetitle = '" + episodetitle + "', mediatype=" + mediatype + ",mediagroup=" + mediagroup + ", mediapath='" + MediaFileAPI.GetFileForSegment(mediafile,0).getAbsolutePath().replaceAll("'","''") + "', mediaencoding = '" + MediaFileAPI.GetMediaFileFormatDescription(mediafile).trim() + "', mediasize = " + MediaFileAPI.GetSize(mediafile) + ", mediaduration = " + MediaFileAPI.GetFileDuration(mediafile) + " , lastwatchedtime = " +
                AiringAPI.GetLatestWatchedTime(mediafile) + ", airingstarttime = " + AiringAPI.GetAiringStartTime(mediafile) + " WHERE mediaid = " + String.valueOf(mfid);
        int success = ortus.api.executeSQL(SQL);
        if(success < 1){
            SQL =  "INSERT INTO sage.media (mediaid, mediatitle,episodetitle, mediapath, mediaencoding, mediatype, mediagroup, mediasize, mediaduration, lastwatchedtime, airingstarttime, mediaimporttime) " +
                       " VALUES(" + mfid + ", '" + name + "','" + episodetitle + "','" + MediaFileAPI.GetFileForSegment(mediafile,0).getAbsolutePath().replaceAll("'","''") + "','" + MediaFileAPI.GetMediaFileFormatDescription(mediafile).trim() + "'," + mediatype + "," + mediagroup + ", " + MediaFileAPI.GetSize(mediafile) +
                       ", " + MediaFileAPI.GetFileDuration(mediafile) + ", " + AiringAPI.GetLatestWatchedTime(mediafile) + "," + AiringAPI.GetAiringStartTime(mediafile) + ", current_timestamp)";
            ortus.api.executeSQL(SQL);
        }

//        if ( ! metadatafound ) {
//            return true;
//        }

	String workYear = ShowAPI.GetShowYear(mediafile);
	if ( workYear.isEmpty() && ! releasedate.isEmpty())
		workYear = releasedate.substring(0,4);

	if ( releasedate.isEmpty())
		releasedate="1970-01-01";
	
        SQL ="UPDATE sage.mediavideos SET description ='" + overview +"', releasedate='" + releasedate+"',userrating=" + rating + ", mpaarated = '" + rated + "', " +
                " mediayear = '" + ShowAPI.GetShowYear(mediafile) + "', tmdbid = " + String.valueOf(id) + ", imdbid = '" + String.valueOf(imdbid) + "', metadatasource = '" + metadatasource + "',trailer = '" + trailer + "' " +
                " WHERE mediaid = " + String.valueOf(mfid);
        success = ortus.api.executeSQL(SQL);
        if(success < 1){
            SQL =  "INSERT INTO sage.mediavideos (mediaid, mediayear, description, releasedate, userrating, mpaarated, tmdbid, imdbid, metadatasource, trailer) " +
                       " VALUES(" + mfid + ", '" + workYear + "','" + overview + "','" + releasedate + "'," + rating + ",'" + rated + "'," + String.valueOf(id) + ",'" + imdbid + "','" + metadatasource + "','" + trailer + "')";
            ortus.api.executeSQL(SQL);
        }

        SQL = "delete from sage.fanart where mediaid = " + String.valueOf(mfid);
        ortus.api.executeSQL(SQL);

        for ( ImageItem ii : images ) {
            ii.WriteImageDB();
        }

        SQL = "delete from sage.genre where mediaid = " + String.valueOf(mfid);
        ortus.api.executeSQL(SQL);
        for ( String g : genres ) {
            SQL = "insert into sage.genre ( mediaid, name) values ( " + String.valueOf(mfid) + " , '" + g + "')";
            ortus.api.executeSQL(SQL);
        }
        SQL = "delete from sage.cast where mediaid = " + String.valueOf(mfid);
        ortus.api.executeSQL(SQL);
     for (CastItem ci : cast) {
		    ci.WriteToDB(mediafile);
		    SQL = "insert into sage.cast ( mediaid, personid, name, job, character) values ( " + String.valueOf(mfid) + ", " + id + ",'" + ci.GetName().replaceAll("'", "''") + "','" + ci.GetJob().replaceAll("'", "''") + "','" + ci.GetCharacter().replaceAll("'", "''") + "')";
		    ortus.api.executeSQL(SQL);
	    }
        return true;
    }

//    public boolean ReadProperty(Object mediafile) {
//        Properties mp = new Properties();
//        try {
//            File propfile = new File(MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath() + ".properties");
//
//            if ( propfile.exists()) {
//		ortus.api.DebugLog(LogLevel.Trace2,"readProperty: " + propfile.getAbsolutePath());
//                mp.load(new FileInputStream(propfile));
//                String[] workproviders = mp.getProperty("MediaProviderDataID").split(";");
//		for ( String prov : workproviders ) {
//			String[] subprov = prov.split(":");
//	                if ( subprov[0].equalsIgnoreCase("themoviedb"))
//			     id = subprov[1];
//			if ( subprov[0].equalsIgnoreCase("theTVDB"))
//			     id = subprov[1];
//	                if ( subprov[0].equalsIgnoreCase("imdb"))
//	                    imdbid = subprov[1];
//		}
//		ortus.api.DebugLog(LogLevel.Trace2,"ID: " + id + " IMDB: " + imdbid);
//                name = mp.getProperty("MediaTitle");
//		if ( id.isEmpty() && imdbid.isEmpty())
//			return false;
//                return true;
//            } else
//                return false;
//        } catch (Exception e) {
//                ortus.api.DebugLog(INFO, "ReadProperty Exception: " + e );
//                return false;
//        }
//    }
//    public void WriteProperty(Object mediafile) {
//        SortedProperties mp = new SortedProperties();
//
//        mp.setProperty("MediaTitle", name);
//        mp.setProperty("Title", name);
//        mp.setProperty("Description", overview);
//        mp.setProperty("RunningTime", runtime);
//        if ( mediatype == 2 )
//            mp.setProperty("MediaType", "Movie");
//        else if ( mediatype == 3)
//            mp.setProperty("MediaType", "Series");
//        else if ( mediatype == 4)
//            mp.setProperty("MediaType", "HomeMovie");
//        else
//            mp.setProperty("MediaType","Recording");
//
//        mp.setProperty("MPAA",rated);
//        mp.setProperty("UserRating",String.valueOf(Math.round(rating)));
//        mp.setProperty("OriginalAirDate", releasedate);
//        mp.setProperty("Trailer", trailer);
//
//        StringBuffer xgenre = new StringBuffer(10);
//        for ( String g : genres) {
//            xgenre.append(g + " ");
//        }
//        mp.setProperty("Genre", xgenre.toString().trim());
//
//        StringBuffer xactor = new StringBuffer(10);
//        StringBuffer xdirector = new StringBuffer(10);
//        StringBuffer xwriter = new StringBuffer(10);
//        for ( CastItem ci : cast ) {
//            ortus.api.DebugLog(LogLevel.Trace2,"Cast: " + ci.GetName() + " doing: " + ci.GetJob());
//            if ( ci.GetJob().equals("Actor"))
//                xactor.append(ci.GetName() + " -- " + ci.GetCharacter() + ";\n");
//            if ( ci.GetJob().equals("Director"))
//                xdirector.append(ci.GetName() + ";");
//            if ( ci.GetJob().equals("Writer"))
//                xwriter.append(ci.GetName() + ";");
//        }
//        mp.setProperty("Director", xdirector.toString().trim());
//        mp.setProperty("Writer", xwriter.toString().trim());
//        mp.setProperty("Actor", xactor.toString().trim());
//
//	String metadataprovider = "MediaProviderDataID";
//        if ( ! id.isEmpty())
//	    metadataprovider+="themoviedb:" + id;
//	if ( !id.isEmpty() && !imdbid.isEmpty())
//		metadataprovider+=";";
//        if ( ! imdbid.isEmpty())
//	    metadataprovider+="imdb:" + imdbid;
//
//        mp.setProperty("MediaProviderDataID", metadataprovider);
//
//        try {
//            mp.store(new FileOutputStream(new File(MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath() + ".properties")), "Generator: Ortus; MediaFile: file:" + MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath());
//        } catch (IOException ex) {
//            ortus.api.DebugLog(ERROR, "Movie: Failed to write property file: " + MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath() + ".properties" );
//            return;
//        }
//
//        return;
//    }
}
