/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ortus.media;

/**
 *
 * @author raZr sharpE
 */
public class Movie
{

	/*
	 * returns the Disc Number as a string, "0" if it does not exist
	 *
	 * @param MediaObject, a sage Airing, Show, or MediaFile Object
	 */
	public static String GetDiscNumberStr(Object MediaObject) {
		return Integer.toString(ortus.api.GetDiscNumber(MediaObject));
	}

	/* returns the Season number as a formatted string with 0 Left padding, "00" if it does not exist
	 *
	 * @param MediaObject, a sage Airing, Show, or MediaFile Object
	 */
	public static String GetDiscNumberPad(Object MediaObject) {
		int dn = ortus.api.GetDiscNumber(MediaObject);

		if (dn == 0) {
			return "0";
		} else {
			return String.format("%02d", dn);
		}
	}
}
