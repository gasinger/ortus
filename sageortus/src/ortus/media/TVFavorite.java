/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media;

import sagex.api.FavoriteAPI;

import java.lang.Integer;
/**
 *
 * @author Aaron
 */
public class TVFavorite {
	public static boolean IsFavoriteComskipped(Object fav){
		int favid = FavoriteAPI.GetFavoriteID(fav);

		return ortus.api.HasSageServerPropertyElement("ortus/comskip/FavoritesEnabled", Integer.toString(favid));

	}

	public static boolean IsMediaFileComskipped(Object mf){
		return IsFavoriteComskipped(FavoriteAPI.GetFavoriteForAiring(mf));
	}

}
