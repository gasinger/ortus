/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media;

import ortus.vars;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

/**
 *
 * @author jphipps
 */
public class CustomFilters extends vars {

    public static boolean IsVideoLibrary( Object MediaObject) {
        if ((MediaFileAPI.IsVideoFile(MediaObject) ||
             MediaFileAPI.IsDVD(MediaObject) ||
             MediaFileAPI.IsBluRay(MediaObject)) &&
             MediaFileAPI.IsLibraryFile(MediaObject) &&
             ! MediaFileAPI.IsTVFile(MediaObject))
             return true;
        else
            return false;
    }
    public static boolean IsGeneralTV( Object MediaObject) {
        if (ortus.api.IsTV(MediaObject))
             return true;
        else
            return false;
    }

    public static boolean IsImportedTV( Object MediaObject) {
        if ((MediaFileAPI.IsVideoFile(MediaObject) ||
            MediaFileAPI.IsDVD(MediaObject) ||
            MediaFileAPI.IsBluRay(MediaObject)) &&
            MediaFileAPI.IsLibraryFile(MediaObject) &&
            ortus.api.IsTV(MediaObject))
             return true;
        else
            return false;
    }

    public static String GetGenre( Object MediaObject) {
        return ShowAPI.GetShowCategory(MediaObject) +
               ShowAPI.GetShowSubCategory(MediaObject);
    }
    public static String GetActor( Object MediaObject) {
        return ShowAPI.GetPeopleInShow(MediaObject);
    }
    
    public static String GetDescription(Object MediaObject) {
        return ShowAPI.GetShowDescription(MediaObject);
    }

    public static String GetWriter(Object MediaObject)
        {return ShowAPI.GetPeopleInShowInRole(MediaObject,"Writer");
    }

    public static String GetDirector(Object MediaObject) {
        return ShowAPI.GetPeopleInShowInRole(MediaObject,"Director");
    }

}
