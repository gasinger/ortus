/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import ortus.property.SortedProperties;
import ortus.onlinescrapper.themoviedb.CastItem;
import ortus.onlinescrapper.themoviedb.ImageItem;
import sagex.api.MediaFileAPI;

/**
 *
 * @author jphipps
 */
public class mediautilities extends ortus.vars {
    private String name = "";
    private String type = "";
    private String overview = "";
    private float rating = 5;
    private String rated = "";
    private String releasedate="1970-01-01";
    private String runtime="";
    private String trailer="";
    private String metadatasource="";
    private boolean metadatafound = false;
    private String url="";
    private String path="";
    private int mediatype = 0;
    private int mediagroup = 1;

    private String imdbid ="";
    private String tmdbid = "";
    private String tvdbid = "";

    private List<String> genres = new ArrayList<String>();
    private List<ImageItem> images = new ArrayList<ImageItem>();
    private List<CastItem> cast = new ArrayList<CastItem>();
    
   public String GetName() {
        return name;
    }
    public void SetName(String name) {
        this.name = name;
    }

    public String GetIMDBId() {
	    return imdbid;
    }
    public void SetIMDBId(String imdbid) {
	    this.imdbid = imdbid;
    }
    public String GetTMDBId() {
	    return tmdbid;
    }
    public void SetTMDBId(String tmdbid) {
	    this.tmdbid = tmdbid;
    }
    public String GetTVDBId() {
	    return tvdbid;
    }
    public void SetTVDBId(String tvdbid) {
	    this.tvdbid = tvdbid;
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

    public void Dump() {
        ortus.api.DebugLog(LogLevel.Trace2,"Dump: Running");
        ortus.api.DebugLog(LogLevel.Trace2,"Title: " + name);
    }

    public boolean ReadProperty(Object mediafile) {
        Properties mp = new Properties();
        try {
            File propfile = new File(MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath() + ".properties");

            if ( propfile.exists()) {
		ortus.api.DebugLog(LogLevel.Trace2,"readProperty: " + propfile.getAbsolutePath());
                mp.load(new FileInputStream(propfile));
                String[] workproviders = mp.getProperty("MediaProviderDataID").split(";");
		for ( String prov : workproviders ) {
			String[] subprov = prov.split(":");
	                if ( subprov[0].equalsIgnoreCase("themoviedb"))
			     tmdbid = subprov[1];
			if ( subprov[0].equalsIgnoreCase("theTVDB"))
			     tvdbid = subprov[1];
	                if ( subprov[0].equalsIgnoreCase("imdb"))
	                    imdbid = subprov[1];
		}
		ortus.api.DebugLog(LogLevel.Trace2,"TVDB: " + tvdbid + " TMDB: " + tmdbid + " IMDB: " + imdbid);
                name = mp.getProperty("MediaTitle");
		if ( tmdbid.isEmpty() && imdbid.isEmpty() && tvdbid.isEmpty())
			return false;
                return true;
            } else
                return false;
        } catch (Exception e) {
                ortus.api.DebugLog(LogLevel.Info, "ReadProperty Exception: " + e );
                return false;
        }
    }
    public void WriteProperty(Object mediafile) {
        SortedProperties mp = new SortedProperties();

        mp.setProperty("MediaTitle", name);
        mp.setProperty("Title", name);
        mp.setProperty("Description", overview);
        mp.setProperty("RunningTime", runtime);
        if ( mediatype == 2 )
            mp.setProperty("MediaType", "Movie");
        else if ( mediatype == 3)
            mp.setProperty("MediaType", "Series");
        else if ( mediatype == 4)
            mp.setProperty("MediaType", "HomeMovie");
        else
            mp.setProperty("MediaType","Recording");

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
            ortus.api.DebugLog(LogLevel.Trace2,"Cast: " + ci.GetName() + " doing: " + ci.GetJob());
            if ( ci.GetJob().equals("Actor"))
                xactor.append(ci.GetName() + " -- " + ci.GetCharacter() + ";\n");
            if ( ci.GetJob().equals("Director"))
                xdirector.append(ci.GetName() + ";");
            if ( ci.GetJob().equals("Writer"))
                xwriter.append(ci.GetName() + ";");
        }
        mp.setProperty("Director", xdirector.toString().trim());
        mp.setProperty("Writer", xwriter.toString().trim());
        mp.setProperty("Actor", xactor.toString().trim());

	String metadataprovider = "";
	List<String> ids = new ArrayList<String>();
	if ( ! tmdbid.isEmpty())
		ids.add("themoviedb:" + tmdbid);
        if ( ! tvdbid.isEmpty())
		ids.add("theTVDB:" + tvdbid);
        if ( ! imdbid.isEmpty())
		ids.add("imdb:" + imdbid);
	for ( String x : ids )
		metadataprovider+=x+";";

        mp.setProperty("MediaProviderDataID", metadataprovider);

        try {
            mp.store(new FileOutputStream(new File(MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath() + ".properties")), "Generator: Ortus; MediaFile: file:" + MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath());
        } catch (IOException ex) {
            ortus.api.DebugLog(LogLevel.Error, "Movie: Failed to write property file: " + MediaFileAPI.GetFileForSegment(mediafile, 0).getAbsolutePath() + ".properties" );
            return;
        }

        return;
    }
}
