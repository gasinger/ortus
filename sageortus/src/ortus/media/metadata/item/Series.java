/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.metadata.item;

import ortus.media.metadata.item.Fanart;
import ortus.media.metadata.item.Cast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jphipps
 */
public class Series implements Serializable,IItem {
    private static final long serialVersionUID = 1L;
    boolean isValid = false;
    private int seriesid;
    private String imdbid;
    private String zap2itid;
    private String title;
    private String firstair;
    private String airday;
    private String airtime;
    private String status;
    private String description;
    private String network;
    private int userrating;
    private String mpaarated;
    private long runtime;
    HashMap<String,List<Fanart>> fanart = new HashMap<String,List<Fanart>>();
    private List<String> genre = new ArrayList<String>();
    private List<Cast> cast = new ArrayList<Cast>();

    public Series() {
    }
    
    public Series(int seriesid, String imdbid, String zap2itid, String title, String firstair, String airday, String airtime, String status, String description, String network, int userrating, String mpaarated, long runtime) {
        this.seriesid = seriesid;
        this.imdbid = imdbid;
        this.zap2itid = zap2itid;
        this.title = title;
        this.firstair = firstair;
        this.airday = airday;
        this.airtime = airtime;
        this.status = status;
        this.description = description;
        this.network = network;
        this.userrating = userrating;
        this.mpaarated = mpaarated;
        this.runtime = runtime;
        isValid = true;
    }

    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return "cacheItemSeries{" + "seriesid=" + seriesid + "imdbid=" + imdbid + "zap2itid=" + zap2itid + "title=" + title + "firstair=" + firstair + "airday=" + airday + "airtime=" + airtime + "status=" + status + "description=" + description + "network=" + network + "userrating=" + userrating + "mpaarated=" + mpaarated + "runtime=" + runtime + "fanart=" + fanart + "genre=" + genre + "cast=" + cast + '}';
    }

    public HashMap toHash() {
        HashMap x = new HashMap();

        x.put("type","series");
        x.put("seriesid",seriesid);
        x.put("imdbid",imdbid);
        x.put("zap2itid",zap2itid);
        x.put("title",title);
        x.put("firstair",firstair);
        x.put("airday", airday);
        x.put("airtime",airtime);
        x.put("status",status);
        x.put("description",description);
        x.put("network", network);
        x.put("userrating",userrating);
        x.put("mpaarated",mpaarated);
        x.put("runtime", runtime);
        if ( fanart.get("Backgrounds") != null)
            if ( fanart.get("Backgrounds").size() > 0)
                x.put("background", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Backgrounds").get(0)).getFile());
        if ( fanart.get("Posters") != null)
            if ( fanart.get("Posters").size() > 0)
                x.put("poster", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Posters").get(0)).getFile());
        if ( fanart.get("Banners") != null)
            if ( fanart.get("Banners").size() > 0)
                x.put("banner", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Banners").get(0)).getFile());

        x.put("genre",genre);
        List castlist = new ArrayList();
        for ( Cast ic : cast) {
            HashMap y = new HashMap();
            y.put("name",ic.getName());
            y.put("job",ic.getJob());
            y.put("character",ic.getCharacter());
            castlist.add(y);
        }
        x.put("cast",castlist);

        List<Object> y = ortus.api.executeSQLQuery("select count(*) from sage.episode where mediaid is not null and seriesid = " + seriesid);
        if ( y.size() > 0)
          x.put("episode",y.get(0));
        y = ortus.api.executeSQLQuery("select count(distinct(seasonno)) from sage.episode where mediaid is not null and seriesid = " + seriesid);
        if ( y.size() > 0)
          x.put("season",y.get(0));
                            
        return x;
    }

    public HashMap toHashFull() {
        HashMap result = toHash();
        return result;
    }
    /**
     * @return the seriesid
     */
    public int getSeriesid() {
        return seriesid;
    }

    /**
     * @param seriesid the seriesid to set
     */
    public void setSeriesid(int seriesid) {
        this.seriesid = seriesid;
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
     * @return the zap2itid
     */
    public String getZap2itid() {
        return zap2itid;
    }

    /**
     * @param zap2itid the zap2itid to set
     */
    public void setZap2itid(String zap2itid) {
        this.zap2itid = zap2itid;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the firstair
     */
    public String getFirstair() {
        return firstair;
    }

    /**
     * @param firstair the firstair to set
     */
    public void setFirstair(String firstair) {
        this.firstair = firstair;
    }

    /**
     * @return the airday
     */
    public String getAirday() {
        return airday;
    }

    /**
     * @param airday the airday to set
     */
    public void setAirday(String airday) {
        this.airday = airday;
    }

    /**
     * @return the airtime
     */
    public String getAirtime() {
        return airtime;
    }

    /**
     * @param airtime the airtime to set
     */
    public void setAirtime(String airtime) {
        this.airtime = airtime;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the network
     */
    public String getNetwork() {
        return network;
    }

    /**
     * @param network the network to set
     */
    public void setNetwork(String network) {
        this.network = network;
    }

    /**
     * @return the userrating
     */
    public int getUserrating() {
        return userrating;
    }

    /**
     * @param userrating the userrating to set
     */
    public void setUserrating(int userrating) {
        this.userrating = userrating;
    }

    /**
     * @return the mpaarated
     */
    public String getMpaarated() {
        return mpaarated;
    }

    /**
     * @param mpaarated the mpaarated to set
     */
    public void setMpaarated(String mpaarated) {
        this.mpaarated = mpaarated;
    }

    /**
     * @return the runtime
     */
    public long getRuntime() {
        return runtime;
    }

    /**
     * @param runtime the runtime to set
     */
    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    /**
     * @return the genre
     */
    public List<String> getGenre() {
        return genre;
    }

    public void addGenre(String catagory) {
        genre.add(catagory);
    }

    /**
     * @return the cast
     */
    public List<Cast> getCast() {
        return cast;
    }

    public void addCast(Cast cic) {
        cast.add(cic);
    }

    public void AddFanart(Fanart cif) {
            if ( fanart.get(cif.getType()) != null) {
                    fanart.get(cif.getType()).add(cif);
            } else {
                    List<Fanart> x = new ArrayList<Fanart>();
                    x.add(cif);
                    fanart.put(cif.getType(), x);
            }

    }

    public List<Fanart> GetFanart(String fanarttype, String imagetype) {
            List<Fanart> results = new ArrayList<Fanart>();
            for( Fanart cif : fanart.get(fanarttype)) {
                if ( cif.getImagetype().equalsIgnoreCase(imagetype))
                    results.add(cif);
            }
            return results;
    }

    @Override
    public String toXML() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
