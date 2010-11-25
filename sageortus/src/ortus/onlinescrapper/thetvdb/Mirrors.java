package ortus.onlinescrapper.thetvdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import ortus.onlinescrapper.tools.XMLHelper;

/**
 *
 * @author altman.matthew
 */
public class Mirrors {

    public static String TYPE_XML = "XML";
    public static String TYPE_BANNER = "BANNER";
    public static String TYPE_ZIP = "ZIP";
    
    private static final Random RNDM = new Random();
    
    private List<String> xmlList = new ArrayList<String>();
    private List<String> bannerList = new ArrayList<String>();
    private List<String> zipList = new ArrayList<String>();
    
    public Mirrors(String apiKey) {
        try {
            XMLEventReader xmlReader = null;
            try {
                xmlReader = XMLHelper.getEventReaderUrl("http://www.thetvdb.com/api/" + apiKey + "/mirrors.xml");
                
                int typeMask = 0;
                String url = null;
                
                while (xmlReader.hasNext()) {
                    XMLEvent event = xmlReader.nextEvent();
                    if (event.isStartElement()) {
                        String tag = event.toString();
                        
                        if (tag.equalsIgnoreCase("<mirrorpath>")) {
                            url = XMLHelper.getCData(xmlReader);
                        } else if (tag.equalsIgnoreCase("<typemask>")) {
                            typeMask = Integer.parseInt(XMLHelper.getCData(xmlReader));
                        }
                    }
                    
                    if (event.isEndElement()) {
                        if (event.toString().equalsIgnoreCase("</Mirror>")) {
                            addMirror(typeMask, url);
                        }
                    }
                }
            } finally {
                if (xmlReader != null) {
                    xmlReader.close();
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: TheTVDB API -> " + e.getMessage());
        }
    }
    
    public String getMirror(String type) {
        String url = null;
        if (type.equals(TYPE_XML) && !xmlList.isEmpty()) {
            url = xmlList.get(RNDM.nextInt(xmlList.size()));
        } else if (type.equals(TYPE_BANNER) && !bannerList.isEmpty()) {
            url = bannerList.get(RNDM.nextInt(bannerList.size()));
        } else if (type.equals(TYPE_ZIP) && !zipList.isEmpty()) {
            url = zipList.get(RNDM.nextInt(zipList.size()));
        }
	if ( url == null)
		url = "http://thetvdb.com";
        return url;
    }
    
    private void addMirror(int typeMask, String url) {
        switch (typeMask) {
            case 1: xmlList.add(url);
                    break;
            case 2: bannerList.add(url);
                    break;
            case 3: xmlList.add(url);
                    bannerList.add(url);
                    break;
            case 4: zipList.add(url);
                    break;
            case 5: xmlList.add(url);
                    zipList.add(url);
                    break;
            case 6: bannerList.add(url);
                    zipList.add(url);
                    break;
            case 7: xmlList.add(url);
                    bannerList.add(url);
                    zipList.add(url);
                    break;
        }
    }
}
