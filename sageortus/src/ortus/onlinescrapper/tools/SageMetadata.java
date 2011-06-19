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
import ortus.Ortus;
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
        
        String Title = ShowAPI.GetShowTitle(mo.getMedia());
        if ( ! mo.getShowtitle().isEmpty())
            Title = mo.getShowtitle();

        String Episode = ShowAPI.GetShowEpisode(mo.getMedia());
        if ( ! mo.getEpisodetitle().isEmpty())
            Episode = mo.getEpisodetitle();
        else
            if ( Episode.isEmpty())
                Episode = Title;

        ortus.api.DebugLog(LogLevel.Trace, "createShow: Title: " + Title);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Episode: " + Episode);

        String Description = ShowAPI.GetShowDescription(mo.getMedia());
        if ( ! mo.getOverview().isEmpty())
            Description = mo.getOverview().replaceAll("\n"," ");
        Description = Description.replaceAll("''","'");
        long Duration = ShowAPI.GetShowDuration(existingShow);

        String Category = ShowAPI.GetShowCategory(existingShow);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Orig Category: " + Category);
        if ( mo.getSageCategory() != null)
            Category = mo.getSageCategory();
        else if ( mo.getGenres().size()>0)
            Category = mo.getGenres().get(0);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Result Category: " + Category);
        String SubCategory = ShowAPI.GetShowSubCategory(existingShow);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Orig SubCategory: " + SubCategory);
        if ( mo.getSageSubCategory() != null)
            SubCategory = mo.getSageSubCategory();
        else if ( mo.getGenres().size() > 1)
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

        String Rated = ShowAPI.GetShowRated(mo.getMedia());
        if ( ! mo.getRated().isEmpty())
            Rated = mo.getRated();
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Rated: " + Rated);

        String[] ExpandedRatingsList = new String[] { };

        String Year = ShowAPI.GetShowYear(mo.getMedia());
        if ( ! mo.getReleasedate().isEmpty() &&
             ! mo.getReleasedate().equalsIgnoreCase("1970-01-01"))
            Year = mo.getReleasedate().substring(0,4);
        ortus.api.DebugLog(LogLevel.Trace, "createShow: Year: " + Year);

        String ParentalRating = ShowAPI.GetShowParentalRating(existingShow);

        Boolean IsFirstRun = ShowAPI.IsShowFirstRun(existingShow);

        String MiscList[] = new String[] { } ;

        String Language = ShowAPI.GetShowLanguage(existingShow);

        long OriginalAirDate = ShowAPI.GetOriginalAiringDate(existingShow);

//        if ( ShowPrefix.equalsIgnoreCase("MO") ||
//             ShowPrefix.equalsIgnoreCase("EP"))
//            ShowPrefix="MF";
//        String ExternalID = ShowPrefix + "OR";
        String ExternalID = ShowPrefix;

        if ( mo.isConvertSageType()) {
             if ( ShowPrefix.equalsIgnoreCase("SH") ||
                  ShowPrefix.equalsIgnoreCase("EP")) {
                 ExternalID = "MO";
            } else {
                 ExternalID = "SH";
            }
        } else {
//        if ( mo.isMediaTypeMovie())
//            ExternalID = "MVOR";
            if ( Configuration.GetServerProperty("ortus/metadata/wizepisode","false").equalsIgnoreCase("true")) {
                if ( mo.isMediaTypeSeries())
                    ExternalID = "EP";
                if ( mo.isMediaTypeRecording())
                    ExternalID = "SH";
            }
        }

        String ExternalIDnum = null;
        while ( ExternalIDnum == null) {
            String wid = UUID.randomUUID().toString().replaceAll("-","").substring(0,8).toUpperCase();
            if ( ShowAPI.GetShowForExternalID(wid) == null)
                ExternalIDnum = wid;
        }

        ExternalID+=ExternalIDnum;

        ortus.api.DebugLog(LogLevel.Trace, "createShow: Creating show with externalid : " + ExternalID);
        Object newShow = null;
        try {
            newShow = ShowAPI.AddShow(Title,IsFirstRun,Episode,Description,Duration,Category,SubCategory,PeopleList.toArray(new String[PeopleList.size()]),RoleList.toArray(new String[RoleList.size()]),Rated,ExpandedRatingsList,Year,ParentalRating,MiscList,ExternalID,Language,OriginalAirDate);
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
        String BackupDir = Configuration.GetServerProperty("ortus/backup/folder", Ortus.getInstance().getBasePath() + java.io.File.separator + "backups");
        String filename = "wiz-backup." + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
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
