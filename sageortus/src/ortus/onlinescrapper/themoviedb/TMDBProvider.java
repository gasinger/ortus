/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLEventReader;
import ortus.onlinescrapper.tools.XMLHelper;

/**
 *
 * @author jphipps
 */
public class TMDBProvider extends ortus.vars {

    private String ProviderName = "TMDB";

    public String GetProviderName() {
        return ProviderName;
    }

       public HashMap<String,SearchResult> Search(String title,int limit) {
 	   XMLEventReader xmlReader = null;

           int search_count = 0;
           long t0 = System.currentTimeMillis();
           ortus.api.DebugLog(LogLevel.Info, ProviderName + " : Search for raw title: " + title + " clean: " + CleanName(title));
           HashMap<String,SearchResult> resultTitles = new HashMap<String,SearchResult>();

	   try {
		xmlReader = XMLHelper.getEventReaderUrl("http://api.themoviedb.org/2.1/Movie.search/en/xml/6d89df83f02af2b3b712a9e63f8be6fb/" + URLEncoder.encode(CleanName(title),"UTF-8"));

		while (xmlReader.hasNext()) {
			Movie movie = ortus.onlinescrapper.themoviedb.TheMovieDB.parseNextMovie(xmlReader);
                        if ( movie == null)
                            continue;
			if (movie.isMetadatafound()) {
//                           ortus.api.DebugLog(LogLevel.Trace, " metadatafound");
		           if ( ! movie.getName().isEmpty()) {
				SearchResult sr = new SearchResult(movie.getName(),"themoviedb:" + movie.getTmdbid());
				if ( movie.getReleasedate() != null)
					sr.setDate(movie.getReleasedate());
                                sr.setDescription(movie.getOverview());
			        ortus.api.DebugLog(LogLevel.Info, ProviderName + " Found: " + sr.toString());
				resultTitles.put(movie.getName(), sr);
			   }
                }
            }
            long ttotal = System.currentTimeMillis() - t0;
            ortus.api.DebugLog(LogLevel.Info, ProviderName + " : Search returned " + resultTitles.size() + " in " + ttotal + " ms");
            return resultTitles;
          } catch ( Exception e ) {
              ortus.api.DebugLog(LogLevel.Error, ProviderName + " : Search Exception: " + e);
              return resultTitles;
          }
       }

       public String CleanName(String title) {
        String cleantitle = null;

        cleantitle = title.replace("_WS", "");
        cleantitle = cleantitle.replaceAll("_LB","");
        cleantitle = cleantitle.replaceAll("_43","");
        cleantitle = cleantitle.replaceAll("_16X9LB_NA","");
        cleantitle = cleantitle.replaceAll("_16X9LB","");
        cleantitle = cleantitle.replaceAll("_4X3FF","");
        cleantitle = cleantitle.replaceAll("_169","");
        cleantitle = cleantitle.replaceAll("_4X3LB","");
        cleantitle = cleantitle.replaceAll("UNRATED", "");
        cleantitle = cleantitle.replaceAll("RATED", "");
        cleantitle = cleantitle.replaceAll("_", " ");
        cleantitle = cleantitle.replaceAll("\\.","");
        cleantitle = cleantitle.replaceAll("16X9","");
        
        cleantitle = cleantitle.toLowerCase();

//        Pattern pattern = Pattern.compile("\\(.*\\d+.*\\)");
//        Matcher matcher = pattern.matcher(cleantitle);
//        StringBuffer sb = new StringBuffer(cleantitle.length());
//        while (matcher.find())
//           matcher.appendReplacement(sb, Matcher.quoteReplacement(""));
//
//        matcher.appendTail(sb);
//        cleantitle = sb.toString();
//        System.out.println("title: " + cleantitle);

        return cleantitle;
    }
}
