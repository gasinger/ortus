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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jphipps
 */
public class IMDBProvider extends ortus.vars {

    private enum TAG { DIV, AHREF, TD, RUNTIME, MPAA, DIVBR, B };

    private String ProviderName = "IMDB";

    public String GetProviderName() {
        return ProviderName;
    }

    public List<Movie> Search(String title, String year, int limit) {
        int search_count = 0;
        long t0 = System.currentTimeMillis();
        ortus.api.DebugLog(LogLevel.Info, ProviderName + " : Search for title: " + CleanName(title));
        List<Movie> resultTitles = new ArrayList<Movie>();

        StringBuffer sb = new StringBuffer();
         try {
                // Send data
                URL url = new URL("http://www.imdb.com/find?s=all&q=" + URLEncoder.encode(CleanName(title),"UTF-8"));
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1");
                conn.connect();

                if ( conn.getResponseCode() != conn.HTTP_OK) {
                    ortus.api.DebugLog(LogLevel.Error,ProviderName + " : HTTP: " + conn.getResponseCode());
                    if ( conn.getResponseCode() == conn.HTTP_NOT_FOUND ) {
                        return resultTitles;
                    }
                    return resultTitles;
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (Exception e) {
                System.out.println("HTTP Exception: " + e);
                return resultTitles;
            }

            Pattern pattern = Pattern.compile("\\<b\\>Popular Titles\\</b\\>(.*?)\\</table\\>");
            Matcher matcher = pattern.matcher(sb);
            while (matcher.find()) {
                for ( int x = 1; x <= matcher.groupCount(); x++) {
                    search_count++;
                    List<String> y = getTitleAhref(matcher.group(x));
                    int retdat = 2;
                    String ttl = null;
                    for ( String jx : y) {
                        retdat++;
                        if ( retdat > 1) {
                            ttl = jx;
                            retdat = 0;
                            continue;
                        }
                        jx = ortus.util.string.decodeString(jx);
                        String[] filttl = ttl.split("/");
                        if ( filttl.length == 3 && ! jx.startsWith("<img")) {
                            if (! filttl[filttl.length-1].isEmpty() ) {
                                Movie movie = new Movie(jx);
                                movie.setImdbid(filttl[filttl.length-1]);
                                ortus.api.DebugLog(LogLevel.Info, ProviderName + " :   Found: " + movie.getName());
                                resultTitles.add(movie);
                            }
                        }
                    }
                    if ( search_count > limit)
                        break;
                }
                if ( search_count > limit)
                        break;
            }
            pattern = Pattern.compile("\\<b\\>Titles \\(Exact Matches\\)\\</b\\>(.*?)\\</table\\>");
            matcher = pattern.matcher(sb);
            while (matcher.find()) {
                for ( int x = 1; x <= matcher.groupCount(); x++) {
                    search_count++;
                    List<String> y = getTitleAhref(matcher.group(x));
                    int retdat = 2;
                    String ttl = null;
                    for ( String jx : y) {
                        retdat++;
                        if ( retdat > 1) {
                            ttl = jx;
                            retdat = 0;
                            continue;
                        }
                        jx = ortus.util.string.decodeString(jx);
                        String[] filttl = ttl.split("/");
                        if ( filttl.length == 3 && ! jx.startsWith("<img")) {
                            if (! filttl[filttl.length-1].isEmpty() ) {
                                Movie movie = new Movie(jx);
                                movie.setImdbid(filttl[filttl.length-1]);
                                ortus.api.DebugLog(LogLevel.Info, ProviderName + " :   Found: " + movie.getName());
                                resultTitles.add(movie);
                            }
                        }
                    }
                    if ( search_count > limit)
                        break;
                }
                if ( search_count > limit)
                    break;

            }
            pattern = Pattern.compile("\\<b\\>Titles \\(Partial Matches\\)\\</b\\>(.*?)\\</table\\>");
            matcher = pattern.matcher(sb);
            while (matcher.find()) {
                for ( int x = 1; x <= matcher.groupCount(); x++) {
                    search_count++;
                    List<String> y = getTitleAhref(matcher.group(x));
                    int retdat = 2;
                    String ttl = null;
                    for ( String jx : y) {
                        retdat++;
                        if ( retdat > 1) {
                            ttl = jx;
                            retdat = 0;
                            continue;
                        }
                        jx = ortus.util.string.decodeString(jx);
                        String[] filttl = ttl.split("/");
                        if ( filttl.length == 3&& ! jx.startsWith("<img")) {
                            if (! filttl[filttl.length-1].isEmpty() ) {
                                Movie movie = new Movie(jx);
                                movie.setImdbid(filttl[filttl.length-1]);
                                ortus.api.DebugLog(LogLevel.Info, ProviderName + " :   Found: " + movie.getName());
                                resultTitles.add(movie);
                            }
                        }
                    }
                    if ( search_count > limit)
                        break;
                }
                if ( search_count > limit)
                    break;
            }

            long ttotal = System.currentTimeMillis() - t0;
            ortus.api.DebugLog(LogLevel.Info, ProviderName + " : Search returned " + resultTitles.size() + " in " + ttotal + " ms");

            return resultTitles;
    }

    public Movie GetItemDetailByIMDB(String imdbid) {
        List<String> ret = new ArrayList<String>();

        ortus.api.DebugLog(LogLevel.Trace, ProviderName + " : GetItemDetail for id: " + imdbid);
        Movie movie = new Movie();

        movie.setImdbid(imdbid);
        
        StringBuffer sb = new StringBuffer();
         try {
                // Send data
                URL url = new URL("http://www.imdb.com/title/" + imdbid);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1");
                conn.connect();

                if ( conn.getResponseCode() != conn.HTTP_OK) {
                    ortus.api.DebugLog(LogLevel.Error,ProviderName + " : HTTP: " + conn.getResponseCode());
                    if ( conn.getResponseCode() == conn.HTTP_NOT_FOUND ) {
                        return new Movie();
                    }
                    return new Movie();
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (Exception e) {
                ortus.api.DebugLog(LogLevel.Error, ProviderName + " : GetItemDetail HTTP Exception: " + e);
                return new Movie();
            }

          ortus.api.DebugLog(LogLevel.Trace2, ProviderName + " : download completed.");
          Pattern pattern = Pattern.compile("(?i)\\<h5\\>(.+?)\\</div\\>");
          Matcher matcher = pattern.matcher(sb);
          while (matcher.find()) {
            for ( int x = 1; x <= matcher.groupCount(); x++) {

              ret = getTag(TAG.DIV, "Release Date:", matcher.group(x));
              if ( ret.size() > 0) {
//                  ortus.api.DebugLog(LogLevel.Trace2, "Parsing: " + ret.get(0));
                  Pattern pat = Pattern.compile("(.*)\\(.*\\)");
                  Matcher mat = pat.matcher(ret.get(0));
                  String temp = null;
                  if ( mat.find() )
                    temp = mat.group(1);

//                  ortus.api.DebugLog(LogLevel.Trace2, "found: " + temp);
                  DateFormat formatter = new SimpleDateFormat("dd MMMMMMMMMM yyyy");
                  Date date;
                  try {
                    date = (Date)formatter.parse(temp);
                  } catch ( Exception e) {
                      ortus.api.DebugLog(LogLevel.Error, "date parse LogLevel.Error: " + temp);
                      date = new Date();
                  }
                  movie.setReleasedate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(date));
              }
              ret = getTag(TAG.DIV, "Plot:",matcher.group(x));
              if ( ret.size() > 0) {
                  movie.setOverview(ret.get(0));
              }
              ret = getTag(TAG.AHREF, "Director:", matcher.group(x));
              if ( ret.size() > 0) {
                  for ( int xx = 0; xx < ret.size(); xx++) {
                      movie.AddCast(0,ortus.util.string.decodeString(ret.get(xx)), "Director", "");
                  }
              }

              ret = getTag(TAG.AHREF, "Writers", matcher.group(x));
              if ( ret.size() > 0) {
                  for ( int xx = 0; xx < ret.size(); xx++) {
                      movie.AddCast(0,ortus.util.string.decodeString(ret.get(xx)), "Writer", "");
                  }
              }

              ret = getTag(TAG.RUNTIME, "Runtime:",matcher.group(x));
              if ( ret.size() > 0) {
                  int runtime = 0;
                  String sruntime = ret.get(0).replaceAll("min","");
                  if ( sruntime.indexOf(":") > -1) {
                      String[] splitrun = sruntime.split(":");
                      sruntime = splitrun[1];
                  }

                  try { 
                      Integer.parseInt(sruntime.trim());
                  } catch(Exception e) {
                      runtime = 0;
                  }

                  movie.setRuntime(runtime);
              }

              ret = getTag(TAG.MPAA, "Certification:",matcher.group(x));
              if ( ret.size() > 0) {
                  movie.setCertification(ret.get(0));
              }
              ret = getTag(TAG.AHREF, "Genre:",matcher.group(x));
              if ( ret.size() > 0) {
                  for ( int xx = 0; xx < ret.size(); xx++) {
                      if ( ! ret.get(xx).equals("more"))
                        movie.AddGenre(ret.get(xx));
                  }
              }

              ret = getTag(TAG.DIVBR, "Also Known As:",matcher.group(x));
              if ( ret.size() > 0) {
                  movie.setAlternateName(ortus.util.string.decodeString(ret.get(0)));
              }

           }
        }
        pattern = Pattern.compile("\\<div.*?\\>(.*?)\\</div\\>");
        matcher = pattern.matcher(sb);

        while (matcher.find()) {
              ret = getTag(TAG.TD, "cast",matcher.group(0));
              String actor = null;
              String role = null;
              if ( ret.size() > 0) {
                  for ( int xx = 0; xx < ret.size(); xx++) {
                      if ( ret.get(xx).startsWith("<a href")) {
                          if ( ret.get(xx).contains("<img")) {
                              continue;
                          } else {
                            List<String> xyz = getAhref(ret.get(xx));
                            if ( actor == null)
                                actor = xyz.get(0);
                          }
                      } else if ( ret.get(xx).contains("..."))
                          continue;
                      else {
                          if ( actor == null)
                              actor = ret.get(xx);
                          if ( role == null)
                              role = ret.get(xx);
                      }
                      if ( actor != null && role != null) {
			  role.replaceAll("</td>","");
			  role.replaceAll("<td class=\"char\">","");
                          movie.AddCast(0,ortus.util.string.decodeString(actor), "Actor", role);
                          actor = null;
                          role = null;
                      }
                  }
              }
        }

        pattern = Pattern.compile("(?i)\\<h5\\>(.+?)/.+?\\</b\\>");
        matcher = pattern.matcher(sb);

        while (matcher.find()) {
              ret = getTag(TAG.B, "User Rating",matcher.group(1));
              if ( ret.size() > 0) {
                   movie.setRating(Integer.parseInt(ret.get(0)));
              }

        }

        pattern = Pattern.compile("(?i)\\<title\\>([^\\(]+)\\([^\\)]+\\)\\</title\\>");
        matcher = pattern.matcher(sb);

        if (matcher.find()) {
            movie.setName(ortus.util.string.decodeString(matcher.group(1)).trim());
        }

        ortus.api.DebugLog(LogLevel.Info, ProviderName + " : GetItemDetail Completed");

        return movie;
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
        
//        cleantitle = cleantitle.toLowerCase();

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
       
    private static List<String> getTag(TAG tt, String ss, String data) {
//        System.out.println("getTag: " + ss + " with " + data);
        List<String> result = new ArrayList<String>();
        Pattern datpat = Pattern.compile(".*" + ss + ".*");
        Matcher datmatch = datpat.matcher(data);

        if ( ! datmatch.matches())
            return new ArrayList<String>();
//        System.out.println("data: " + data);
        if ( tt == TAG.AHREF) {
            return getAhref(data);
        }
        if ( tt == TAG.TD) {
            return getTD(data);
        }
        if ( tt == TAG.RUNTIME) {
            return getRunTime(data);
        }
        if ( tt == TAG.MPAA) {
            return getMpaa(data);
        }
        if ( tt == TAG.DIV) {
            return getDiv(data);
        }
        if ( tt == TAG.DIVBR) {
            return getDivBr(data);
        }
        if ( tt == TAG.B) {
            return getB(data);
        }
        return new ArrayList<String>();
    }

    private static List<String> getTitleAhref(String val) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\<a href=\"(.*?)\".*?\\>(.*?)\\</a\\>");
        Matcher matcher = pattern.matcher(val);
        int findercount = 1;
        while (matcher.find()) {
            findercount++;
            if ( findercount > 1) {
                findercount = 0;
                continue;
            }
            for ( int x = 1; x <= matcher.groupCount(); x++) {
//                ortus.api.DebugLog(LogLevel.Trace2,"match: " + matcher.group(x));
                result.add(matcher.group(x));
            }
        }
        return result;

    }
  public static List<String> getDivBr(String val) {
        List<String> result = new ArrayList<String>();
	Pattern patternTag;
	Matcher matcherTag;

	String HTML_A_TAG_PATTERN = "(?i)\\<div[^>]+\\>(.+?)\\<br\\>";
        patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
        matcherTag = patternTag.matcher(val);

        while(matcherTag.find()){
          String linkText = matcherTag.group(1); //link text
          result.add(linkText);
        }

      return result;

    }
   public static List<String> getB(String val) {
        List<String> result = new ArrayList<String>();
	Pattern patternTag;
	Matcher matcherTag;

	String HTML_A_TAG_PATTERN = "(?i)\\<b\\>(.*)";
        patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
        matcherTag = patternTag.matcher(val);

        while(matcherTag.find()){
          String linkText = matcherTag.group(1); //link text
          result.add(linkText);
        }

      return result;

    }
    private static List<String> getAhref(String val) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\<a href.*?\\>(.*?)\\</a\\>");
        Matcher matcher = pattern.matcher(val);
        while (matcher.find()) {
            for ( int x = 1; x <= matcher.groupCount(); x++) {
                result.add(matcher.group(x));
            }
        }
        return result;

    }

    private static List<String> getDiv(String val) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\<div.*?\\>(.*?)\\<a.*?");
        Matcher matcher = pattern.matcher(val);
        while (matcher.find()) {
            for ( int x = 1; x <= matcher.groupCount(); x++) {
                result.add(matcher.group(x));
            }
        }
        return result;

    }

    private static List<String> getRunTime(String val) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\<div.*?\\>(.*?)\\|.*");
        Matcher matcher = pattern.matcher(val);
        while (matcher.find()) {
            for ( int x = 1; x <= matcher.groupCount(); x++) {
                result.add(matcher.group(x));
            }
        }
        return result;

    }

    private static List<String> getMpaa(String val) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\>USA:(.*?)\\<");
        Matcher matcher = pattern.matcher(val);
        while (matcher.find()) {
            for ( int x = 1; x <= matcher.groupCount(); x++) {
                result.add(matcher.group(x));
            }
        }
        return result;

    }

    public static List<String> getTD(String val) {
        List<String> result = new ArrayList<String>();
	Pattern patternTag;
	Matcher matcherTag;

	String HTML_A_TAG_PATTERN = "(?i)\\<td([^>]+)\\>(.+?)\\</td\\>";

        patternTag = Pattern.compile(HTML_A_TAG_PATTERN);

        matcherTag = patternTag.matcher(val);

        while(matcherTag.find()){
          String href = matcherTag.group(1); //href
          String linkText = matcherTag.group(2); //link text
//          System.out.println("td: " + linkText);
          result.add(linkText);
        }

      return result;

    }
}
