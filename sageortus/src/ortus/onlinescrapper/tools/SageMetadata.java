/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.onlinescrapper.tools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import ortus.configurationEngine;
import ortus.onlinescrapper.MediaObject;
import ortus.onlinescrapper.themoviedb.CastItem;
import ortus.vars.LogLevel;
import sagex.api.AiringAPI;
import sagex.api.Configuration;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

/**
 *
 * @author jphipps
 */
public class SageMetadata {

    public static boolean createShow(MediaObject mo) {
        boolean retcode = false;

        if ( Configuration.GetServerProperty("ortus/metadata/wizupdate","false").equalsIgnoreCase("false"))
            return false;

        Object existingAiring = MediaFileAPI.GetMediaFileAiring(mo.getMedia());
        Object existingShow = AiringAPI.GetShow(existingAiring);
        String ShowPrefix = ShowAPI.GetShowExternalID(existingShow).substring(0,2);

        ortus.api.DebugLog(LogLevel.Trace,"createShow: Replacing show w/ExternalID: " + ShowAPI.GetShowExternalID(existingShow));

        ortus.api.DebugLog(LogLevel.Trace, "createShow: existingShow: " + existingShow);
        
        String Title = mo.getShowtitle();
        String Episode = mo.getShowtitle();
        if ( mo.getEpisodetitle() != null)
            if ( ! mo.getEpisodetitle().isEmpty() )
                Episode = mo.getEpisodetitle();
        String Description = "";
        if ( mo.getOverview() != null)
            Description = mo.getOverview().replaceAll("\n"," ");
        long Duration = ShowAPI.GetShowDuration(existingShow);
        String Category = ShowAPI.GetShowCategory(existingShow);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Orig Category: " + Category);
        if ( mo.getGenres().size()>0)
            Category = mo.getGenres().get(0);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Result Category: " + Category);
        String SubCategory = ShowAPI.GetShowSubCategory(existingShow);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Orig SubCategory: " + SubCategory);
        if ( mo.getGenres().size() > 1)
            SubCategory = mo.getGenres().get(1);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Result SubCategory: " + SubCategory);
        List<String> PeopleList = new ArrayList<String>();
        List<String> RoleList = new ArrayList<String>();
        if ( mo.getCast().size()>0) {
            for( CastItem ci : mo.getCast()) {
                if ( ci.GetJob().equalsIgnoreCase("director")) {
                    PeopleList.add(ci.GetName());
                    RoleList.add("Director");
                    ortus.api.DebugLog(LogLevel.Trace, "createShow: Add Director : " + ci.GetName());
                } else if ( ci.GetJob().equalsIgnoreCase("writer")) {
                    PeopleList.add(ci.GetName());
                    RoleList.add("Writer");
                    ortus.api.DebugLog(LogLevel.Trace, "createShow: Add Writer : " + ci.GetName());
                } else {
                    PeopleList.add(ci.GetName());
                    RoleList.add("Actor");
                    ortus.api.DebugLog(LogLevel.Trace, "createShow: Add Actor : " + ci.GetName() + " Role: " + ci.GetCharacter());
                }                
            }
        }
        String Rated = ShowAPI.GetShowRated(existingShow);
        if ( mo.getRated() != null)
            Rated = mo.getRated();
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Rated: " + Rated);
        String[] ExpandedRatingsList = new String[] { };
        String Year = mo.getReleasedate();
        if ( Year == null)
            Year="";
        if ( ! Year.isEmpty())
            Year = mo.getReleasedate().substring(0,4);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Year: " + Year);
        String ParentalRating = ShowAPI.GetShowParentalRating(existingShow);
        String MiscList[] = new String[] { } ;
        String Language = ShowAPI.GetShowLanguage(existingShow);
        long OriginalAirDate = ShowAPI.GetOriginalAiringDate(existingShow);

        if ( ShowPrefix.equalsIgnoreCase("MO") ||
             ShowPrefix.equalsIgnoreCase("EP"))
            ShowPrefix="MF";
        String ExternalID = ShowPrefix + "OR";
//        if ( mo.isMediaTypeMovie())
//            ExternalID = "MVOR";
        if ( Configuration.GetServerProperty("ortus/metadata/wizepisode","false").equalsIgnoreCase("true")) {
            if ( mo.isMediaTypeSeries())
                ExternalID = "EPOR";
            if ( mo.isMediaTypeRecording())
                ExternalID = "SHOR";
        }

        String ExternalIDnum = null;
        while ( ExternalIDnum == null) {
            String wid = UUID.randomUUID().toString().replaceAll("-","").substring(0,6).toUpperCase();
            if ( ShowAPI.GetShowForExternalID(wid) == null)
                ExternalIDnum = wid;
        }

        ExternalID+=ExternalIDnum;

        ortus.api.DebugLog(LogLevel.Trace, "createShow: Creating show with externalid : " + ExternalID);
        Object newShow = null;
        try {
            newShow = ShowAPI.AddShow(Title,false,Episode,Description,Duration,Category,SubCategory,PeopleList.toArray(new String[PeopleList.size()]),RoleList.toArray(new String[RoleList.size()]),Rated,ExpandedRatingsList,Year,ParentalRating,MiscList,ExternalID,Language,OriginalAirDate);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if ( newShow == null) {
            ortus.api.DebugLog(LogLevel.Error, "createShow: Failed to create show");
            return false;
        }

        ortus.api.DebugLog(LogLevel.Trace, "createShow: newShow: " + newShow);

        MediaFileAPI.SetMediaFileShow(mo.getMedia(), newShow);

//        Object newAiring = MediaFileAPI.GetMediaFileAiring(mo.getMedia());

//        if (existingAiring!=null && newAiring!=null) {
//            if (AiringAPI.IsDontLike(existingAiring)) {
//                AiringAPI.SetDontLike(newAiring);
//            }
//
//            AiringAPI.SetRecordingName(newAiring, AiringAPI.GetRecordingName(existingAiring));
//            AiringAPI.SetRecordingQuality(newAiring, AiringAPI.GetRecordingQuality(existingAiring));
//            AiringAPI.SetRecordingTimes(newAiring, AiringAPI.GetAiringStartTime(existingAiring), AiringAPI.GetAiringEndTime(existingAiring));
//
//            if (AiringAPI.IsWatched(existingAiring)) {
//                AiringAPI.SetWatched(newAiring);
//            }
//        }
        return retcode;
    }

    public static boolean BackupWiz() {
        String wizbinfilename = System.getProperty("user.dir") + java.io.File.separator + "Wiz.bin";
        String BackupDir = configurationEngine.getInstance().getBasePath() + java.io.File.separator + "WIZ";
        String filename = "Wiz." + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        File bd = new File(BackupDir);
        if ( !bd.exists() )
            bd.mkdirs();
        try {
            FileUtils.copyFile(new File(wizbinfilename), new File(bd.getAbsolutePath() + java.io.File.separator + filename));
        } catch (IOException ex) {
            ortus.api.DebugLog(LogLevel.Error, "BackupWiz: Exception: " + ex);
            return false;
        }
        ortus.api.DebugLog(LogLevel.Info, "BackupWiz: Wiz.bin backup to " + filename + "was successful");
        return true;        
    }
}
