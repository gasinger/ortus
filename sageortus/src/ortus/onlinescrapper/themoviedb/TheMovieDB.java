/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Attribute;
import ortus.onlinescrapper.tools.XMLHelper;
import ortus.onlinescrapper.tools.database;

/**
 *
 * @author jphipps
 */
public class TheMovieDB extends ortus.vars {
	int searchLimit = 5;
	GoogleProvider gp = null;
	IMDBProvider ip = null;
	TMDBProvider tp = null;

public TheMovieDB() {
	gp = new GoogleProvider();
	ip = new IMDBProvider();
	tp = new TMDBProvider();
}

public void SetSearchLimit(int searchLimit) {
	this.searchLimit = searchLimit;
}

public HashMap<String,Movie> Search(String title, String year) {
	long t0 = System.currentTimeMillis();
	HashMap<String,Movie> results = new HashMap<String,Movie>();

	List<Movie> tresults = tp.Search(title,year,searchLimit);
        for ( Movie y : tresults)
            if ( results.get(y.getName()) == null) {
                results.put(y.getName(),y);
                if ( ! y.getAlternateName().isEmpty())
                    results.put(y.getAlternateName(), y);
                if ( ! y.getOriginalName().isEmpty())
                    results.put(y.getOriginalName(),y);
            }

//	List<Movie> gresults = gp.Search(title, searchLimit);
//	for ( Movie y : gresults)
//            if ( results.get(y.getName()) == null)
//		results.put(y.getName(),y);

	List<Movie> iresults = ip.Search(title, year, searchLimit);
	for ( Movie y : iresults)
            if ( results.get(y.getName()) == null)
                results.put(y.getName(),y);
	
	long ttotal = System.currentTimeMillis() - t0;
	ortus.api.DebugLog(LogLevel.Info, "MetadataSearch: Search returned " + results.size() + " in " + ttotal + " ms");

	return results;
}

//    public Movie GetDetail(String key) {
public Movie GetDetail(String key) {
	ortus.api.DebugLog(LogLevel.Info,"GetDetail: " + key);
	Movie cacheitem = null;
	String[] keyparts = key.split(":");
	if ( keyparts[0].trim().equals("imdb")) {
//            cacheitem = database.GetCacheMetadataIMDB(keyparts[1]);
//   	    if ( cacheitem != null ) {
//		return cacheitem;
//	    }
	    cacheitem = GetItemDetailByIMDB(keyparts[1].trim());
//	    if ( cacheitem.getImdbid().isEmpty()) {
//		    cacheitem = ip.GetItemDetailByIMDB(keyparts[1]);
//	    }
	} else if (keyparts[0].trim().equalsIgnoreCase("themoviedb")) {
            try {
//                cacheitem = database.GetCacheMetadataTMDB(keyparts[1]);
//                if ( cacheitem != null ) {
//                    return cacheitem;
//                }
                cacheitem = GetItemDetailByTMDB(keyparts[1].trim());
                if ( cacheitem.isMetadatafound()) {
                    if ( cacheitem.getImdbid() != null) {
                            Movie tempmovie = ip.GetItemDetailByIMDB(cacheitem.getImdbid());
                            if ( tempmovie != null) {
                                if ( tempmovie.getCertification() != null)
                                        cacheitem.setCertification(tempmovie.getCertification());
//                                if ( cacheitem.GetCast().size() == 0) {
//                                        for ( CastItem ci : tempmovie.GetCast()) {
//                                                cacheitem.AddCast(ci.GetId(),ci.GetName(), ci.GetJob(), ci.GetCharacter());
//                                        }
//                                }
                            }
                    }
                }
            } catch ( Exception e) {
                ortus.api.DebugLog(LogLevel.Error, "GetDetail: Exception",e);
            }
	}

//        database.cacheMetadata(cacheitem);

	ortus.api.DebugLog(LogLevel.Info,"GetDetail: Complete");
	
        return cacheitem;
    }

public Movie GetItemDetailByIMDB(String imdbid) {
//	Movie cacheitem = database.GetCacheMetadataIMDB(imdbid);
//        if ( cacheitem != null )
//            return cacheitem;
	return GetItemDetail("http://api.themoviedb.org/2.1/Movie.imdbLookup/en/xml/6d89df83f02af2b3b712a9e63f8be6fb/" + imdbid);
}

public Movie GetItemDetailByTMDB(String tmdbid) {
//	Movie cacheitem = database.GetCacheMetadataTMDB(tmdbid);
//        if ( cacheitem != null )
//            return cacheitem;
	return GetItemDetail("http://api.themoviedb.org/2.1/Movie.getInfo/en/xml/6d89df83f02af2b3b712a9e63f8be6fb/" + tmdbid);
}

private Movie GetItemDetail(String url) {
	Movie movie = null;
	XMLEventReader xmlReader = null;
        ortus.api.DebugLog(LogLevel.Info, "GetItemDetail URL: " +  url);

	try {
		xmlReader = XMLHelper.getEventReaderUrl(url);

                if ( xmlReader == null) {
                    ortus.api.DebugLog(LogLevel.Info, "GetItemDetail Nout found for URL: " +  url);
                    return new Movie();
                }
//		ortus.api.DebugLog(LogLevel.Trace,"Getting ready to parse from url: " + url);
		movie = parseNextMovie(xmlReader);
//                ortus.api.DebugLog(LogLevel.Trace,"Getting ready to parse from url: " + url + " Completed");
		if ( movie == null)
			return new Movie();
	} catch (Exception error) {
		ortus.api.DebugLog(LogLevel.Error,"TheMovieDB: GetItemDetail: Exception: " , error);
		return new Movie();
	} finally {
		XMLHelper.closeEventReader(xmlReader);
	}

	for ( CastItem ci : movie.GetCast()) {
		GetPersonDetail(ci);
	}
	ortus.api.DebugLog(LogLevel.Trace,"GetItemDetail: Retrning");
	return movie;

}
private void GetPersonDetail(CastItem ci) {
	XMLEventReader xmlReader = null;
	String url = "http://api.themoviedb.org/2.1/Person.getInfo/en/xml/6d89df83f02af2b3b712a9e63f8be6fb/" + ci.GetId();

	try {
		xmlReader = XMLHelper.getEventReaderUrl(url);

//		ortus.api.DebugLog(LogLevel.Trace,"Getting ready to parse from url: " + url);
		parseNextPerson(xmlReader,ci);
	} catch (Exception error) {
		ortus.api.DebugLog(LogLevel.Error,"TheMovieDB: GetPersonDetail: Exception: ", error);
		return;
	} finally {
		XMLHelper.closeEventReader(xmlReader);
	}
	return;

}

public static Movie parseNextMovie(XMLEventReader xmlReader) throws XMLStreamException {
	Movie movie = null;

//        ortus.api.DebugLog(LogLevel.Debug, "parseNextMovie");
	while (xmlReader.hasNext()) {

	    XMLEvent event = xmlReader.nextEvent();

	    if (event.isStartElement()) {

		StartElement se = event.asStartElement();
		String tag = se.getName().getLocalPart();
//		ortus.api.DebugLog(LogLevel.Trace2,"parsing tag: " + tag);
		if (tag.equalsIgnoreCase("movie")) {
		    movie = new Movie();
		} else if (tag.equalsIgnoreCase("name")) {
		    movie.setName(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("alternative_name")) {
		    movie.setAlternateName(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("original_name")) {
		    movie.setOriginalName(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("type")) {
		    movie.setType(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("id")) {
                    movie.setMetadatafound(true);
		    movie.setTmdbid(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("overview")) {
		    movie.setOverview(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("certification")) {
		    movie.setCertification(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("votes")) {
		    movie.setVotes(XMLHelper.getCDataInt(xmlReader));
		} else if (tag.equalsIgnoreCase("tagline")) {
		    movie.setTagline(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("homepage")) {
		    movie.setHomepage(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("budget")) {
		    movie.setBudget(XMLHelper.getCDataLong(xmlReader));
		} else if (tag.equalsIgnoreCase("revenue")) {
		    movie.setRevenue(XMLHelper.getCDataLong(xmlReader));
		} else if (tag.equalsIgnoreCase("rating")) {
		    movie.setRating(XMLHelper.getCDataInt(xmlReader));
		} else if (tag.equalsIgnoreCase("released")) {
		    movie.setReleasedate(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("runtime")) {
		    movie.setRuntime(XMLHelper.getCDataInt(xmlReader));
		} else if (tag.equalsIgnoreCase("trailer")) {
		    movie.setTrailer(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("imdb_id")) {
		    movie.setImdbid(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("url")) {
		    movie.setUrl(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("category")) {
			Iterator<Attribute> attributes = se.getAttributes();
			while (attributes.hasNext()) {
				Attribute attribute = attributes.next();
				if (attribute.getName().toString().equals("name"))
				   movie.AddGenre(attribute.getValue());
			}
		} else if (tag.equalsIgnoreCase("image")) {
			String itype = null;
			String isize = null;
			String iurl = null;
                        String iid = null;
                        int iwidth = 0;
                        int iheight = 0;
			Iterator<Attribute> attributes = se.getAttributes();
			while (attributes.hasNext()) {
				Attribute attribute = attributes.next();
				if (attribute.getName().toString().equals("type"))
				   itype = attribute.getValue();
				if (attribute.getName().toString().equals("size"))
				   isize = attribute.getValue();
				if (attribute.getName().toString().equals("url"))
				   iurl = attribute.getValue();
                                if (attribute.getName().toString().equals("id"))
				   iid = attribute.getValue();
			}
                        if ( ! isize.equalsIgnoreCase("cover"))
                            movie.AddImage(itype,isize,iurl,iid, iwidth, iheight);
		} else if (tag.equalsIgnoreCase("person")) {
			String id = null;
                        int wId = 0;
			String name = null;
			String job = null;
			String character = null;
			Iterator<Attribute> attributes = se.getAttributes();
			while (attributes.hasNext()) {
				Attribute attribute = attributes.next();
				if (attribute.getName().toString().equals("id"))
				   id= attribute.getValue();
				if (attribute.getName().toString().equals("name"))
				   name = attribute.getValue();
				if (attribute.getName().toString().equals("job"))
				   job = attribute.getValue();
				if (attribute.getName().toString().equals("character"))
				   character = attribute.getValue();
			}
                        try { wId = Integer.parseInt(id); } catch ( Exception e) {}
			movie.AddCast(wId, name, job, character);
		}
	    } else if (event.isEndElement()) {
		if (event.toString().equalsIgnoreCase("</movie>")) {
		    break;
		}
	    }
	}
//         ortus.api.DebugLog(LogLevel.Debug, "parseNextMovie return: " + movie);
	return movie;
}
public static void parseNextPerson(XMLEventReader xmlReader, CastItem ci) throws XMLStreamException {

	while (xmlReader.hasNext()) {

	    XMLEvent event = xmlReader.nextEvent();

	    if (event.isStartElement()) {

		StartElement se = event.asStartElement();
		String tag = se.getName().getLocalPart();
//		ortus.api.DebugLog(LogLevel.Trace2,"parsing tag: " + tag);
		if (tag.equalsIgnoreCase("person")) {
		} else if (tag.equalsIgnoreCase("name")) {
		    ci.setName(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("birthday")) {
		    ci.setBirthday(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("birthplace")) {
		    ci.setBirthplace(XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("known_movies")) {
                    ci.setKnown_movies(XMLHelper.getCDataInt(xmlReader));
                } else if (tag.equalsIgnoreCase("biography")) {
                    ci.setBiography(XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("movie")) {
			HashMap<String,String> film = new HashMap<String,String>();
			Iterator<Attribute> attributes = se.getAttributes();
			while (attributes.hasNext()) {
				Attribute attribute = attributes.next();
				if (attribute.getName().toString().equals("id"))
				   film.put("id",attribute.getValue());
				if (attribute.getName().toString().equals("name"))
				   film.put("name",attribute.getValue());
				if (attribute.getName().toString().equals("character"))
				   film.put("character", attribute.getValue());
				if (attribute.getName().toString().equals("job"))
				   film.put("job",attribute.getValue());
                                if ( attribute.getName().toString().equalsIgnoreCase("poster"))
                                    film.put("poster",attribute.getValue());
                                if (attribute.getName().toString().equalsIgnoreCase("url"))
                                    film.put("url",attribute.getValue());
			}
			ci.AddFilms(film);
		} else if (tag.equalsIgnoreCase("image")) {
			HashMap<String,String> image = new HashMap<String,String>();
			Iterator<Attribute> attributes = se.getAttributes();
			while (attributes.hasNext()) {
				Attribute attribute = attributes.next();
				if (attribute.getName().toString().equals("type"))
				   image.put("type",attribute.getValue());
				if (attribute.getName().toString().equals("size"))
				   image.put("size",attribute.getValue());
				if (attribute.getName().toString().equals("url"))
				   image.put("url",attribute.getValue());
                                if (attribute.getName().toString().equals("id"))
				   image.put("id",attribute.getValue());
			}
			ci.AddImages(image);
		}
	    } else if (event.isEndElement()) {
		if (event.toString().equalsIgnoreCase("</person>")) {
		    break;
		}
	    }
	}
	return;
}
}
