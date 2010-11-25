/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Aaron
 */

package ortus.media;

import ortus.vars;

import sagex.api.PlaylistAPI;
import sagex.api.AiringAPI;
import sagex.api.Database;
import sagex.api.MediaFileAPI;

import java.util.Arrays;

public class util extends vars{
	/*
	 * A Sage Playlist object containing the airings in MediaObjects
	 * If NewPlaylistName already exists, it will be removed without prompt and recreated.
	 *
	 * @param MediaObjects, a sage MediaFile, Airing, or Show Object in an Array, list, or vector
	 * @param NewPlaylistName, string for the title of the new playlist
	 */
	public static Object MakePlaylist(Object MediaObjects, String NewPlaylistName) {
		ortus.api.DebugLog(LogLevel.Info, "ortus.api.MakePlaylist START");
		ortus.api.DebugLog(LogLevel.Trace, "Playlist Name: " + NewPlaylistName);
		Object[] AllPlaylists = PlaylistAPI.GetPlaylists();

		Object[] MediaObjectsArray = ortus.api.toArray(MediaObjects);

		for (Object TempPlaylist : AllPlaylists) {
			String TempPlaylistName = PlaylistAPI.GetName(TempPlaylist);
			ortus.api.DebugLog(LogLevel.Trace2, "TempPlaylist: '" + TempPlaylistName + "'");

			if (TempPlaylistName.equals(NewPlaylistName)) {
				ortus.api.DebugLog(LogLevel.Trace2, "Removing TempPlaylist: '" + TempPlaylistName + "'");
				PlaylistAPI.RemovePlaylist(TempPlaylist);
			}
		}

		ortus.api.DebugLog(LogLevel.Trace2, "Adding Playlist: '" + NewPlaylistName + "'");
		Object NewPlaylist = PlaylistAPI.AddPlaylist(NewPlaylistName);

		for (Object MediaObject : MediaObjectsArray) {
			ortus.api.DebugLog(LogLevel.Trace2, "Adding To Playlist: '" + ortus.api.GetMediaTitle(MediaObject) + "::" + ortus.api.GetMediaFileID(MediaObject) + "'");
			PlaylistAPI.AddToPlaylist(NewPlaylist, AiringAPI.GetMediaFileForAiring(MediaObject));
		}
		ortus.api.DebugLog(LogLevel.Info, "ortus.api.MakePlaylist END");
		return NewPlaylist;
	}

	public static Object[] GetLastMediaAdded(String SearchString, String MediaType, int size) {
		MediaType = MediaType.toLowerCase();
		Object[] MediaObjects = (Object[]) ortus.api.search(SearchString);
		if (MediaType.equals("tv") || MediaType.equals("series")) {
			MediaObjects = (Object[]) Database.Sort(MediaObjects, false, "ortus_api_GetOriginalAiringDate");
		}
		if (MediaType.equals("recordedtv") || MediaType.equals("tvmovies")) {
			MediaObjects = (Object[]) Database.SortLexical(MediaObjects, true, "GetAiringStartTime");
		}
		if (MediaType.equals("movies")) {
			MediaObjects = (Object[]) Database.SortLexical(MediaObjects, true, "GetFileStartTime");
		}

		if (MediaObjects.length < size) {
			return MediaObjects;
		} else {
			return Arrays.copyOfRange(MediaObjects, 0, size);
		}
	}

	public static void DeleteMediaFiles(Object mfs){
		Object[] mf_arr = ortus.api.toArray(mfs);

		for (Object mf: mf_arr){
			if (MediaFileAPI.IsFileCurrentlyRecording(mf)){
				AiringAPI.CancelRecord(mf);
				MediaFileAPI.DeleteFile(mf);
			} else {
				MediaFileAPI.DeleteFile(mf);
			}

		}
	}
}
