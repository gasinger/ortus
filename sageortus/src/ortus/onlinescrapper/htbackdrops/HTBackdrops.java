/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.htbackdrops;

import java.net.URLEncoder;
import java.util.ArrayList;
import ortus.onlinescrapper.themoviedb.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class HTBackdrops extends ortus.vars {
	int searchLimit = 5;
        String htBackdropsUrl = "http://htbackdrops.com/api/a47324e3116b9249b0bc8151d30cff7d/";


public HTBackdrops() {
	
}

public void SetSearchLimit(int searchLimit) {
	this.searchLimit = searchLimit;
}

public List<HashMap> Search(String title) {
	long t0 = System.currentTimeMillis();
        HashMap temp = null;
	List<HashMap> results = new ArrayList<HashMap>();

        XMLEventReader xmlReader = null;
	ortus.api.DebugLog(LogLevel.Trace2,"Search HTBackdrops: URL: " + htBackdropsUrl + "searchXML?keywords=" + URLEncoder.encode(title));
        try {
            xmlReader = XMLHelper.getEventReaderUrl(htBackdropsUrl + "searchXML?keywords=" + URLEncoder.encode(title));

            while ( (temp = parseNextImage(xmlReader)) != null) {
                if ( title.equalsIgnoreCase((String)temp.get("title"))) {
                    results.add(temp);
                }

            }
        } catch (Exception error) {
            ortus.api.DebugLog(LogLevel.Error,"HTBackdrops: Search: Exception: ", error);
        } finally {
            XMLHelper.closeEventReader(xmlReader);
        }

	long ttotal = System.currentTimeMillis() - t0;
	ortus.api.DebugLog(LogLevel.Info, "HTBackdrops: Search returned " + results.size() + " in " + ttotal + " ms");

	return results;
}

public String getUrlLow(String id) {
    return htBackdropsUrl + "download/" + id + "/thumbnail";
}
public String getUrlMedium(String id) {
      return htBackdropsUrl + "download/" + id + "/intermediate";
}

public String getUrlHigh(String id) {
      return htBackdropsUrl + "download/" + id + "/fullsize";
}
//    public Movie GetDetail(String key) {

public static HashMap parseNextImage(XMLEventReader xmlReader) throws XMLStreamException {

        HashMap entry = null;
//        ortus.api.DebugLog(LogLevel.Debug, "parseNextMovie");
	while (xmlReader.hasNext()) {

	    XMLEvent event = xmlReader.nextEvent();

	    if (event.isStartElement()) {
		StartElement se = event.asStartElement();
		String tag = se.getName().getLocalPart();
//		ortus.api.DebugLog(LogLevel.Trace2,"parsing tag: " + tag);
		if (tag.equalsIgnoreCase("image")) {
		    entry = new HashMap();
		} else if (tag.equalsIgnoreCase("id")) {
		    entry.put("id",XMLHelper.getCData(xmlReader));
                } else if (tag.equalsIgnoreCase("filename")) {
		    entry.put("filename",XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("album")) {
		    entry.put("album",XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("title")) {
		    entry.put("title",XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("filesize")) {
		    entry.put("filesize",XMLHelper.getCData(xmlReader));
		} else if (tag.equalsIgnoreCase("dimensions")) {
                    String dims = XMLHelper.getCData(xmlReader);
                    Pattern pattern = Pattern.compile("(\\d+)x(\\d+)");
                    Matcher matcher = pattern.matcher(dims);
		    if (matcher.matches()) {
                        int w = Integer.parseInt(matcher.group(1));
                        int h = Integer.parseInt(matcher.group(2));
                        if( w == h) {
                            entry.put("type","Posters");
                        } else {
                             entry.put("type","Backgrounds");
                        }
                    } else {
                        entry.put("type","Backgrounds");
                    }

		    entry.put("dimensions",dims);
		}
	    } else if (event.isEndElement()) {
		if (event.toString().equalsIgnoreCase("</image>")) {
		    break;
		}
	    }
	}
         ortus.api.DebugLog(LogLevel.Debug, "parseNextImage return: " + entry);
	return entry;
}
}
