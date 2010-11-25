/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.trailers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.log4j.Logger;
import ortus.onlinescrapper.tools.XMLHelper;
import ortus.vars.LogLevel;

/**
 *
 * @author jphipps
 */
public class api {
    
        enum TagType {
            Normal,
            Genre,
            Cast
        }


    public static String getAppleTrailerTitle(HashMap thm) {
        return (String)thm.get("title");
    }

    public static String getAppleTrailerPostDate(HashMap thm) {
        return (String)thm.get("postdate");
    }

    public static String getAppleTrailerReleaseDate(HashMap thm) {
        return (String)thm.get("releasedate");
    }
    
    public static List<HashMap> getAppleTrailers() {
        String url = "http://trailers.apple.com/trailers/home/xml/current.xml";
        return internalgetAppleTrailers(true,url);
    }

    public static List<HashMap> getAppleTrailers(String filename) {
        return internalgetAppleTrailers(false, filename);
    }
    public static List<HashMap> internalgetAppleTrailers(boolean dt, String url) {
        ortus.api.DebugLog(LogLevel.Info, "appleTrailers: Starting download from " + url);
        List<HashMap> results = new ArrayList<HashMap>();
        
        try {
            XMLEventReader xmlReader = null;
            if ( dt == true)
                xmlReader = XMLHelper.getEventReaderUrl(url);
            else
                xmlReader = XMLHelper.getEventReaderFile(url);
 
            List<movieinfo> trailers = new ArrayList<movieinfo>();

            movieinfo mi;

            while((mi = parseNextTrailer(xmlReader)) != null) {
                trailers.add(mi);
            }

            for ( movieinfo x : trailers) {
                ortus.api.DebugLog(LogLevel.Trace, "appleTrailer: " + x.getInfo().getTitle());
                HashMap y = new HashMap();
                y.put("title", x.getInfo().getTitle());
                y.put("runtime", x.getInfo().getRuntime());
                y.put("rating", x.getInfo().getRating());
                y.put("studio", x.getInfo().getStudio());
                y.put("postdate", x.getInfo().getPostdate());
                y.put("releasedate", x.getInfo().getReleasedate());
                y.put("copyright", x.getInfo().getCopyright());
                y.put("director", x.getInfo().getDirector());
                y.put("description", x.getInfo().getDescription());
                String cast = x.getCast().toString();
                cast = cast.replaceAll("\\[","");
                cast = cast.replaceAll("]","");
                y.put("cast", cast);
                String genre = x.getGenre().toString();
                genre = genre.replaceAll("\\[","");
                genre = genre.replaceAll("]", "");
                y.put("genre", genre);
                y.put("location", x.getLocation());
                y.put("large", x.getLarge());
                y.put("xlarge",x.getXlarge());
                results.add(y);
            }
        } catch (Exception e) {
            ortus.api.DebugLog(LogLevel.Error, "appleTrailer: Exception: " , e);
            Logger log = Logger.getLogger("ortus.onlinescrapper.trailers");
            log.error(e);
            return null;
        }
        ortus.api.DebugLog(LogLevel.Info, "appleTrailers: Completed; Number of trailers: " + results.size());
        return results;
    }

    public static void downloadTrailers(String dirname) {
        ortus.api.DebugLog(LogLevel.Info, "appleTrailers: Starting download");
        String url = "http://trailers.apple.com/trailers/home/xml/current.xml";
        try {
            ortus.Ortus.getInstance().getDownloadServer().AddDownloadQueue("autoclean", "appleTrailers", url, dirname);
         } catch (Exception e) {
            ortus.api.DebugLog(LogLevel.Error, "appleTrailer: Exception: " , e);
            Logger log = Logger.getLogger("ortus.onlinescrapper.trailers");
            log.error(e);
        }

    }

    public static movieinfo parseNextTrailer(XMLEventReader xmlReader) throws XMLStreamException {
	movieinfo movie = null;
        TagType tt = TagType.Normal;
        
	while (xmlReader.hasNext()) {

	    XMLEvent event = xmlReader.nextEvent();

	    if (event.isStartElement()) {

		StartElement se = event.asStartElement();
		String tag = se.getName().getLocalPart();
//		ortus.api.DebugLog(LogLevel.Trace2,"parsing tag: " + tag);
		if (tag.equalsIgnoreCase("movieinfo")) {
		    movie = new movieinfo();
                    movie.setInfo(new info());
		} else if (tag.equalsIgnoreCase("title")) {
		    movie.getInfo().setTitle(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("runtime")) {
		    movie.getInfo().setRuntime(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("rating")) {
		    movie.getInfo().setRating(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("studio")) {
		    movie.getInfo().setStudio(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("postdate")) {
		    movie.getInfo().setPostdate(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("releasedate")) {
		    movie.getInfo().setReleasedate(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("copyright")) {
		    movie.getInfo().setCopyright(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("director")) {
		    movie.getInfo().setDirector(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("description")) {
		    movie.getInfo().setDescription(XMLHelper.getCData(xmlReader));
                } else if ( tag.equalsIgnoreCase("location")) {
                    movie.setLocation(XMLHelper.getCData(xmlReader));
                } else if ( tag.equalsIgnoreCase("large")) {
                    movie.setLarge(XMLHelper.getCData(xmlReader));
                } else if ( tag.equalsIgnoreCase("xlarge")) {
                    movie.setXlarge(XMLHelper.getCData(xmlReader));
		} else if ( tag.equalsIgnoreCase("cast")) {
                    tt = TagType.Cast;
                } else if ( tag.equalsIgnoreCase("genre")) {
                    tt = TagType.Genre;
                } else if ( tag.equalsIgnoreCase("name")) {
                    switch(tt) {
                        case Cast: movie.getCast().add(XMLHelper.getCData(xmlReader));
                            break;
                        case Genre: movie.getGenre().add(XMLHelper.getCData(xmlReader));
                    }
                }

	    } else if (event.isEndElement()) {
		if (event.toString().equalsIgnoreCase("</movieinfo>")) {
		    break;
		}
	    }
	}
	return movie;
    }
}
