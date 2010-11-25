/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.trailers;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jphipps
 */
public class movieinfo {
    private info info;
    private List<String> cast = new ArrayList<String>();
    private List<String> genre = new ArrayList<String>();
    private String location;
    private String xlarge;
    private String large;

    /**
     * @return the cast
     */
    public List<String> getCast() {
        return cast;
    }

    /**
     * @param cast the cast to set
     */
    public void setCast(List<String> cast) {
        this.cast = cast;
    }

    /**
     * @return the genre
     */
    public List<String> getGenre() {
        return genre;
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the xlarge
     */
    public String getXlarge() {
        return xlarge;
    }

    /**
     * @param xlarge the xlarge to set
     */
    public void setXlarge(String xlarge) {
        this.xlarge = xlarge;
    }

    /**
     * @return the large
     */
    public String getLarge() {
        return large;
    }

    /**
     * @param large the large to set
     */
    public void setLarge(String large) {
        this.large = large;
    }

    /**
     * @return the info
     */
    public info getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(info info) {
        this.info = info;
    }
}


