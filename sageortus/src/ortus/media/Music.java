/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media;

import sagex.api.AlbumAPI;
/**
 *
 * @author Administrator
 */
public class Music {
	public static String GetAlbumArtistClean(Object MediaObject)
	{
		return ortus.util.string.CleanString(AlbumAPI.GetAlbumArtist(MediaObject));
	}

	// Scrubs "A " , "The" and "An" from start of AlbumName for better sorting/grouping.

	public static String GetAlbumNameClean(Object MediaObject)
	{
		return ortus.util.string.CleanString(AlbumAPI.GetAlbumName(MediaObject));
	}
}
