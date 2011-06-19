/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.themoviedb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jphipps
 */
public class GoogleProvider extends ortus.vars {

    private String ProviderName = "Google";
    
    public String GetProviderName() {
        return ProviderName;
    }

 public List<Movie> Search(String title, int limit) {
		int search_count = 0;
		long t0 = System.currentTimeMillis();
		ortus.api.DebugLog(LogLevel.Info, ProviderName + " : Search for title: " + CleanName(title));
		List<Movie> resultTitles = new ArrayList<Movie>();

		String html = SearchGoogle("themoviedb " + title);
		List<HashMap> result = getTitleAhref(html);
		for (HashMap y : result) {
			if ( y.get("tmdbid") == null || ((String)y.get("tmdbid")).isEmpty())
				continue;
			search_count++;
			Movie movie = new Movie(ortus.util.string.decodeString((String) y.get("title")));
                        movie.setTmdbid((String)y.get("tmdbid"));
			if ( y.get("year") != null)
				movie.setReleasedate((String)y.get("year"));
			ortus.api.DebugLog(LogLevel.Info, ProviderName + " :  Found: " + movie.getName());
			resultTitles.add(movie);
			if (search_count > limit) {
				break;
			}
		}

		html = SearchGoogle("imdb " + title);
		result = getTitleAhref(html);
		for (HashMap y : result) {
			if ( y.get("imdb") == null || ((String)y.get("imdb")).isEmpty())
				continue;
			search_count++;
			Movie movie = new Movie(ortus.util.string.decodeString((String) y.get("title")));
                        movie.setImdbid((String)y.get("imdbid"));
			if ( y.get("year") != null)
				movie.setReleasedate((String)y.get("year"));
			ortus.api.DebugLog(LogLevel.Info, ProviderName + " :  Found: " + movie.getName());
			resultTitles.add(movie);
			if (search_count > limit) {
				break;
			}
		}

		long ttotal = System.currentTimeMillis() - t0;
		ortus.api.DebugLog(LogLevel.Info, ProviderName + " : Search returned " + resultTitles.size() + " in " + ttotal + " ms");
		return resultTitles;
	}

   public String SearchGoogle(String title) {
		StringBuffer sb = new StringBuffer();
		try {
			// Send data
			URL url = new URL("http://www.google.com/search?q=" + URLEncoder.encode(CleanName(title),"UTF-8"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1");
			conn.connect();

			if (conn.getResponseCode() != conn.HTTP_OK) {
				ortus.api.DebugLog(LogLevel.Error, ProviderName + " : HTTP: " + conn.getResponseCode());
				if (conn.getResponseCode() == conn.HTTP_NOT_FOUND) {
					return null;
				}
				return null;
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
		} catch (Exception e) {
			System.out.println("HTTP Exception: " + e);
			return null;
		}
		return sb.toString();
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

        Pattern pattern = Pattern.compile("\\(.*\\d+.*\\)");
        Matcher matcher = pattern.matcher(cleantitle);
        StringBuffer sb = new StringBuffer(cleantitle.length());
        while (matcher.find())
           matcher.appendReplacement(sb, Matcher.quoteReplacement(""));

        matcher.appendTail(sb);
        cleantitle = sb.toString();
//        System.out.println("title: " + cleantitle);

        return cleantitle;
    }

  private static List<HashMap> getTitleAhref(String val) {
        List<HashMap> result = new ArrayList<HashMap>();
        Pattern pattern = Pattern.compile("\\<a href=\"http://www.imdb.com/title/(.*?)/.*?\\>(.*?)\\</a\\>");
        Matcher matcher = pattern.matcher(val);
        while (matcher.find()) {
//		System.out.println("ahref: " + matcher.group(0));
	    HashMap x = new HashMap();
	    x.put("imdbid",matcher.group(1));
	    String worktitle = matcher.group(2).replaceAll("\\<em\\>","");
	    worktitle = worktitle.replaceAll("\\</em\\>","");
	    Pattern titpat = Pattern.compile("(.*?)\\((.*?)\\).*");
	    Matcher titmat = titpat.matcher(worktitle);
	    if ( titmat.find()) {
		    worktitle = titmat.group(1);
		    if( titmat.groupCount() > 1)
			x.put("year",titmat.group(2));
	    }
    	    x.put("title",worktitle);

            result.add(x);
        }
        pattern = Pattern.compile("\\<a href=\"http://www.themoviedb.org/movie/(.*?)\".*?\\>(.*?)\\</a\\>");
        matcher = pattern.matcher(val);
        while (matcher.find()) {
//		System.out.println("ahref: " + matcher.group(0));
	    HashMap x = new HashMap();
	    x.put("tmdbid",matcher.group(1));
	    String worktitle = matcher.group(2).replaceAll("\\<em\\>","");
	    worktitle = worktitle.replaceAll("\\</em\\>","");
	    Pattern titpat = Pattern.compile("(.*?)\\|.*");
	    Matcher titmat = titpat.matcher(worktitle);
	    if ( titmat.find()) {
		    worktitle = titmat.group(1);
	    }
    	    x.put("title",worktitle);

            result.add(x);
        }

        return result;

    }
    

}