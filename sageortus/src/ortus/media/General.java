/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ortus.media;

import ortus.vars;


/**
 *
 * @author raZr sharpE
 */
public class General extends vars {

	public static String GetMediaTitleClean(Object MediaObject){
		return ortus.util.string.CleanString(ortus.api.GetMediaTitle(MediaObject));
	}

	public static String GetMediaTitleCleanPostpend(Object MediaObject) {
		return ortus.util.string.CleanStringPostpend(ortus.api.GetMediaTitle(MediaObject));
	}
}
