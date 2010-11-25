/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media.metadata.item;

import ortus.media.metadata.item.Fanart;
import ortus.media.metadata.item.Cast;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import sagex.api.MediaFileAPI;

/**
 *
 * @author jphipps
 */
public class Media extends ortus.vars implements Serializable,IItem {
        boolean isValid = false;
	int mediaid = 0;
	String mediatitle ="";
	String episodetitle = "";
	String mediapath = "";
	String Description = "";
	String mediaencoding = "";
        private String trailer = "";
	int mediatype = 0;
	int mediagroup = 0;
	private long mediasize =0;
        private long mediaduration = 0;
        private long mediaimporttime = 0;
	long lastwatched = 0;
	long airingstarttime= 0;
	float userrating = 0;
	String mpaarated = "";
	String releasedate = "";
        private int seriesid = 0;
        private int seasonno = 0;
        private int episodeid = 0;
        private int episodeno = 0;
        
	HashMap<String,HashMap<Integer,List<Fanart>>> fanart = new HashMap<String,HashMap<Integer,List<Fanart>>>();
	List<String> genre = new ArrayList<String>(4);
        private List<Cast> cast = new ArrayList<Cast>();

	public Media() {
	}

	public Media( int mediaid, String mediatitle, String episodetitle, String mediapath,
		String Description, String mediaencoding, int mediatype, int mediagroup, 
		long airingstarttime, float userrating, String mpaarated, String releasedate, long mediaduration, long mediasize, long mediaimporttime) {
		this.mediaid = mediaid;
		this.mediatitle = mediatitle;
		this.episodetitle = episodetitle;
		this.mediapath = mediapath;
		this.Description = Description;
		this.mediaencoding = mediaencoding;
		this.mediatype = mediatype;
		this.mediagroup = mediagroup;
		this.airingstarttime = airingstarttime;
		this.userrating = userrating;
		this.mpaarated = mpaarated;
		this.releasedate = releasedate;
                this.mediaduration = mediaduration;
                this.mediasize = mediasize;
                this.mediaimporttime = mediaimporttime;

//                this.mediasize = MediaFileAPI.GetSize(MediaFileAPI.GetMediaFileForID(mediaid));
//                this.mediaduration = MediaFileAPI.GetFileDuration(MediaFileAPI.GetMediaFileForID(mediaid));
                isValid = true;
	}

    public boolean isValid() {
        return isValid;
    }
    @Override
    public String toString() {
        return "cacheItemMedia{" + "mediaid=" + mediaid + "mediatitle=" + mediatitle + "episodetitle=" + episodetitle + "mediapath=" + mediapath + "Description=" + Description + "mediaencoding=" + mediaencoding + "mediatype=" + mediatype + "mediagroup=" + mediagroup + "mediasize=" + getMediasize() + "mediaduration=" + getMediaduration() + "lastwatched=" + lastwatched + "airingstarttime=" + airingstarttime + "userrating=" + userrating + "mpaarated=" + mpaarated + "releasedate=" + releasedate + "fanart=" + fanart + "genre=" + genre + "cast=" + cast + '}';
    }

    public HashMap toHash() {
        HashMap x = new HashMap();

        x.put("type","media");
        x.put("mediaid",mediaid);
        x.put("title",mediatitle);
        x.put("episodetitle",episodetitle);
        x.put("mediapath",mediapath);
        x.put("description",Description);
        x.put("mediaencoding",mediaencoding);
        x.put("mediatype",mediatype);
        x.put("mediagroup", mediagroup);
        x.put("mediasize", getMediasize());
        x.put("mediaduration", getMediaduration());
        x.put("lastwatched",lastwatched);
        x.put("airingstarttime",airingstarttime);
        x.put("userrating",userrating);
        x.put("mpaarated",mpaarated);
        x.put("releasedate",releasedate);
        x.put("genre",genre);
        x.put("trailer", getTrailer());
        x.put("mediaimporttime", getMediaimporttime());

        if ( seriesid > 0)
            x.put("seriesid",seriesid);
        if ( seasonno > 0)
            x.put("seasonno",seasonno);
        if ( episodeid > 0)
            x.put("episodeid",episodeid);
        if ( episodeno > 0)
            x.put("episodeno",episodeno);
        List castlist = new ArrayList();
        for ( Cast ic : cast) {
            HashMap y = new HashMap();
            y.put("name",ic.getName());
            y.put("job",ic.getJob());
            y.put("character",ic.getCharacter());
            castlist.add(y);
        }
        x.put("cast",castlist);
        if ( fanart.get("Backgrounds") != null) {
            if ( fanart.get("Backgrounds").get(3) != null)
                x.put("background_high", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Backgrounds").get(3).get(0)).getFile());
            if ( fanart.get("Backgrounds").get(2) != null)
                x.put("background_mid", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Backgrounds").get(2).get(0)).getFile());
            if ( fanart.get("Backgrounds").get(1) != null)
                x.put("background_thumb", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Backgrounds").get(1).get(0)).getFile());
        }
        if ( fanart.get("Posters") != null) {
            if ( fanart.get("Posters").get(3) != null)
                x.put("posters_high", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Posters").get(3).get(0)).getFile());
            if ( fanart.get("Posters").get(2) != null)
                x.put("posters_mid", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Posters").get(2).get(0)).getFile());
            if ( fanart.get("Posters").get(1) != null)
                x.put("posters_thumb", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Posters").get(1).get(0)).getFile());
        }
        if ( fanart.get("Banners") != null) {
            if ( fanart.get("Banners").get(3) != null)
                x.put("baners_high", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Banners").get(3).get(0)).getFile());
            if ( fanart.get("Banners").get(2) != null)
                x.put("banners_mid", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Banners").get(2).get(0)).getFile());
            if ( fanart.get("Banners").get(1) != null)
                x.put("banners_thumb", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Banners").get(1).get(0)).getFile());
        }

        return x;
    }

    public HashMap toHashFull() {
        HashMap result = toHash();

        for ( HashMap fh : fanart.values()) {
            for ( Object fih : fh.values()) {
                List<Fanart> fal = (List<Fanart>)fih;
                int loopcount=0;
                for (Fanart fai : fal) {
                    if ( result.get(fai.getType()) == null) {
                        List x = new ArrayList();
                        x.add(ortus.api.GetFanartFolder() + java.io.File.separator + fai.getFile());
                        result.put(fai.getType(),x);
                    } else {
                        ((List)result.get(fai.getType())).add(ortus.api.GetFanartFolder() + java.io.File.separator + fai.getFile());
                    }
                }
            }
        }

        return result;
    }

       public String toXML() {
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xtw = null;
        StringWriter strw = new StringWriter();
        try {
            xtw = xof.createXMLStreamWriter(strw);
//            xtw.writeStartDocument("1.0");
            xtw.writeStartElement("Media");
            WriteElement(xtw,"MediaID",String.valueOf(mediaid));
            WriteElement(xtw,"Title",mediatitle);
            WriteElement(xtw,"EpisodeTitle",episodetitle);
            WriteElement(xtw,"MediaPath",mediapath);
            WriteElement(xtw,"Description",Description);
            WriteElement(xtw,"MediaEncoding",mediaencoding);
            WriteElement(xtw,"MediaType",String.valueOf(mediatype));
            WriteElement(xtw,"MediaGroup",String.valueOf(mediagroup));
            WriteElement(xtw,"MediaSize",String.valueOf(mediasize));
            WriteElement(xtw,"MediaDuration",String.valueOf(mediaduration));
            WriteElement(xtw,"LastWatched",String.valueOf(lastwatched));
            WriteElement(xtw,"UserRating",String.valueOf(userrating));
            xtw.writeEndElement();
//            xtw.writeEndDocument();
            xtw.flush();
            xtw.close();
        } catch (Exception e) {
            ortus.api.DebugLog(LogLevel.Error, "WriteXML: Exception " + e);
        }
        return strw.toString();
    }

    public void WriteElement(XMLStreamWriter xtw, String ElementName, String ElementValue) {
        try {
        xtw.writeStartElement(ElementName);
        xtw.writeCData(ElementValue);
        xtw.writeEndElement();
        } catch(Exception e) {
            ortus.api.DebugLogError("WriteElementException",e);
        }
    }
	public void AddFanart(Fanart cif) {
		if ( fanart.get(cif.getType()) != null) {
                    if ( fanart.get(cif.getType()).get(cif.getImagetype()) != null)
			fanart.get(cif.getType()).get(cif.getImagetype()).add(cif);
                    else {
                        List<Fanart> x = new ArrayList<Fanart>();
                        x.add(cif);
                        fanart.get(cif.getType()).put(cif.getImagetype(), x);
                    }
		} else {
			List<Fanart> x = new ArrayList<Fanart>();
			x.add(cif);
                        HashMap<Integer,List<Fanart>> xh = new HashMap<Integer,List<Fanart>>();
                        xh.put(cif.getImagetype(), x);
			fanart.put(cif.getType(), xh);
		}

	}

	public List<Fanart> GetFanart(String fanarttype, int imagetype) {

            if ( fanart.get(fanarttype) != null) {
                if ( fanart.get(fanarttype).get(imagetype) != null)
                    return fanart.get(fanarttype).get(imagetype);
            }

            return new ArrayList<Fanart>();
//            return fanart.get(fanarttype);
//		Object[] keys = fanart.keySet().toArray();
//		for ( Object o : keys) {
//			ortus.api.DebugLog(TRACE2,"Fanart key: " + o);
//			for ( Object x : fanart.get(o)) {
//				ortus.api.DebugLog(TRACE2,"  Fanart File: " + x);
//			}
//		}

//		for ( int x = imagetype; x >= 0; x--) {
//			String fanartkey = fanarttype+"-"+String.valueOf(x);
////			ortus.api.DebugLog(TRACE2,"Fanart key: " + fanartkey);
//			if( fanart.get(fanartkey) != null) {
////				ortus.api.DebugLog(TRACE2,"Fanart: " + fanart.get(fanartkey));
//				return (List<Object>)fanart.get(fanartkey);
//			}
//		}
//		return new ArrayList();
	}


	public void AddGenre(String genre) {
		this.genre.add(genre);
	}

	public List<String> GetGenre() {
		return genre;
	}

	public int GetMediaId() {
		return mediaid;
	}
	public void SetMediaID(int mediaid) {
		this.mediaid = mediaid;
	}
	public String GetMediaTitle() {
//            ortus.api.DebugLogInfo("GetMediaTitle: " + mediatitle);
		return mediatitle;
	}
	public void SetMediaTitle(String mediatitle) {
		this.mediatitle = mediatitle;
		this.episodetitle = mediatitle;
	}
	public String GetEpisodeTitle() {
		return episodetitle;
	}
	public String GetMediaPath() {
		return mediapath;
	}
	public String GetDescription() {
		return Description;
	}
	public String GetMediaEncoding() {
		return mediaencoding;
	}
	public int GetMediaType() {
		return mediatype;
	}
	public boolean IsTV() {
		if ( mediatype == 1 || mediatype == 3)
			return true;
		else
			return false;
	}
	public boolean IsMovie() {
		if ( mediatype == 2)
			return true;
		else
			return false;
	}
	public boolean IsRecording() {
		if ( mediagroup == 0)
			return true;
		else
			return false;
	}
	public int GetMediaGroup() {
		return mediagroup;
	}
	public long GetAiringStartTime() {
		return airingstarttime;
	}
	public float GetUserRating() {
		return userrating;
	}
	public String GetMPAARated() {
		return mpaarated;
	}
	public String GetReleaseDate() {
		return releasedate;
	}

    /**
     * @return the cast
     */
    public List<Cast> getCast() {
        return cast;
    }

    /**
     * @param cast the cast to set
     */
    public void addCast(Cast cic) {
        cast.add(cic);
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
     * @return the mediasize
     */
    public long getMediasize() {
//       return  MediaFileAPI.GetSize(MediaFileAPI.GetMediaFileForID(mediaid));
        return mediasize;
    }

    /**
     * @param mediasize the mediasize to set
     */
    public void setMediasize(long mediasize) {
        this.mediasize = mediasize;
    }

    /**
     * @return the mediaduration
     */
    public long getMediaduration() {

//       return MediaFileAPI.GetFileDuration(MediaFileAPI.GetMediaFileForID(mediaid));

        return mediaduration;
    }

    /**
     * @param mediaduration the mediaduration to set
     */
    public void setMediaduration(long mediaduration) {
        this.mediaduration = mediaduration;
    }

    /**
     * @return the mediaimporttime
     */
    public long getMediaimporttime() {
        return mediaimporttime;
    }

    /**
     * @param mediaimporttime the mediaimporttime to set
     */
    public void setMediaimporttime(long mediaimporttime) {
        this.mediaimporttime = mediaimporttime;
    }

}
