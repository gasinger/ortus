/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import ortus.onlinescrapper.MediaObject;
import ortus.vars.LogLevel;
import sagex.api.ShowAPI;
/**
 *
 * @author jphipps
 */
public class Movie implements Serializable {
    private String metadataid;
    private int mediaid;
    private String Type;
    private String tmdbid;
    private String imdbid;
    private String originalname;
    private String name;
    private String alternatename;
    private String url;
    private Integer votes;
    private float rating;
    private String tagline;
    private String certification;
    private String releasedate;
    private Integer runtime;
    private long budget;
    private long revenue;
    private String homepage;
    private String trailer;
    private String overview;
    private String metadatasource;
    private String Path;
    private boolean metadatafound = false;
    private List<String> genres = new ArrayList<String>();
    private List<ImageItem> images = new ArrayList<ImageItem>();
    private List<CastItem> cast = new ArrayList<CastItem>();
    
    private int mediatype = 0;
    private int mediagroup = 1;

    public Movie() {
    }

    public Movie(MediaObject mo) {
	ortus.api.DebugLog(LogLevel.Trace,"Movie: create");
	name = mo.getShowtitle();
	mediatype = mo.getMediaTypeInt();
	mediagroup = mo.getMediaGroupInt();
	overview = ShowAPI.GetShowDescription(mo.getMedia());
	certification = ShowAPI.GetShowRated(mo.getMedia());
        metadatafound=true;
	metadatasource="Sage";
    }

    public Movie(String metadataid) {
        this.metadataid = metadataid;
    }

    public Movie(String metadataid, int mediaid) {
        this.metadataid = metadataid;
        this.mediaid = mediaid;
    }

    public String getMetadataid() {
        return metadataid;
    }

    public void setMetadataid(String metadataid) {
        this.metadataid = metadataid;
    }

    public int getMediaid() {
        return mediaid;
    }

    public void setMediaid(int mediaid) {
        this.mediaid = mediaid;
    }

    public String getTmdbid() {
        return tmdbid;
    }

    public void setTmdbid(String tmdbid) {
        this.tmdbid = tmdbid;
        metadataid = "TM" + this.tmdbid;
    }

    public String getImdbid() {
        return imdbid;
    }

    public void setImdbid(String imdbid) {
        this.imdbid = imdbid;
        if ( metadataid == null)
            metadataid = "IM" + this.imdbid;
    }

    public String getOriginalName() {
        return getOriginalname();
    }

    public void setOriginalName(String originalname) {
        this.setOriginalname(originalname);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlternateName() {
        return getAlternatename();
    }

    public void setAlternateName(String alternatename) {
        this.setAlternatename(alternatename);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(String releasedate) {
        this.releasedate = releasedate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getMetadatasource() {
        return metadatasource;
    }

    public void setMetadatasource(String metadatasource) {
        this.metadatasource = metadatasource;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (metadataid != null ? metadataid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movie)) {
            return false;
        }
        Movie other = (Movie) object;
        if ((this.metadataid == null && other.metadataid != null) || (this.metadataid != null && !this.metadataid.equals(other.metadataid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ortus.onlinescrapper.themoviedb.MoviesNew[metadataid=" + metadataid + "]";
    }

    /**
     * @return the mediatype
     */
    public int getMediatype() {
        return mediatype;
    }

    /**
     * @param mediatype the mediatype to set
     */
    public void setMediatype(int mediatype) {
        this.mediatype = mediatype;
    }

    /**
     * @return the mediagroup
     */
    public int getMediagroup() {
        return mediagroup;
    }

    /**
     * @param mediagroup the mediagroup to set
     */
    public void setMediagroup(int mediagroup) {
        this.mediagroup = mediagroup;
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
        images.add(new ImageItem(metadataid, type, size,url));
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

        int pstfa = 0;

        ortus.api.DebugLog(LogLevel.Trace2, "DownloadImages to: " + destination + " Limit: " + fanart_limit);
        for( ImageItem ii : images ) {
            if ( ii.IsBackgrounds()) {
                if( ii.IsOriginal())
                    bgfa++;
                if ( bgfa > fanart_limit)
                        continue;
            }
            if ( ii.IsPoster()) {
                if( ii.IsOriginal() ) 
                    pstfa++;
                if ( pstfa > fanart_limit)
                    continue;
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
        ortus.api.DebugLog(LogLevel.Trace2,"TMDBID: " + tmdbid);
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
    
    public boolean WriteDB() {
//        WriteProperty(mediafile);

        if ( ! tmdbid.isEmpty()) {
            metadataid="TM" + tmdbid;
        } else if ( !imdbid.isEmpty()) {
            metadataid="IM" + imdbid;
        } else if ( mediaid > 0) {
            metadataid="UR" + mediaid;
        } else
            return false;
            
	if ( releasedate.isEmpty())
		releasedate="1970-01-01";

        Connection conn = ortus.api.GetConnection();

        QueryRunner qr = new QueryRunner();

        try {
            int updatecount = qr.update(conn,"update sage.movies set mediaid=?, tmdbid=?, imdbid=? , original_name=? , name=?, alternate_name=? , url=? , votes=?, rating=? , tagline=? , certification=? , releasedate=?, runtime=?,budget=?,revenue=?,homepage=? , trailer=? , overview=? , metadatasource=? where metadataid = ?",
                    mediaid,tmdbid,imdbid, getOriginalname(),name, getAlternatename(),url,votes,rating,tagline,certification,releasedate,runtime,budget,revenue,homepage,trailer,overview,metadatasource,metadataid);
            if ( updatecount == 0) {
                updatecount = qr.update(conn,"INSERT INTO sage.movies (metadataid, mediaid, tmdbid, imdbid , original_name , name, alternate_name , url , votes, rating , tagline , certification , releasedate, runtime,budget,revenue,homepage , trailer , overview , metadatasource ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    metadataid,mediaid,tmdbid,imdbid, getOriginalname(),name, getAlternatename(),url,votes,rating,tagline,certification,releasedate,runtime,budget,revenue,homepage,trailer,overview,metadatasource);
            }

            updatecount = qr.update(conn,"delete from sage.fanart where metadataid = ?", metadataid);
            updatecount = qr.update(conn,"delete from sage.genre where metadataid = ?", metadataid);
            updatecount = qr.update(conn,"delete from sage.cast where metadataid = ?", metadataid);
            
            for ( ImageItem ii : images ) {
                ii.WriteImageDB();
            }

            for ( String g : genres ) {
                updatecount = qr.update(conn,"insert into sage.genre ( metadataid, name) values (?,?)", metadataid, g);
            }
            for (CastItem ci : cast) {
                ci.WriteToDB(metadataid);
                updatecount = qr.update(conn, "insert into sage.cast (metadataid, personid, name, job,character) values ( ?,?,?,?,?)", metadataid, tmdbid,ci.GetName(),ci.GetJob(),ci.GetCharacter());
            }
        } catch ( Exception e) {
            ortus.api.DebugLog(LogLevel.Error,"WriteDB: Exception: " + e);
            e.printStackTrace();
        } finally { 
             try { DbUtils.close(conn); } catch(Exception e) {}
        }

        return true;
    }

    /**
     * @return the Type
     */
    public String getType() {
        return Type;
    }

    /**
     * @param Type the Type to set
     */
    public void setType(String Type) {
        this.Type = Type;
    }

    /**
     * @return the originalname
     */
    public String getOriginalname() {
        return originalname;
    }

    /**
     * @param originalname the originalname to set
     */
    public void setOriginalname(String originalname) {
        this.originalname = originalname;
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
     * @return the Path
     */
    public String getPath() {
        return Path;
    }

    /**
     * @param Path the Path to set
     */
    public void setPath(String Path) {
        this.Path = Path;
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
