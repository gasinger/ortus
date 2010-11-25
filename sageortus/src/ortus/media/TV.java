/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import sagex.api.Database;
import sagex.api.MediaFileAPI;
import sagex.api.AiringAPI;
import sagex.api.ShowAPI;
import sagex.api.Utility;
import sagex.api.FavoriteAPI;


/**
 *
 * @author raZr sharpE
 */
public class TV {

	public static String GetShowTitleClean(Object MediaObject) {
		return ortus.util.string.CleanString(ortus.api.GetShowTitle(MediaObject));
	}

	public static String GetShowTitleCleanPostpend(Object MediaObject) {
		return ortus.util.string.CleanStringPostpend(ortus.api.GetShowTitle(MediaObject));
	}

	public static String GetEpisodeTitleClean(Object MediaObject) {
		return ortus.util.string.CleanString(ortus.api.GetEpisodeTitle(MediaObject));
	}

	public static String GetEpisodeTitleCleanPostpend(Object MediaObject) {
		return ortus.util.string.CleanStringPostpend(ortus.api.GetEpisodeTitle(MediaObject));
	}

	public static String GetShowTitleOrMovie(Object MediaObject) {
		if (ortus.api.IsMovie(MediaObject)){
			return "Movies";
		}else{
			return ortus.api.GetShowTitle(MediaObject);
		}
	}

	/*
	 * Season Number as an string, "0" if it does not exist
	 *
	 * @param MediaObject, a sage Airing, Show, or MediaFile Object
	 */
	public static String GetSeasonNumberStr(Object MediaObject) {
	    return Integer.toString(ortus.api.GetSeasonNumber(MediaObject));
	 }

	/*
	 * Season number as a formatted string with 0 Left padding, "0" if it does not exist
     *
     * @param MediaObject, a sage Airing, Show, or MediaFile Object
     */
	public static String GetSeasonNumberPad(Object MediaObject) {
        int sn = ortus.api.GetSeasonNumber(MediaObject);

		if (sn == 0) {
            return "0";
        } else {
            return String.format("%02d", sn);
        }
    }

	/*
	 * Episode Number as a String, "0" if it does not exist
     *
     * @param MediaObject, a sage Airing, Show, or MediaFile Object
     */
	public static String GetEpisodeNumberStr(Object MediaObject)  {
        return Integer.toString(ortus.api.GetEpisodeNumber(MediaObject));
    }

	 /* Episode number as a formatted string with 0 Left padding, "0" if it does not exist
     *
     * @param MediaObject, a sage Airing, Show, or MediaFile Object
     */

    public static String GetEpisodeNumberPad(Object MediaObject){
        int en = ortus.api.GetEpisodeNumber(MediaObject);

		if (en == 0) {
            return "0";
        } else {
            return String.format("%02d", en);
        }
    }

	/*
	 * Episode Number as a String, "0" if it does not exist
     *
     * @param MediaObject, a sage Airing, Show, or MediaFile Object
     */
	public static String GetDiscNumberStr(Object MediaObject)  {
        return Integer.toString(ortus.api.GetDiscNumber(MediaObject));
    }

	 /* Episode number as a formatted string with 0 Left padding, "0" if it does not exist
     *
     * @param MediaObject, a sage Airing, Show, or MediaFile Object
     */

    public static String GetDiscNumberPad(Object MediaObject){
        int en = ortus.api.GetDiscNumber(MediaObject);

		if (en == 0) {
            return "0";
        } else {
            return String.format("%02d", en);
        }
    }

	 /*
     * SeasonEpisode Number as an integer
     * or 0 if Season Number does not exist
     * 101 (s01e01)
     * 201 (s02e01)
     * 1010 (s10e10)
     *
     * @param MediaObject, a sage Airing, Show, or MediaFile Object
     */
    public static int GetSeasonEpisodeNumber(Object MediaObject){
        int sn = ortus.api.GetSeasonNumber(MediaObject);
        int en = ortus.api.GetEpisodeNumber(MediaObject);

        if (sn == 0) {
            return 0;
        } else {
            return sn * 100 + en;
        }
    }

	 /*
     * SeasonDisc Number as an integer
     * or 0 if Season Number does not exist
     * 101 (s01d01)
     * 201 (s02d01)
     * 1010 (s10d10)
     *
     * @param MediaObject, a sage Airing, Show, or MediaFile Object
     */
    public static int GetSeasonDiscNumber(Object MediaObject){
        int sn = ortus.api.GetSeasonNumber(MediaObject);
        int dn = ortus.api.GetDiscNumber(MediaObject);

        if (sn == 0) {
            return 0;
        } else {
            return sn * 100 + dn;
        }
    }




	/*
	 * returns true if the MediaObjects  the IsWatched() flag is false and it has been partially watched.
	 *
	 * @param MediaObject, a sage MediaFile, Airing, or Show object
	 */

	public static boolean IsWatchedPartial(Object MediaObject) {
		if (!AiringAPI.IsWatched(MediaObject) && (AiringAPI.GetWatchedDuration(MediaObject) != 0)) {
			return true;
		} else {
			return false;
		}
	}
	/*
	 * Given an array of Airings will return the last watched (in real time) object.
	 *
	 * @param MediaObjects - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
	 */

	public static Object GetLastWatched(Object MediaObjects) {
		MediaObjects = Database.Sort(MediaObjects, true, "GetRealWatchedStartTime");
		return Utility.GetElement(MediaObjects, 0);
	}
	/*
	 * Given an array of Airings will return the next episode to watch by AiringDate (in real time).
	 * Returns Null if Airings not found after last watched. (end of series)
	 *
	 * @param MediaObjects - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
	 */

	public static Object GetNextShow(Object MediaObjects) {
		Object LastWatched = GetLastWatched(MediaObjects);
		if (AiringAPI.IsWatched(LastWatched)) {
			MediaObjects = Database.Sort(MediaObjects, false, "flux_api_GetOriginalAiringDate");
			int index = Utility.FindElementIndex(MediaObjects, LastWatched);

			if (index >= Utility.Size(MediaObjects)) {
				return null;
			} else {
				return Utility.GetElement(MediaObjects, index + 1);
			}
		} else {
			return LastWatched;
		}
	}

	/*
	 * given an array of Airings will return a subarray sorted by original airing date
	 * where the first element of the array is the last watched episode
	 * (or the next episode if IsWatched() = true).
	 * Subsequent elements are all "later" episodes (as defined by flux_api_GetOriginalAiringDate).
	 * Episodes "before" the last watched are truncated (again as defined by flux_api_GetOriginalAiringDate).
	 *
	 * @param Arr - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
	 * Presumably grouped into AiringTitle and/or a filtered by a specific season.
	 */
	public static Object[] GetShowsFromLastWatched(Object Arr) {
		Object NextWatch = GetNextShow(Arr);
		if (NextWatch == null) {
			return null;
		} else {
			Arr = Database.Sort(Arr, false, "flux_api_GetOriginalAiringDate");
			Object[] Arr0 = ortus.api.toArray(Arr);
			int elementlocation = Utility.FindElementIndex(Arr0, NextWatch);

			return Arrays.copyOfRange(Arr0, elementlocation, Arr0.length);
		}
	}

	/*
	 * Given an array of Airings and a member of Airing will return the remaining shows
	 * (1st element = the passed Airing)
	 * with  in array after current show.
	 *
	 * Returns Null if Airings not found after current show. (end of series)
	 * @param MediaObjects - sage MediaFiles, Airings, or Shows Objects in an Array, list, or vector.
	 * @param MediaObject=sage object of current show to start from
	 */
	public static Object[] GetShowsFromShow(Object MediaObjects, Object MediaObject) {
		MediaObjects = Database.Sort(MediaObjects, false, "flux_api_GetOriginalAiringDate");
		int elementlocation = Utility.FindElementIndex(MediaObjects, MediaObject);
		Object[] Arr0 = ortus.api.toArray(MediaObjects);

		if (elementlocation > Arr0.length) {
			return null;
		} else {
			return Arrays.copyOfRange(Arr0, elementlocation, Arr0.length);
		}
	}

	public static void SetWatched(Object mfs)
	{
		Object[] mf_arr = ortus.api.toArray(mfs);

		for (Object mf: mf_arr){
			AiringAPI.SetWatched(mf);
		}
	}

	public static void ClearWatched(Object mfs)
	{
		Object[] mf_arr = ortus.api.toArray(mfs);

		for (Object mf: mf_arr){
			AiringAPI.ClearWatched(mf);
		}
	}

	public static Object[] GetWatchedMediaFiles(Object mfs)
	{
		if (mfs == null){
			return new Object[0];
		}

		Object[] mf_arr = ortus.api.toArray(mfs);
		ArrayList WatchedList = new ArrayList<Object>();

		for (Object mf: mf_arr){
			if (AiringAPI.IsWatched(mf)){
				WatchedList.add(mf);
			}
		}

		return WatchedList.toArray();

	}

	public static int GetWatchedCount(Object mfs)
	{
		if (mfs == null){
			return 0;
		}
		Object[] mf_arr = ortus.api.toArray(mfs);
		int count = 0;

		for (Object mf: mf_arr){
			if (AiringAPI.IsWatched(mf)){
				count++;
			}
		}

		return count;
	}

	public static void SetDontLike(Object mfs)
	{
		Object[] mf_arr = ortus.api.toArray(mfs);

		for (Object mf: mf_arr){
			AiringAPI.SetDontLike(mf);
		}
	}

	public static void ClearDontLike(Object mfs)
	{
		Object[] mf_arr = ortus.api.toArray(mfs);

		for (Object mf: mf_arr){
			AiringAPI.ClearDontLike(mf);
		}
	}

	public static int GetDontLikeCount(Object mfs)
	{
		if (mfs == null){
			return 0;
		}

		Object[] mf_arr = ortus.api.toArray(mfs);
		int count = 0;

		for (Object mf: mf_arr){
			if (AiringAPI.IsDontLike(mf)){
				count++;
			}
		}

		return count;
	}

	public static void Archive(Object mfs)
	{
		Object[] mf_arr = ortus.api.toArray(mfs);

		for (Object mf: mf_arr){
			MediaFileAPI.MoveFileToLibrary(mf);
		}
	}

	public static void Unarchive(Object mfs)
	{
		Object[] mf_arr = ortus.api.toArray(mfs);

		for (Object mf: mf_arr){
			MediaFileAPI.MoveTVFileOutOfLibrary(mf);
		}
	}

	public static int GetArchiveCount(Object mfs)
	{
		if (mfs == null){
			return 0;
		}
		Object[] mf_arr = ortus.api.toArray(mfs);
		int count = 0;

		for (Object mf: mf_arr){
			if (MediaFileAPI.IsLibraryFile(mf)){
				count++;
			}
		}

		return count;
	}
}
