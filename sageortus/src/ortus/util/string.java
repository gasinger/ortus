/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Aaron
 */
public class string extends ortus.vars {
	public static String CleanString(String s1){
		return CleanString(s1, "");
	}

	public static String CleanStringPostpend(String s1){
		return CleanStringPostpend(s1, "");
	}

	public static String CleanStringExtreme(String s1){
		return CleanStringExtreme(s1,"");
	}
	

	private static boolean CleanStringMatch(String s1, String AddSep){
		String cleanregex = "(?i)^(a|an|the)[ " + AddSep + "]\\w.*";
		return s1.matches(cleanregex);
	}

	public static String CleanString(String s1, String AddSep) {

		boolean matchfound = CleanStringMatch(s1, AddSep);

		if (matchfound){
			String[] s1split = s1.split("[ "+AddSep+"]", 2);
			return s1split[1];
		} else{
			return s1;
		}
	}

	public static String CleanStringPostpend(String s1, String AddSep) {

		boolean matchfound = CleanStringMatch(s1, AddSep);

		if (matchfound){
			String[] s1split = s1.split("[ "+AddSep+"]", 2);
			return s1split[1]+", "+s1split[0];
		} else{
			return s1;
		}
	}

	public static String CleanStringExtreme(String s1, String AddSep) {
//		DebugLog(TRACE2, "ExtremeStringClean: starting with: <" + s1+">");

		s1 = s1.replaceAll("_WS", "");
		s1 = s1.replaceAll("_LB", "");
		s1 = s1.replaceAll("_43", "");
		s1 = s1.replaceAll("_16X9LB", "");
		s1 = s1.replaceAll("_169", "");
		s1 = s1.replaceAll("_4X3FF", "");
		s1 = s1.replaceAll("_4X3LB", "");
		s1 = s1.replaceAll("UNRATED", "");
		s1 = s1.replaceAll("RATED", "");

		s1 = CleanString(s1, "_."+AddSep);

		s1 = s1.toLowerCase();

		s1 = s1.replaceAll("\\.", "");
		s1 = s1.replaceAll("/", "");
		s1 = s1.replaceAll("\\?", "");
		s1 = s1.replaceAll("_", "");
		s1 = s1.replaceAll("-", " ");
		s1 = s1.replaceAll("'", "");
		s1 = s1.replaceAll("\"", "");
		s1 = s1.replaceAll(",", "");
		s1 = s1.replaceAll(":", "");
		s1 = s1.replaceAll("!", "");
		s1 = s1.replaceAll("&", "and");
		s1 = s1.replaceAll("\\s+", "").trim();

//		DebugLog(TRACE2, "ExtremeStringClean: returning: <" + s1 + ">");

		return s1;
	}

        public static String ScrubFileName(String filename) {
//            String s1 = filename.replaceAll("\\", " ");
//            s1 = s1.replaceAll("/"," ");
            String s1 = filename.replaceAll(":"," ");
            s1 = s1.replaceAll("\\*"," ");
            s1 = s1.replaceAll("\\?"," ");
//            s1 = s1.replaceAll("\""," ");
            s1 = s1.replaceAll("<"," ");
            s1 = s1.replaceAll(">"," ");
            s1 = s1.replaceAll("'","");
	    s1 = s1.replaceAll("\"","");
//	    s1 = s1.replaceAll(java.io.File.separator,"");
	    s1 = s1.replaceAll("/","");

            return s1.trim();
        }
        
	public static String decodeString(String x) {
//		DebugLog(TRACE2, "decodeString: parsing: " + x);
		Pattern pattern = Pattern.compile("&#x([^;]+);");
		Matcher matcher = pattern.matcher(x);
		StringBuffer sb = new StringBuffer(x.length());
		while (matcher.find()) {
			matcher.appendReplacement(sb, Matcher.quoteReplacement("%" + matcher.group(1)));
		}
                matcher.appendTail(sb);

		pattern = Pattern.compile("&#([^;]+);");
		matcher = pattern.matcher(sb);
		StringBuffer sb2 = new StringBuffer(sb.length());
		while (matcher.find()) {
			matcher.appendReplacement(sb2, Matcher.quoteReplacement("%" + String.format("%02X", Integer.parseInt(matcher.group(1)))));
		}
		matcher.appendTail(sb2);

		pattern = Pattern.compile("&amp([^;]+);");
		matcher = pattern.matcher(sb2);
		StringBuffer sb3 = new StringBuffer(sb2.length());
		while (matcher.find()) {
			matcher.appendReplacement(sb3, Matcher.quoteReplacement("%" + String.format("%02X", Integer.parseInt(matcher.group(1)))));
		}
		matcher.appendTail(sb3);

		String result;
		try {
			result = URLDecoder.decode(sb3.toString(),"UTF-8");
		} catch (Exception e) {
			result = sb3.toString();
		}

		result = result.replaceAll("&quot;","");
		result = result.replaceAll("IMDb -","");
		result = result.replaceAll("IMDb:","");
		result = result.replaceAll("IMDb","");
//		ortus.api.DebugLog(TRACE2,"decodeString: returning: " + result);
		return result.trim();
	}

        public static long getLong(Object val) {
            long result = 0;

            if ( val instanceof Long)
                return (Long)val;
            else if(val instanceof String) {
                try {
                    return Long.parseLong((String)val);
                } catch(Exception e) {}                                   
            }

            return 0;
        }

         public static int getInt(Object val) {
            int result = 0;

            if ( val instanceof Integer)
                return (Integer)val;
            if ( val instanceof Long)
                return ((Long)val).intValue();
            else if(val instanceof String) {
                try {
                    return Integer.parseInt((String)val);
                } catch(Exception e) {}
            }

            return 0;
        }
}
