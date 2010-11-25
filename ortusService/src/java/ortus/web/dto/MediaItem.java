/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.web.dto;

/**
 *
 * @author jphipps
 */
public class MediaItem {
    private int sageid=0;
    private String title="";
    private String episodetitle="";
    private String mediapath="";
    private String description="";
    private String mediaencoding="";

    /**
     * @return the sageid
     */
    public int getSageid() {
        return sageid;
    }

    /**
     * @param sageid the sageid to set
     */
    public void setSageid(int sageid) {
        this.sageid = sageid;
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
     * @return the mediapath
     */
    public String getMediapath() {
        return mediapath;
    }

    /**
     * @param mediapath the mediapath to set
     */
    public void setMediapath(String mediapath) {
        this.mediapath = mediapath;
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
     * @return the mediaencoding
     */
    public String getMediaencoding() {
        return mediaencoding;
    }

    /**
     * @param mediaencoding the mediaencoding to set
     */
    public void setMediaencoding(String mediaencoding) {
        this.mediaencoding = mediaencoding;
    }
}
