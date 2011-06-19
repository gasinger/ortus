package ortus.onlinescrapper.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import ortus.Ortus;

/**
 *
 * @author altman.matthew
 */
public class XMLHelper extends ortus.vars {

    public static XMLEventReader getEventReaderUrl(String url) throws IOException, XMLStreamException {
	HttpURLConnection  uCon = null;
	InputStream in = null;
	try {
		URL Url;
		Url= new URL(url);

                ortus.api.DebugLog(LogLevel.Trace, "getEventReaderUrl: URL: " + url);
		if ( ortus.api.GetSageProperty("ortus/proxy",null) != null ) {
		String[] prox = ortus.api.GetSageProperty("ortus/proxy",null).split(":");
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(prox[0], Integer.parseInt(prox[1])));
		uCon = (HttpURLConnection)Url.openConnection(proxy);
		} else {
		uCon = (HttpURLConnection)Url.openConnection();
		}

		uCon.setInstanceFollowRedirects(true);
		uCon.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1");
		uCon.connect();

		if ( uCon.getResponseCode() != uCon.HTTP_OK) {
		if ( uCon.getResponseCode() == uCon.HTTP_NOT_FOUND ) {
		    ortus.api.DebugLog(LogLevel.Info, " URL not found" );
		    return null;
		}
		ortus.api.DebugLog(LogLevel.Error, " HTTP Response: " + uCon.getResponseCode());
		ortus.api.DebugLog(LogLevel.Error, " HTTP GET Failed");
		return null;
		}

		in = uCon.getInputStream();
		return XMLInputFactory.newInstance().createXMLEventReader(in);
	} catch( Exception e) {
		ortus.api.DebugLog(LogLevel.Error,"getEventReaderUrl: Exception: " + e);
		return null;
	}
    }

    public static XMLEventReader getEventReaderFile(String pathfilename) throws IOException, XMLStreamException {
        InputStream in = new FileInputStream(new File(pathfilename));
        return XMLInputFactory.newInstance().createXMLEventReader(in);
    }

    public static XMLEventReader getEventReaderJar(String pathfilename) throws IOException, XMLStreamException {
        InputStream in = Ortus.getInstance().getJarStream(pathfilename);
        return XMLInputFactory.newInstance().createXMLEventReader(in);
    }
    public static void closeEventReader(XMLEventReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException ex) {
                System.err.println("LogLevel.Error: TheTVDB API -> " + ex.getMessage());
            }
        }
    }

    public static String getCData(XMLEventReader r) throws XMLStreamException {
        StringBuffer sb = new StringBuffer();
        while (r.peek().isCharacters()) {
            sb.append(r.nextEvent().asCharacters().getData());
        }
        return sb.toString().trim();
    }

    public static Long getCDataLong(XMLEventReader r) throws XMLStreamException {
        Long result = 0L;
        try {
            result = Long.parseLong(getCData(r));
        } catch(Exception e) {}

        return result;
    }
    
    public static int getCDataInt(XMLEventReader r) throws XMLStreamException {
        int result = 0;
        try {
            result = Integer.parseInt(getCData(r).replaceAll("min","").trim());
        } catch(Exception e) {}

        return result;
    }
    public static float getCDataFloat(XMLEventReader r) throws XMLStreamException {
        float result = 0;
        try {
            result = Float.parseFloat(getCData(r));
        } catch(Exception e) {}

        return result;
    }

}
