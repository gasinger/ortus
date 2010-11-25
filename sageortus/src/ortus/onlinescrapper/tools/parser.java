/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.tools;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author SBANTA
 */
public class parser {



public static ArrayList<Integer> GetEpisodeSeasonNumber(String name, int regexmethod){
//String test = "dexter_s01_e02.mkv";
String regex = null;
ArrayList<Integer> results = new  ArrayList<Integer>();
if (regexmethod==0){
regex = "(.*)[sS]([0-9][0-9]).*[eE]([0-9][0-9])";}

else if (regexmethod==1){
//        String test = "\\\\folder\\season01\\e02.fred";
regex = "(.*)season.*([0-9][0-9]).*[eE]([0-9][0-9])(.*)";}

else if (regexmethod==2){
   // String test = "\\\\folder\\season 1\\e02.fred";
regex = "(.*)season.*([0-9]).*[eE]([0-9][0-9])(.*)";}

else if (regexmethod==3){

      //  String test = "dexter 03x05.mkv";
        regex = "(.*)([0-9][0-9])x([0-9][0-9]).*";}
else if (regexmethod==4){
     // String test = "dexter 1x05.mkv";
    regex = "(.*)([0-9])x([0-9][0-9]).*";}






        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);


        while (matcher.find()) {
            
            for ( int x = 2; x <= matcher.groupCount();x++){
                
                results.add(java.lang.Integer.parseInt(matcher.group(x)));
            }
       }
        
        
        
        
        return results;
        
    }
       public static String CleanName(String title) {
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
	cleantitle = cleantitle.replaceAll("-","");

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

}
