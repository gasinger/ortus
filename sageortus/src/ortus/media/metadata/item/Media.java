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
import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

/**
 *
 * @author jphipps
 */
public class Media extends ortus.vars implements Serializable,IItem {
	private static final long serialVersionUID = 1L;
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
	int userrating = 0;
	String mpaarated = "";
	String releasedate = "";
        private int seriesid = 0;
        private int seasonno = 0;
        private int episodeid = 0;
        private int episodeno = 0;
        
	HashMap<String,HashMap<String,List<Fanart>>> fanart = new HashMap<String,HashMap<String,List<Fanart>>>();
	List<String> genre = new ArrayList<String>(4);
        private List<Cast> cast = new ArrayList<Cast>();

	public Media() {
	}

	public Media( int mediaid, String mediatitle, String episodetitle, String mediapath,
		String Description, String mediaencoding, int mediatype, int mediagroup, 
		long airingstarttime, int userrating, String mpaarated, String releasedate, long mediaduration, long mediasize, long mediaimporttime) {
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

//        ortus.api.DebugLogTrace("toHash() for id: " + mediaid);
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
//        x.put("lastwatched",lastwatched);
        x.put("airingstarttime",airingstarttime);
        x.put("userrating",userrating);
        x.put("mpaarated",mpaarated);
        x.put("releasedate",releasedate);
        x.put("genre",genre);
        x.put("trailer", getTrailer());
        x.put("mediaimporttime", getMediaimporttime());

        if ( MediaFileAPI.IsTVFile(MediaFileAPI.GetMediaFileForID(mediaid))) {
//            ortus.api.DebugLogTrace("toHash() loading TV");
            Object mf = MediaFileAPI.GetMediaFileForID(mediaid);
            x.put("channelname",AiringAPI.GetAiringChannelName(mf));
            x.put("channelnumber",AiringAPI.GetAiringChannelNumber(mf));
            x.put("airingduration",AiringAPI.GetAiringDuration(mf));
            x.put("airingstarttime",AiringAPI.GetAiringStartTime(mf));
            x.put("airingendtime",AiringAPI.GetAiringEndTime(mf));
        }

        if ( seriesid > 0)
            x.put("seriesid",seriesid);
        if ( seasonno > 0)
            x.put("seasonno",seasonno);
        if ( episodeid > 0)
            x.put("episodeid",episodeid);
        if ( episodeno > 0)
            x.put("episodeno",episodeno);
//        List castlist = new ArrayList();
//        for ( Cast ic : cast) {
//            HashMap y = new HashMap();
//            y.put("name",ic.getName());
//            y.put("job",ic.getJob());
//            y.put("character",ic.getCharacter());
//            castlist.add(y);
//        }
//        x.put("cast",castlist);
//        ortus.api.DebugLogTrace("toHash() loading fanart");
        if ( fanart.get("Backgrounds") != null) {
  //              x.put("background_high", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Backgrounds").get("high").get(0)).getFile());
             x.put("background_id",((Fanart)fanart.get("Backgrounds").get("high").get(0)).getId());
        }
        if ( fanart.get("Posters") != null) {
 //               x.put("posters_high", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Posters").get("high").get(0)).getFile());
                x.put("posters_id",((Fanart)fanart.get("Posters").get("high").get(0)).getId());
        }
        if ( fanart.get("Banners") != null) {
  //              x.put("banners_high", ortus.api.GetFanartFolder() + java.io.File.separator + ((Fanart)fanart.get("Banners").get("high").get(0)).getFile());
                x.put("banners_id",((Fanart)fanart.get("Banners").get("high").get(0)).getId());
        }
//        ortus.api.DebugLogTrace("GetMetadata: Returning: " + x);
        
        return x;
    }

    public HashMap toHashFull() {
        HashMap result = toHash();

        result.put("sage_isdvd", MediaFileAPI.IsDVD(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_isbluray", MediaFileAPI.IsBluRay(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_istvfile", MediaFileAPI.IsTVFile(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_islibrary", MediaFileAPI.IsLibraryFile(MediaFileAPI.GetMediaFileForID(mediaid)));        

        result.put("sage_mediatitle", ShowAPI.GetShowTitle(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_episodetitle", ShowAPI.GetShowEpisode(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_overview", ShowAPI.GetShowDescription(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_mpaarating", ShowAPI.GetShowRated(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_year", ShowAPI.GetShowYear(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_category", ShowAPI.GetShowCategory(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_subcategory", ShowAPI.GetShowSubCategory(MediaFileAPI.GetMediaFileForID(mediaid)));

        result.put("sage_externalid", ShowAPI.GetShowExternalID(MediaFileAPI.GetMediaFileForID(mediaid)));
        result.put("sage_airingid", AiringAPI.GetAiringID(MediaFileAPI.GetMediaFileForID(mediaid)));
        
//        List images = new ArrayList();
//        result.put("fanart", images);
//
//        for ( HashMap fh : fanart.values()) {
//            for ( Object fih : fh.values()) {
//                List<Fanart> fal = (List<Fanart>)fih;
//                for (Fanart fai : fal) {
//                    HashMap x = new HashMap();
//                    x.put("id", fai.getId());
//                    x.put("source",fai.getUrl());
//                    x.put("file",ortus.api.GetFanartFolder() + java.io.File.separator + fai.getFile());
//                    x.put("type",fai.getType());
//                    x.put("res",fai.getImagetype());
//                    x.put("height",fai.getHeight());
//                    x.put("width",fai.getWidth());
//                    images.add(x);
//                }
//            }
//        }

        
        HashMap<String,List<HashMap>> images = new HashMap<String,List<HashMap>>();
        result.put("fanart", images);

        for ( Object it : fanart.keySet().toArray()) {
            HashMap<String,List<Fanart>> fh = fanart.get((String)it);
            HashMap<String,HashMap> holdhash = new HashMap<String,HashMap>();
            for ( Object x : fh.keySet().toArray()) {
                List<Fanart> z =fh.get((String)x);

                for (Fanart fai : z) {
                    if ( holdhash.get(fai.getMetadataid()) == null)
                        holdhash.put(fai.getMetadataid(), new HashMap());

                    HashMap entry = new HashMap();
                    entry.put("id", fai.getId());
                    entry.put("source",fai.getUrl());
                    if ( fai.getFile() == null)
                        entry.put("file","");
                    else {
                        if ( fai.getFile().equalsIgnoreCase("null"))
                            entry.put("file","");
                        else
                            entry.put("file",ortus.api.GetFanartFolder() + java.io.File.separator + fai.getFile());
                    }
                    entry.put("type",fai.getType());
                    entry.put("height",fai.getHeight());
                    entry.put("width",fai.getWidth());
                    entry.put("imagesize",fai.getMediasize());

                    holdhash.get(fai.getMetadataid()).put(fai.getImagetype(), entry);


                }
            }
            if ( images.get((String)it) == null)
                 images.put((String)it, new ArrayList<HashMap>());
            for ( HashMap x : holdhash.values()) {
                images.get((String)it).add(x);
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
            xtw.writeStartElement("MediaFile");
            WriteElement(xtw,"mediaid",String.valueOf(mediaid));
            WriteElement(xtw,"title",mediatitle);
            WriteElement(xtw,"episodetitle",episodetitle);
            WriteElement(xtw,"mediapath",mediapath);
            WriteElement(xtw,"description",Description);
            WriteElement(xtw,"mediaencoding",mediaencoding);
            WriteElement(xtw,"mediatype",String.valueOf(mediatype));
            WriteElement(xtw,"mediagroup",String.valueOf(mediagroup));
            WriteElement(xtw,"mediasize",String.valueOf(mediasize));
            WriteElement(xtw,"mediaduration",String.valueOf(mediaduration));
            WriteElement(xtw,"lastwatched",String.valueOf(lastwatched));
            WriteElement(xtw,"userrating",String.valueOf(userrating));
            WriteElement(xtw,"mpaarated",String.valueOf(mpaarated));
            WriteElement(xtw,"releasedate",String.valueOf(releasedate));
           // genre
            WriteElement(xtw,"trailer",String.valueOf(getTrailer()));
            WriteElement(xtw,"mediaimporttime",String.valueOf(getMediaimporttime()));

            if ( MediaFileAPI.IsTVFile(MediaFileAPI.GetMediaFileForID(mediaid))) {
                Object mf = MediaFileAPI.GetMediaFileForID(mediaid);
                WriteElement(xtw,"channelname",AiringAPI.GetAiringChannelName(mf));
                WriteElement(xtw,"channelnumber",AiringAPI.GetAiringChannelNumber(mf));
                WriteElement(xtw,"airingduration",String.valueOf(AiringAPI.GetAiringDuration(mf)));
                WriteElement(xtw,"airingstarttime",String.valueOf(AiringAPI.GetAiringStartTime(mf)));
                WriteElement(xtw,"airingendtime",String.valueOf(AiringAPI.GetAiringEndTime(mf)));
            }

            if ( seriesid > 0)
                WriteElement(xtw,"seriesid",String.valueOf(seriesid));
            if ( seasonno > 0)
                WriteElement(xtw,"seasonno",String.valueOf(seasonno));
            if ( episodeid > 0)
                WriteElement(xtw,"episodeid",String.valueOf(episodeid));
            if ( episodeno > 0)
                WriteElement(xtw,"episodeno",String.valueOf(episodeno));

            WriteElement(xtw,"sage_isdvd", String.valueOf(MediaFileAPI.IsDVD(MediaFileAPI.GetMediaFileForID(mediaid))));
            WriteElement(xtw,"sage_isbluray", String.valueOf(MediaFileAPI.IsBluRay(MediaFileAPI.GetMediaFileForID(mediaid))));
            WriteElement(xtw,"sage_istvfile", String.valueOf(MediaFileAPI.IsTVFile(MediaFileAPI.GetMediaFileForID(mediaid))));
            WriteElement(xtw,"sage_islibrary", String.valueOf(MediaFileAPI.IsLibraryFile(MediaFileAPI.GetMediaFileForID(mediaid))));

            WriteElement(xtw,"sage_mediatitle", ShowAPI.GetShowTitle(MediaFileAPI.GetMediaFileForID(mediaid)));
            WriteElement(xtw,"sage_episodetitle", ShowAPI.GetShowEpisode(MediaFileAPI.GetMediaFileForID(mediaid)));
            WriteElement(xtw,"sage_overview", ShowAPI.GetShowDescription(MediaFileAPI.GetMediaFileForID(mediaid)));
            WriteElement(xtw,"sage_mpaarating", ShowAPI.GetShowRated(MediaFileAPI.GetMediaFileForID(mediaid)));
            WriteElement(xtw,"sage_year", ShowAPI.GetShowYear(MediaFileAPI.GetMediaFileForID(mediaid)));
            WriteElement(xtw,"sage_category", ShowAPI.GetShowCategory(MediaFileAPI.GetMediaFileForID(mediaid)));
            WriteElement(xtw,"sage_subcategory", ShowAPI.GetShowSubCategory(MediaFileAPI.GetMediaFileForID(mediaid)));

            WriteElement(xtw,"sage_externalid", ShowAPI.GetShowExternalID(MediaFileAPI.GetMediaFileForID(mediaid)));
            WriteElement(xtw,"sage_airingid", String.valueOf(AiringAPI.GetAiringID(MediaFileAPI.GetMediaFileForID(mediaid))));

            xtw.writeStartElement("fanartimages");
            for ( Object it : fanart.keySet().toArray()) {
                HashMap<String,List<Fanart>> fh = fanart.get((String)it);
                HashMap<String,HashMap> holdhash = new HashMap<String,HashMap>();
                for ( Object x : fh.keySet().toArray()) {
                    List<Fanart> z =fh.get((String)x);

                    for (Fanart fai : z) {
                        if ( holdhash.get(fai.getMetadataid()) == null)
                            holdhash.put(fai.getMetadataid(), new HashMap());

                        xtw.writeStartElement("fanart");

                        WriteElement(xtw,"id",String.valueOf(fai.getId()));
                        WriteElement(xtw,"source",String.valueOf(fai.getUrl()));
                        
                        if ( fai.getFile() == null)
                            WriteElement(xtw,"file","");
                        else {
                            if ( fai.getFile().equalsIgnoreCase("null"))
                                WriteElement(xtw,"file","");
                            else
                                WriteElement(xtw,"file",ortus.api.GetFanartFolder() + java.io.File.separator + fai.getFile());
                        }
                        WriteElement(xtw,"type",fai.getType());
                        WriteElement(xtw,"height",String.valueOf(fai.getHeight()));
                        WriteElement(xtw,"width",String.valueOf(fai.getWidth()));
                        WriteElement(xtw,"imagesize",String.valueOf(fai.getMediasize()));
                        xtw.writeEndElement();
                    }
                }               
            }
            xtw.writeEndElement();
            
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
                        HashMap<String,List<Fanart>> xh = new HashMap<String,List<Fanart>>();
                        xh.put(cif.getImagetype(), x);
			fanart.put(cif.getType(), xh);
		}

	}

	public List<Fanart> GetFanart(String fanarttype, String imagetype) {

            if ( fanart.get(fanarttype) != null) {              
                if ( fanart.get(fanarttype).get(imagetype) != null)
                    return fanart.get(fanarttype).get(imagetype);
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
	public int GetUserRating() {
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
