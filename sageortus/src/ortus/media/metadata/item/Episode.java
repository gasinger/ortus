/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.metadata.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jphipps
 */
public class Episode implements Serializable,IItem  {
    private boolean isValid = false;
    private int episodeid;
    private int seriesid;
    private int episodeno;
    private int seasonid;
    private int seasonno;
    private int mediaid;
    private String title;
    private String description;
    private String originalairdate;
    private float userrating;
    private String thumbpath;
    private String fanart;
    private List<Cast> cast = new ArrayList<Cast>();

    public Episode() {
    }

    public Episode(int episodeid, int seriesid, int episodeno, int seasonid, int seasonno, int mediaid, String title, String description, String originalairdate, float userrating, String thumbpath) {
        this.episodeid = episodeid;
        this.seriesid = seriesid;
        this.episodeno = episodeno;
        this.seasonid = seasonid;
        this.seasonno = seasonno;
        this.mediaid = mediaid;
        this.title = title;
        this.description = description;
        this.originalairdate = originalairdate;
        this.userrating = userrating;
        this.thumbpath = thumbpath;
        isValid=true;

    }

    public boolean isValid() {
        return isValid;
    }
    
    @Override
    public String toString() {
        return "cacheItemEpisode{" + "episodeid=" + episodeid + "seriesid=" + seriesid + "episodeno=" + episodeno + "seasonid=" + seasonid + "seasonno=" + seasonno + "mediaid=" + mediaid + "title=" + title + "description=" + description + "originalairdate=" + originalairdate + "userrating=" + userrating + "thumbpath=" + thumbpath + '}';
    }

    public HashMap toHash() {
        HashMap x = new HashMap();

        x.put("type","episode");
        x.put("episodeid",episodeid);
        x.put("seriesid",seriesid);
        x.put("episodeno", episodeno);
        x.put("seasonid",seasonid);
        x.put("seasonno",seasonno);
        x.put("mediaid", mediaid);
        x.put("title", title);
        x.put("description", description);
        x.put("originalairdate" ,originalairdate);
        x.put("userrating",userrating);
        x.put("thumbpath",thumbpath);
        x.put("poster", ortus.api.GetFanartFolder() + java.io.File.separator + fanart);
        List castlist = new ArrayList();
        for ( Cast ic : cast) {
            HashMap y = new HashMap();
            y.put("name",ic.getName());
            y.put("job",ic.getJob());
            y.put("character",ic.getCharacter());
            castlist.add(y);
        }
        x.put("cast",castlist);

        return x;
    }

    public HashMap toHashFull() {
        HashMap result = toHash();
        return result;
    }
    /**
     * @return the episodeid
     */
    public int getEpisodeid() {
        return episodeid;
    }

    /**
     * @param episodeid the episodeid to set
     */
    public void setEpisodeid(int episodeid) {
        this.episodeid = episodeid;
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
     * @return the episodeno
     */
    public int getEpisodeno() {
        return episodeno;
    }

    /**
     * @param episodeno the episodeno to set
     */
    public void setEpisodeno(int episodeno) {
        this.episodeno = episodeno;
    }

    /**
     * @return the seasonid
     */
    public int getSeasonid() {
        return seasonid;
    }

    /**
     * @param seasonid the seasonid to set
     */
    public void setSeasonid(int seasonid) {
        this.seasonid = seasonid;
    }

    /**
     * @return the seasonno
     */
    public int getSeasonno() {
        return seasonno;
    }

    /**
     * @param seasonno the seasonno to set
     */
    public void setSeasonno(int seasonno) {
        this.seasonno = seasonno;
    }

    /**
     * @return the mediaid
     */
    public int getMediaid() {
        return mediaid;
    }

    /**
     * @param mediaid the mediaid to set
     */
    public void setMediaid(int mediaid) {
        this.mediaid = mediaid;
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
     * @return the originalairdate
     */
    public String getOriginalairdate() {
        return originalairdate;
    }

    /**
     * @param originalairdate the originalairdate to set
     */
    public void setOriginalairdate(String originalairdate) {
        this.originalairdate = originalairdate;
    }

    /**
     * @return the userrating
     */
    public float getUserrating() {
        return userrating;
    }

    /**
     * @param userrating the userrating to set
     */
    public void setUserrating(float userrating) {
        this.userrating = userrating;
    }

    /**
     * @return the thumbpath
     */
    public String getThumbpath() {
        return thumbpath;
    }

    /**
     * @param thumbpath the thumbpath to set
     */
    public void setThumbpath(String thumbpath) {
        this.thumbpath = thumbpath;
    }

    /**
     * @return the fanart
     */
    public String getFanart() {
        return fanart;
    }

    /**
     * @param fanart the fanart to set
     */
    public void setFanart(String fanart) {
        this.fanart = fanart;
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
}
