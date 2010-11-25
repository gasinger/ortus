/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media;

import ortus.vars.OrtusMediaType;
import sagex.api.MediaFileAPI;

/**
 *
 * @author jphipps
 */
public class OrtusMedia implements Comparable {
    int ID = 0;
    int SageMediaID = 0;
    OrtusMediaType MediaType = OrtusMediaType.MediaFile;
    private String title;

    public OrtusMedia() {
    }

    public OrtusMedia(int ID, OrtusMediaType omt) {
        this.ID = ID;
        this.MediaType = omt;
    }

    public OrtusMedia(Object mo) {
        if ( mo instanceof Integer)
            ID = (Integer)mo;
        else
            ID = MediaFileAPI.GetMediaFileID(mo);
    }

    public OrtusMedia(Object mo, OrtusMediaType omt) {
        ID = (Integer)mo;
        MediaType = omt;
    }

    public void Wrap(Object mo) {
        ID = MediaFileAPI.GetMediaFileID(mo);
    }

    public Object Unwrap() {
        return MediaFileAPI.GetMediaFileForID(ID);
    }

    public int GetID() {
        return ID;
    }
    
    public void SetMediaType(OrtusMediaType MediaType) {
        this.MediaType = MediaType;
    }

    public String GetMediaType() {
        return MediaType.name();
    }

    public boolean IsMediaFile() {
        if ( MediaType == OrtusMediaType.MediaFile)
            return true;
        else
            return false;
    }

    public boolean IsEpisode() {
        if ( MediaType == OrtusMediaType.Episode)
            return true;
        else
            return false;
    }

    public boolean IsSeries() {
        if ( MediaType == OrtusMediaType.Series)
            return true;
        else
            return false;
    }

    public void SetMediaID(Object Media) {
        if ( Media instanceof Integer) {
            this.SageMediaID = (Integer)Media;
        } else {
            this.SageMediaID = MediaFileAPI.GetMediaFileID(Media);
        }
    }

    public Integer GetMediaID() {
        return SageMediaID;
    }

    public String GetKey() {
        switch(MediaType) {
            case Episode: return "EP" + ID;
            case Series: return "SR" + ID;
            default: return "MD" + ID;
        }
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

    @Override
    public String toString() {
        return "OrtusMedia{" + "title=\"" + title + "\",ID=" + ID + ",SageMediaID=" + SageMediaID + ",MediaType=" + MediaType + "}";
    }

    @Override
    public int compareTo(Object t) {
        return getTitle().compareToIgnoreCase(((OrtusMedia)t).getTitle());
    }



    
}
