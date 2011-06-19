/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.metadata.item;

import java.io.Serializable;

/**
 *
 * @author jphipps
 */
public class Fanart implements Serializable {
    private int id;
    private String metadataid;
    private int default_ind;
    private int height;
    private int width;
    private String imagetype;
    private String type;
    private String url;
    private String file;
    private long mediasize;

    public Fanart(int id, String metadataid,int default_ind,int height,int width, String imagetype,String type,String url, String file, long mediasize) {
        this.id = id;
        this.metadataid = metadataid;
        this.default_ind = default_ind;
        this.height = height;
        this.width = width;
        this.imagetype = imagetype;
        this.type = type;
        this.url = url;
        this.file = file;
        this.mediasize = mediasize;
    }

    public Fanart(String metadataid,int default_ind,int height,int width, String imagetype,String type,String url, String file) {
        this.metadataid = metadataid;
        this.default_ind = default_ind;
        this.height = height;
        this.width = width;
        this.imagetype = imagetype;
        this.type = type;
        this.url = url;
        this.file = file;
    }

    /**
     * @return the metadataid
     */
    public String getMetadataid() {
        return metadataid;
    }

    /**
     * @param metadataid the metadataid to set
     */
    public void setMetadataid(String metadataid) {
        this.metadataid = metadataid;
    }

    /**
     * @return the default_ind
     */
    public int getDefault_ind() {
        return default_ind;
    }

    /**
     * @param default_ind the default_ind to set
     */
    public void setDefault_ind(int default_ind) {
        this.default_ind = default_ind;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the imagetype
     */
    public String getImagetype() {
        return imagetype;
    }

    /**
     * @param imagetype the imagetype to set
     */
    public void setImagetype(String imagetype) {
        this.imagetype = imagetype;
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
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the mediasize
     */
    public long getMediasize() {
        return mediasize;
    }

    /**
     * @param mediasize the mediasize to set
     */
    public void setMediasize(long mediasize) {
        this.mediasize = mediasize;
    }
}
